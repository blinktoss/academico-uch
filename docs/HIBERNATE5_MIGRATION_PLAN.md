# Spring 4.1 → 4.3 + Hibernate 4.1 → 5.4 Migration Plan

**Date:** 2026-06-28 · **Executed:** 2026-07-05 on branch `upgrade/hibernate5`
**Status:** EXECUTED. Target raised from the originally planned 5.2.18 to **5.4.24.Final** (maintainer decision). Suite green twice consecutively (393/393), no manual DB intervention between runs. See "Execution results (2026-07-05)" at the end for what 5.4 actually required beyond this plan.
**Purpose:** Training exercise — design patterns and migration discipline, not a production deadline.
**Scope (as executed):** Spring 4.1.9 → 4.3.30 (last Spring 4.x; 4.3.17+ carries the Hibernate 5.3/5.4 compat fixes), Hibernate 4.1.12 → 5.4.24

---

## Why this baseline, why not further

- **Spring 4.3, not 5.x:** `org.springframework.orm.hibernate5` requires Spring **4.2+**, so a Spring bump is mandatory here too — but jumping straight to Spring 5 would also mean dropping `HibernateTemplate`/`HibernateDaoSupport` support windows and revisiting Servlet API requirements again. Staying on the last Spring 4.x release keeps this phase isolated to "adopt Hibernate 5" instead of compounding it with a second Spring-major jump.
- **Hibernate 5.2, not 5.0/5.1 or 5.3+:** `org.hibernate.Criteria` / `DetachedCriteria` are deprecated starting **5.2** but not removed until **Hibernate 6**. That means the 193 Criteria call sites from the last migration keep compiling as-is (deprecation warnings, no rewrite forced) — same reasoning as choosing 4.1 over 4.3/5.3 last time: modern enough to be a real step forward, conservative enough not to force an unrelated rewrite (JPA Criteria API) in the same phase.

---

## Lessons carried forward from the Spring 4.1/Hibernate 4.1 migration

These aren't hypothetical — they're exactly the three categories of *silent* breakage that took the most debugging time last phase, and the same class of risk reappears any time session/transaction semantics tighten further:

1. **DAOs with no Spring transaction at all.** Caught last time via a catch-all `<prop key="*">PROPAGATION_REQUIRED</prop>`. Re-verify this is still correctly applied to all `TransactionProxyFactoryBean` definitions after any XML changes in this phase — don't assume it carries forward silently.
2. **`HibernateTemplate` blocking writes outside an active transaction.** Hibernate 5 doesn't change this Spring-side behavior, but if `HibernateTemplate`'s defaults shift in the 4.3→5.x line, re-run the exact diagnostic-test technique from last time (a throwaway test asserting `TransactionSynchronizationManager` state) rather than re-guessing.
3. **Object-level `delete()`/`deleteAll()` silently dropped without an explicit flush, in non-transactional test-only beans (`HSTestSupportDao`).** Already fixed for H4.1. Confirm the fix (`session.flush()` after `session.delete()`) still holds under H5's session/transaction model — don't assume it's now "fixed forever"; the underlying cause (no Spring transaction wrapping a test helper) is unchanged, so it's worth a deliberate regression check, not a skip.

---

## Phase Breakdown

### Phase 1: Preparation (30 minutes)

```bash
# Baseline counts (compare against the H4.1 numbers to see scope delta)
grep -rn "org.springframework.orm.hibernate4" src/main/java src/main/webapp/WEB-INF --include="*.java" --include="*.xml" | wc -l
grep -rln "org.hibernate.Criteria\|org.hibernate.criterion" src/main/java/net/uch/*/dao/impl/*.java | wc -l
grep -rn "jasypt.hibernate4" src/main/java src/main/resources --include="*.java" --include="*.xml" | wc -l

git checkout -b upgrade/spring43-hibernate5
```

---

### Phase 2: Version Bump (30 minutes)

#### 2a. pom.xml

```xml
<properties>
    <spring-framework.version>4.3.30.RELEASE</spring-framework.version>
    <hibernate.version>5.2.18.Final</hibernate.version>
</properties>

<dependency>
    <groupId>org.jasypt</groupId>
    <artifactId>jasypt-hibernate5</artifactId>
    <version>1.9.3</version>
</dependency>
```

