# UCH Suite — Hibernate 4 → Hibernate 5 Migration Impact Assessment

**Date:** 2026-06-28
**Baseline branch:** `upgrade/spring4-hibernate4` (Spring 4.1.9 / Hibernate 4.1.12, all 380 tests passing)
**Type:** Analysis only — no implementation changes.
**Target evaluated:** Hibernate **5.2.18.Final** (last 5.2.x). Where 5.3+/5.4+ differ materially, it is called out explicitly, because several "safe in 5.2" items become breaking in 5.3.

All counts below were measured directly against the baseline branch, not estimated.

---

## 1. Executive Summary

**Overall difficulty: MEDIUM-LOW** (target 5.2) — significantly easier than the H3→H4 migration just completed.

**Estimated effort: 2–4 working days** (core migration 1–2 days, verification 1–2 days), assuming the H4 baseline is merged and stable first.

**Main blockers:**

1. **Spring version prerequisite (hard blocker):** `org.springframework.orm.hibernate5.*` (LocalSessionFactoryBean, HibernateTransactionManager, HibernateDaoSupport, HibernateTemplate) only exists from **Spring 4.2+**. The current branch is on Spring 4.1.9. A minor Spring bump (4.1.9 → 4.3.30, same major line) is unavoidable. This is out of scope for this analysis per the instructions, but it cannot be waved away: Hibernate 5 is not adoptable on Spring 4.1 at all.
2. **29 JDBC-style positional-parameter HQL queries** (`where a.Id = ?` via `HibernateTemplate.find(hql, args)`). Deprecated in 5.2 (the deprecation warnings are *already visible* in today's H4.1 test logs), **removed in 5.3**. Safe for a 5.2 target; a hard stop for anything later. These should be converted to named parameters regardless — cheap, mechanical, and removes a known future landmine.
3. No other hard blockers found. The Criteria API — the single largest exposure (295 call sites) — compiles unchanged on 5.2 (deprecated, not removed until Hibernate 6).

**Classification: MEDIUM impact** — high *volume* of touched files (mechanical package renames across ~57 files), low *semantic* risk (no transaction-model change comparable to H3→H4).

**Recommendation: Yes — phased migration, target 5.2.18** (details in §6).

**On the stated motivation (reducing cast-heavy code):** partially achievable — be precise about expectations. Hibernate 5.2 introduces **typed queries** (`session.createQuery(hql, MyEntity.class)` returning `Query<MyEntity>`), which genuinely eliminates casts for the HQL paths (46 call sites). However, the *bulk* of the casts live on the legacy Criteria path (140 `findByCriteria` + Criteria `.list()` calls), and those signatures return raw `List` in **every** Hibernate 5.x release — the casts there do not go away until the queries are rewritten to typed HQL or JPA Criteria, which is a separate refactoring initiative, not a version bump. Hibernate 5 *enables* the debt reduction; it does not *deliver* it by itself.

---

## 2. Affected Components

### Configuration (3 files)
| File | Change |
|---|---|
| `pom.xml` | `hibernate.version` 4.1.12 → 5.2.18; `jasypt-hibernate4` → `jasypt-hibernate5`; re-verify `com.mchange:c3p0` pin against `hibernate-c3p0:5.2.18`'s transitive (the ancient `c3p0:c3p0:0.9.1` coordinate problem recurs in the 5.x line); Spring 4.1.9 → 4.3.30 (prerequisite) |
| `applicationContext-hibernate.xml` | `orm.hibernate4.LocalSessionFactoryBean` → `orm.hibernate5.*`; same for `HibernateTransactionManager`. `entityInterceptor` (AuditInterceptor) wiring unchanged. 84 `mappingResources` entries unchanged (hbm.xml fully supported in 5.x) |
| `hibernate.properties` | `hibernate.dialect=org.hibernate.dialect.MySQLDialect` — still valid in 5.2 for MySQL 5.6. `hibernate.jdbc.batch_size`, `show_sql`, `use_second_level_cache=false`, `use_reflection_optimizer`, `jdbc.lob.non_contextual_creation` — all still recognized. No key removals detected |

### DAO layer (56 impl files + 1 test-support DAO)
- All 56 `HS*DAOImpl` extend `org.springframework.orm.hibernate4.support.HibernateDaoSupport` → package rename to `orm.hibernate5.support` (pure sed, identical to the H3→H4 pass).
- `HSTestSupportDao` (test tree): same rename + re-verify its two H4-era workarounds (`setCheckWriteOperations(false)`, explicit `session.flush()` after object-level deletes) still behave identically. No semantic change expected — Spring's `HibernateTemplate` behavior, not Hibernate's, governs both — but verify empirically (two consecutive full-suite runs, no manual DB intervention, per the H4 idempotency check).

### Services (minimal exposure — good layering)
- Only **1 non-DAO production class touches `org.hibernate.*` directly**: `AuditInterceptor` (extends `EmptyInterceptor`, uses `org.hibernate.type.Type`). Both survive all of Hibernate 5 unchanged (removed in 6). Zero service-layer impact — the `@Transactional` services (5 classes) and all JSF beans sit entirely behind DAO interfaces. This is Dependency Inversion paying off measurably: the ORM swap is invisible above the DAO layer.

### Utility/test classes
- 1 test file imports `org.hibernate.*` (`HSTestSupportDao`). Test infrastructure otherwise Hibernate-agnostic.

### Explicitly NOT affected
- JSF 1.2 / RichFaces 3.3 pages and managed beans (zero Hibernate imports).
- The 84 `.hbm.xml` mappings (format unchanged in 5.x; one caveat: the jasypt `EncryptedStringType` literal class name in `TbUsuario.hbm.xml` needs the `hibernate4`→`hibernate5` string swap — this exact file was missed by automated import fixes in the H3→H4 pass too).
- The 4 stored-procedure named queries (`<sql-query callable="true">` in FConceptosBanco/FConvalidacionCursos/FDataBanco/FGenerarCodigo hbm files) — callable named queries remain supported in 5.2.

---

## 3. Main Breaking Changes (H4.1 → H5.2)

Ordered by real risk to this codebase:

1. **JDBC-style positional parameters in HQL — deprecated now, removed in 5.3.**
   29 queries across the DAOs (e.g. `HSAlumnoDAOImpl:42`, `HSMatriculaDAOImpl:320`, `HSSaldoDAOImpl:31`). On 5.2 they run with warnings; on 5.3+ they throw. **This is the one item that dictates the 5.2 ceiling.** Conversion to named parameters is mechanical (~1–2h for all 29) and should be done either as pre-work on the H4 baseline (testable today, zero risk) or inside the migration commit.

2. **Spring ORM package rename (via the Spring 4.3 prerequisite).**
   Not a Hibernate break per se, but the largest file-count change: ~57 files. Mechanical, zero semantic risk, fully covered by compile verification.

3. **`Query` interface moves + generics (5.2).**
   `org.hibernate.Query` is deprecated in favor of `org.hibernate.query.Query<R>`; `createSQLQuery` → `createNativeQuery`. Old forms still work in all of 5.x. The 49 deprecated setter calls (`setString`/`setInteger`/`setDate`/`setParameterList`) keep working (removed only in 6). **No forced change** — but this is exactly where the cast-reduction opportunity lives (see §5).

4. **Criteria API deprecation (5.2) — warnings only.**
   295 call sites (`createCriteria`/`DetachedCriteria`), 537 `Restrictions`, 34 `Projections`, 51 `Order`. All compile and run unchanged through 5.x; removed in Hibernate 6. The deprecation warnings will be *loud* (hundreds of them) — plan to suppress or accept the noise, and treat Criteria→typed-query conversion as a tracked, separate debt item.

5. **Transaction/session semantics: no change of the H3→H4 magnitude.**
   Spring's `SpringSessionContext` model carries over. The three silent-breakage categories from the H4 migration (no-transaction DAOs, out-of-transaction write blocking, unflushed object deletes) are governed by *Spring's* HibernateTemplate, not Hibernate's version — expected stable, verify empirically anyway.

6. **What does NOT apply at 5.2** (commonly cited for "Hibernate 5" generally):
   - Bytecode provider switch to Bytebuddy — that default lands in **5.4**, not 5.2 (5.2 keeps Javassist). Irrelevant for this target.
   - `hibernate.id.new_generator_mappings` default flip to `true` — changed in **5.0**; this codebase uses `native`/`identity` id generation in hbm.xml against MySQL auto-increment, which maps identically either way. Verify one insert path in testing; expected non-issue.
   - HQL parser rewrite — Hibernate 6.

---

## 4. Complexity Analysis

| Area | Complexity | Notes |
|---|---|---|
| Configuration | **Low** | 3 files; package renames + dependency swaps; all current property keys still valid; c3p0 pin re-applied proactively (known trap from H3→H4) |
| Session API | **Low–Medium** | 339 `getHibernateTemplate()` calls unchanged (Spring API); 33 `getCurrentSession()` + 6 `openSession()` unchanged; deprecation warnings on Query/SQLQuery paths, no forced rewrites at 5.2 |
| Criteria API | **Low (at 5.2) / High (deferred, for Hibernate 6)** | 295 call sites compile unchanged; deprecated not removed. The H6 rewrite cost is real but explicitly out of this migration's scope — track as debt, do not bundle |
| HQL | **Medium** | 29 positional-parameter queries are the single genuine must-fix (mechanical); 46 `createQuery` + 7 `createSQLQuery` otherwise compatible; only 5 dynamic-concatenation HQL sites (low volume); stricter 5.x alias/validation checks expected to pass given the suite already runs clean on 4.1's stricter-than-3.6 parser |
| DAO Layer | **Medium (volume) / Low (risk)** | 56 files touched, but ~95% is the identical mechanical rename already executed once in H3→H4; the DAO-behind-interface pattern means zero call-site changes above the DAO layer |

---

## 5. Estimated Refactoring Scope

**Mandatory (the migration itself):**
- **~57 classes** touched (56 DAO impls + test-support DAO): package rename only.
- **29 queries** converted from positional to named parameters (mandatory in spirit — technically optional at 5.2, but doing it later costs the same and blocks 5.3+).
- **3 config files** + 1 hbm.xml literal (jasypt type).
- **0 service classes, 0 JSF beans, 0 JSP pages.**
- **Risky modules: 2** — `banco` (stored-procedure named queries + BLOB handling, the c3p0 `AbstractMethodError` from H3→H4 originated here) and `administrativa` payments (widest transaction-boundary surface). Both have targeted test coverage from the H4 stabilization; re-run their suites first.

**Optional (the actual debt reduction — separate initiative, enabled but not forced by 5.2):**
- **46 HQL call sites** convertible to typed `Query<T>` — this removes their casts properly (not suppresses them). Highest-value, lowest-risk modernization; can be done incrementally file-by-file *after* the migration is green.
- **140 `findByCriteria` casts + ~199 `@SuppressWarnings`**: unchanged by any Hibernate 5.x version. Options: (a) centralize the cast in one generic helper (~1 day, cosmetic but honest — one well-named unsafe cast instead of 193 scattered ones), or (b) progressive rewrite to typed HQL/JPA Criteria (weeks, and the real prerequisite for Hibernate 6 someday). Recommend (a) now, (b) as a standing background task.

**Not in scope, confirmed unaffected:** Tomcat, Java version, Jakarta namespace, frontend.

---

## 6. Final Recommendation

**Yes — but phased migration recommended.** Target **Hibernate 5.2.18**, not 5.3+.

Phasing:

1. **Pre-work on the current H4 baseline (before any version bump):** convert the 29 positional-parameter queries to named parameters. Fully testable against the passing suite today; shrinks the migration diff; removes the only true 5.x-line landmine. (~2h)
2. **Core migration commit:** Spring 4.3.30 + Hibernate 5.2.18 + package renames + jasypt/c3p0 swaps. One atomic commit, same playbook as H3→H4. (~1 day)
3. **Verification:** full suite **twice consecutively with no manual DB intervention** (the idempotency check that caught the flush bug), targeted re-runs of `banco`/`administrativa`, one manual JSF smoke pass. (~1 day)
4. **Post-migration, separate PRs:** typed-query conversion for the 46 HQL sites; centralized cast helper for the Criteria sites. This is where the "reduce casts / modernize query handling" motivation is actually satisfied — deliberately decoupled from the version bump so a regression in either is trivially attributable.

**Why not "Yes, low-risk" outright:** the volume (57 files), the Spring prerequisite, and this codebase's demonstrated history of *silent* semantics-dependent behavior (three separate categories surfaced during H3→H4) justify the phased ceremony even though no individual step is hard. **Why not "No":** every identified cost is bounded, mechanical, and already rehearsed once; test coverage is the strongest it has ever been; and staying on 4.1 — an EOL release nothing else in the ecosystem targets — accrues risk faster than the migration costs.

Consistent with the stated principles: business continuity first (5.2 not 5.3+, Criteria untouched, one-variable-at-a-time), KISS (no JPA/EntityManager conversion bundled in), minimal refactor (debt reduction split into its own reversible PRs).

---

**Owner:** Claude Code + César
**Method:** static analysis of `upgrade/spring4-hibernate4` @ HEAD; all counts from grep against production sources (`src/main/java`), test tree counted separately.
**Companion doc:** `HIBERNATE5_MIGRATION_PLAN.md` (execution playbook for phases 2–3 above).