`hibernate-c3p0:5.2.18.Final` still transitively drags the ancient `c3p0:c3p0:0.9.1` coordinate — same problem we hit last phase. Re-apply the same fix immediately, don't wait to hit the `AbstractMethodError` again:

```xml
<dependency>
    <groupId>com.mchange</groupId>
    <artifactId>c3p0</artifactId>
    <version>0.9.5.5</version>
</dependency>
<!-- plus the existing exclusion of c3p0:c3p0 from hibernate-c3p0 -->
```

#### 2b. applicationContext-hibernate.xml

```xml
<!-- org.springframework.orm.hibernate4.* -> org.springframework.orm.hibernate5.* -->
<bean id="sessionFactory" class="org.springframework.orm.hibernate5.LocalSessionFactoryBean">
    ...
</bean>
<bean id="transactionManager" class="org.springframework.orm.hibernate5.HibernateTransactionManager">
    ...
</bean>
```

No change expected to `hibernate.dialect` (`MySQL5Dialect` is still valid in 5.2.x against MySQL 5.6) or to the absence of `hibernate.current_session_context_class` (still must stay unset/Spring-managed, per the H4.1 lesson).

---

### Phase 3: Package + Import Fixes (30–45 minutes, mostly automated)

```bash
# DAO impls: HibernateDaoSupport package
find src/main/java -name "*.java" -exec sed -i \
  's/org\.springframework\.orm\.hibernate4\.support\.HibernateDaoSupport/org.springframework.orm.hibernate5.support.HibernateDaoSupport/g' {} \;

# Test support DAO + any other hibernate4-package references
grep -rln "org.springframework.orm.hibernate4" src/main/java src/test/java --include="*.java"

# Jasypt
find src/main/java src/main/resources -name "*.java" -o -name "*.hbm.xml" | xargs sed -i \
  's/org\.jasypt\.hibernate4\./org.jasypt.hibernate5./g'
```

**Verification:**
```bash
grep -r "orm.hibernate4\|jasypt.hibernate4" src/main/java src/main/resources src/test/java  # should return nothing
```

---

### Phase 4: Criteria API — confirm no rewrite needed, fix what Hibernate 5 actually changed (1–3 hours)

Unlike the H3→H4 phase, this is **not** expected to be the bottleneck — Criteria's method signatures (`Criteria.list()` returning raw `List`, `DetachedCriteria`, `Restrictions`, `setFetchMode`) are unchanged between 4.1 and 5.2. Expect compiler **warnings** (`Criteria` deprecated), not **errors**, at the ~193 call sites already fixed last phase.

What actually needs checking in Hibernate 5.2:

- **`Session.createCriteria(Class)` deprecation noise** — cosmetic, ignore unless it actually breaks compile (it doesn't in 5.2.x).
- **`Criteria.uniqueResult()` / `.list()` return types** — unchanged, no new casts needed beyond what's already there.
- **Bytecode provider** — correction: the Javassist → Bytebuddy default switch lands in Hibernate **5.4**, not 5.2. At the 5.2.18 target, Javassist remains the default, so proxy-generation behavior is unchanged from 4.1. (Kept here as a note for a future 5.4+ bump; at that point, check for any `equals()`/`hashCode()` overrides on mapped entities that assume a specific proxy class:)
  ```bash
  grep -rln "public boolean equals\|public int hashCode" src/main/java/net/uch/mapping/*.java
  ```
- **`saveOrUpdate()` semantics** — unchanged from 4.1; no new replacement needed beyond what was already done for `saveOrUpdateAll()`.

---

### Phase 5: Re-verify the H4.1 transaction-semantics fixes still hold (1–2 hours)

This phase doesn't introduce new transaction-boundary changes by design, but Hibernate 5's session-context handling is the highest-risk area for silent regressions of the same three bugs listed above. Don't skip this just because "Hibernate 5 is supposed to be backward compatible at the API level" — verify empirically, the same way the H4.1 phase did:

1. Re-run the full suite **twice in a row with no manual DB intervention between runs** (the idempotency check that caught the test-helper flush bug last time).
2. If anything reappears, use the same diagnostic-test technique (a throwaway test asserting `TransactionSynchronizationManager.isActualTransactionActive()` / `isCurrentTransactionReadOnly()`) rather than re-guessing from logs.

---

### Phase 6: Compilation Verification (30 minutes)

```bash
mvn clean compile -DskipTests -q 2>&1 | tee /tmp/h5-compile.log
grep -c "\[ERROR\]" /tmp/h5-compile.log   # target: 0
grep -c "\[WARNING\].*deprecat" /tmp/h5-compile.log   # expected: Criteria-related, informational only
```

---

### Phase 7: Testing (2–3 hours)

```bash
mvn test 2>&1 | tee /tmp/h5-test.log
```

Then the idempotency check from Phase 5, plus the same manual JSF/RichFaces smoke test still pending from the H4.1 phase (Alumnos.jsp, Notas.jsp, Horario.jsp, Docentes.jsp, login/logout) — do it once for this phase, not twice; if the H4.1 manual smoke test was done by the time this phase starts, this is the first real one.

---

## Risk Mitigation Strategies

### Risk 1: Bytecode provider change affects lazy-loaded entity identity/equality
**Mitigation:** grep for custom `equals`/`hashCode` on mapped entities (Phase 4) before assuming Bytebuddy's proxies behave identically to Javassist's.

### Risk 2: c3p0 transitive coordinate regression
**Mitigation:** apply the exclusion + pin in the same commit as the version bump (Phase 2), not as a reactive fix after hitting `AbstractMethodError` again.

### Risk 3: Recurrence of non-transactional test-helper flush bug
**Mitigation:** Phase 5's two-runs-in-a-row check. If it recurs, the fix pattern is already known (`session.flush()` inside `getHibernateTemplate().execute(...)`).

### Risk 4: jasypt-hibernate5 type compatibility
**Symptom:** `MappingException: Could not determine type for: org.jasypt.hibernate5.type.EncryptedStringType` if the `.hbm.xml` literal string isn't caught by the sed pass.
**Mitigation:** explicitly grep `.hbm.xml` files for `jasypt.hibernate4` after Phase 3 — the literal class-name string in XML isn't a Java import and was missed by automated import fixes in the H3→H4 phase too.

---

## Work Breakdown Summary

| Phase | Task | Effort | Complexity |
|-------|------|--------|------------|
| 1 | Preparation + baseline | 30m | TRIVIAL |
| 2 | Version bump (pom.xml + XML + c3p0 pin) | 30m | TRIVIAL |
| 3 | Package/import fixes (automated) | 30–45m | TRIVIAL |
| 4 | Criteria compatibility check + bytecode provider check | 1–3h | LOW–MEDIUM |
| 5 | Re-verify H4.1 transaction-semantics fixes | 1–2h | MEDIUM (empirical, not mechanical) |
| 6 | Compile verification | 30m | TRIVIAL |
| 7 | Testing (unit + idempotency + manual JSF) | 2–3h | MEDIUM |
| **TOTAL** | | **6–10 hours** | |

Notably *less* mechanical-fix volume than the H3→H4 phase (no 193-call-site generics problem this time), but proportionally *more* empirical-verification weight — consistent with this being a smaller technical jump riding on a now-correct H4.1 baseline, rather than a second from-scratch transaction-semantics excavation.

---

## Commit Strategy

Same as last phase: one atomic commit, easy to revert, single test run validates the whole migration.

```bash
git commit -m "$(cat <<'EOF'
chore: upgrade Spring 4.1 -> 4.3 and Hibernate 4.1 -> 5.2

- Spring 4.1.9 -> 4.3.30
- Hibernate 4.1.12 -> 5.2.18
- jasypt-hibernate4 -> jasypt-hibernate5 (Java imports + .hbm.xml literal)
- Re-pinned com.mchange:c3p0 against the new hibernate-c3p0 transitive
- Criteria API call sites unchanged (still supported in 5.2, deprecated not removed)
- Re-verified H4.1 transaction-semantics fixes hold under H5's session context

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>
EOF
)"
```

---

## Success Criteria

- `mvn clean compile -DskipTests` → BUILD SUCCESS, 0 errors (Criteria deprecation warnings expected and acceptable)
- `mvn test` → all tests pass, **twice in a row with no manual DB intervention** between runs
- No JSF/RichFaces regressions (manual browser smoke test)
- No recurrence of the three H4.1-era transaction-semantics bugs

---

## Next Steps

1. Merge and stabilize `upgrade/spring4-hibernate4` first — including the still-pending manual JSF smoke test.
2. Treat this doc as ready-to-execute once that's done; re-read the "Lessons carried forward" section before starting Phase 1, since that's where most of the actual debugging time will go if anything regresses.

---

## References

**Hibernate 5.2 Migration:**
- https://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html
- https://github.com/hibernate/hibernate-orm/blob/5.2/migration-guide.adoc

**Spring 4.3 + Hibernate 5:**
- https://docs.spring.io/spring-framework/docs/4.3.x/spring-framework-reference/htmlsingle/#orm-hibernate

---

---

## Execution results (2026-07-05)

Executed on branch `upgrade/hibernate5` in four commits (positional params → Spring 4.3.30 → Hibernate 5.4.24 core → docs), each individually green. Target was **5.4.24**, not the planned 5.2.18, which changed the picture in these ways:

### What 5.4 forced beyond the 5.2 plan

1. **JDBC-style positional parameters (`= ?`) removed in 5.3.** All ~30 HQL queries across 14 DAO impls + `HSTestSupportDao` converted to named parameters via `findByNamedParam` — done as a separate first commit on the H4 baseline so the still-green suite validated it before any version bump.
2. **`TransactionRequiredException` on `executeUpdate()`/`flush()` outside a transaction (5.2+ JPA compliance).** This is "lesson 3" from the H3→H4 phase escalating from *silent no-op* to *hard error* — and it's a good thing:
   - `HSTestSupportDao` (test-only, previously bare) is now `@Transactional`, proxied by the `tx:annotation-driven` already present in `applicationContext-services.xml`. All the H4-era hacks came out: `setCheckWriteOperations(false)`, manual `beginTransaction()/commit()` blocks (which would collide with the Spring-managed transaction), and explicit `session.flush()` after object-level deletes.
   - **Real production bug caught:** `HSAulaDAOImpl.eliminarAula` opened a raw un-transacted `openSession()` for a bulk update (silently autocommitted on H4, session leaked on exception). Rewritten to `getHibernateTemplate().execute(...)` like every other `eliminar*` DAO. The other five raw `openSession()` sites are read-only or manage their own transaction and were left as-is (minimal impact).
3. **Callable named queries broken on MySQL Connector/J 8.** The 4 `<sql-query callable="true">` mappings (`sp_data_banco`, `sp_conceptos_banco`, `sp_convalidacion_cursos`, `sp_Generar_Codigo`) failed with `No operations allowed after statement closed` — H5's CallableStatement path closes the statement before extracting the ResultSet. Fixed by dropping `callable="true"` and using plain native `CALL sp_x(...)` bodies, which Connector/J executes as prepared statements.
4. **Spring 4.3's embedded CGLIB vs Java 11.** Proxying the class-based (no-interface) `@Transactional` test bean needs `--add-opens java.base/java.lang=ALL-UNNAMED`, added to surefire only (production proxies are interface-based JDK proxies and unaffected). This goes away when Spring reaches 5.x.
5. **Byte Buddy replaces Javassist as bytecode provider in 5.4** (as this plan's Phase 4 note predicted). The explicit `org.javassist:javassist` pin was removed from the pom; no proxy-behavior regressions observed.

### What the plan predicted correctly

- Criteria API: zero changes needed at the ~193 call sites (deprecated, not removed).
- jasypt: `jasypt-hibernate5:1.9.3` + the `.hbm.xml` literal class string in `TbUsuario.hbm.xml` (caught by including `*.hbm.xml` in the rename pass, as Risk 4 warned).
- `MySQLDialect`, c3p0 pin, and the Spring XML `LocalSessionFactoryBean`/`HibernateTransactionManager` renames were drop-in.
- The idempotency double-run (Phase 5) was again the decisive verification: the first H5 runs leaked fixture rows exactly where cleanups aborted, requiring a one-time approved purge (`ac_aula` 71-74, `ac_ampliaciones` 15, `ad_comprobante_pago` 48554 + child `ad_pago`, `TRUNCATE ac_equivalencia`) before the clean double-run.

### Still pending

- Manual JSF/RichFaces browser smoke test on this branch (Phase 7's last item).
- Post-migration modernization (separate PRs): typed `Query<T>` for the ~46 HQL sites, centralized Criteria cast helper.

---

**Owner:** Claude Code + César
**Created:** 2026-06-28
