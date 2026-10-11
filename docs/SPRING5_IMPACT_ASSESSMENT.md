# Spring 4.3 → Spring 5.3 Migration Impact Assessment

**Date:** 2026-07-05
**Baseline:** branch `upgrade/hibernate5` — Spring 4.3.30, Hibernate 5.4.24, Java 11, JSF 1.2 + RichFaces 3.3, 393/393 tests green
**Target:** Spring Framework **5.3.39** (final 5.3.x release)
**Type:** analysis only — no implementation

---

## 1. Executive Summary

**Overall impact: LOW.** This is the smallest framework jump of the entire modernization track — smaller than either Hibernate phase. The reason is structural: this application uses Spring only as (a) an XML bean container bootstrapped by `ContextLoaderListener`, (b) a transaction manager (`TransactionProxyFactoryBean` × 56 + `tx:annotation-driven` services), and (c) the `orm.hibernate5` integration (`HibernateDaoSupport`/`HibernateTemplate`). **Every one of those APIs survives unchanged in Spring 5.3.** There is no Spring MVC, no Spring Security, no Spring-JSF bridge — the JSF layer finds beans through the custom `ServiceFinder`, which only touches `WebApplicationContextUtils`.

- **Estimated effort:** 0.5–1 day (version bump + full suite + JSF smoke test).
- **Main blocker:** none at compile/runtime. One *operational* caveat: the `tomcat7-maven-plugin` local runner (see §3.1).
- **Main motivations:** Spring 4.3 has been EOL since December 2020 (no security fixes); Spring 5.3 formally supports the Hibernate 5.4 + Java 11 combination we now run; and Spring 5's repackaged CGLIB removes the `--add-opens` workaround added for the test suite.
- **Recommendation:** **Yes — low-risk migration** (see §6).

## 2. Affected Components

| Area | What we use | Status in Spring 5.3 |
|---|---|---|
| Bootstrap | `ContextLoaderListener` (web.xml:148) | Unchanged |
| Bean config | DTD-based `applicationContext-hibernate.xml` (+ `<ref local>`), XSD-based services XML | DTD config still parsed; `<ref local>` valid in DTD |
| Transactions | 56× `TransactionProxyFactoryBean`, `tx:annotation-driven`, `HibernateTransactionManager` | All present, not removed |
| ORM | `orm.hibernate5` (`HibernateDaoSupport`, `HibernateTemplate`, `LocalSessionFactoryBean`) | Present; Spring 5.1+ is the *formally supported* tier for Hibernate 5.3/5.4 |
| Properties | `PropertyPlaceholderConfigurer` (env-var JDBC config) | Deprecated since 5.2 in favor of `PropertySourcesPlaceholderConfigurer` — still fully functional; migration optional |
| Logging | `commons-logging` → SLF4J → Log4j 1.x | Spring 5 ships `spring-jcl`, which auto-routes to the SLF4J binding already on the classpath — no change needed |
| Tests | `spring-test` + JUnit 4 + Mockito 3.6 | JUnit 4 `SpringRunner` fully supported in 5.3 |
| Frontend | JSF 1.2 / RichFaces 3.3 via `ServiceFinder` | Zero Spring coupling — unaffected |

**Dead weight found during analysis:** the pom declares `spring-webmvc`, but no `org.springframework.web.servlet.*` class is referenced anywhere. The bump is a natural moment to narrow it to `spring-web` (which provides `ContextLoaderListener`).

## 3. Main Breaking Changes / Risks

### 3.1 Servlet 3.1+ baseline vs. the tomcat7-maven-plugin (the only real friction)
Spring 5's web module requires a Servlet 3.1+ container. Production runs Tomcat 9 (Docker) — fine. But `mvn tomcat7:run` embeds Tomcat 7.0.x (Servlet 3.0). Our usage (`ContextLoaderListener` only, no Spring MVC) may happen to work, but it is formally unsupported and could fail at any 3.1-only API touch.
**Mitigation:** move local runs to a Tomcat 9 runner (cargo-maven-plugin or the existing Docker image). This aligns dev with prod anyway and was already flagged as pending from the H4 phase.

### 3.2 Deprecation noise, not breakage
`PropertyPlaceholderConfigurer` and a few other 4.x-era classes log deprecation warnings. Nothing we use is removed. Policy: leave as-is (KISS), record in the tech-debt list.

### 3.3 What Spring 5.3 explicitly does NOT break (verified against our usage)
- DTD-based XML bean definitions — still parsed by 5.3.
- `TransactionProxyFactoryBean` — present and functional (it has been "superseded" since Spring 2.0 yet never removed).
- `HibernateTemplate`/`HibernateDaoSupport` — present in `orm.hibernate5`.

### 3.4 The wins
- **CGLIB on Java 11 works natively** → delete the `--add-opens java.base/java.lang=ALL-UNNAMED` surefire workaround added during the H5 migration.
- **Security posture:** 4.3.x stopped receiving fixes in Dec 2020; 5.3.x received fixes into 2024 (including the Spring4Shell era — note 5.3 itself was the *patched* line while 4.3 was already out of scope).
- Formal support for exactly our stack: Java 11 + Hibernate 5.4 + JUnit 4.

## 4. Complexity Analysis

| Area | Complexity | Notes |
|---|---|---|
| Configuration (XML) | **Low** | No changes required; DTD + XSD both parse |
| Transactions | **Low** | No changes; proxy beans and annotations carry over |
| ORM integration | **Low** | Same `orm.hibernate5` package; H5.4 formally supported |
| Test infrastructure | **Low** | Remove `--add-opens`; JUnit 4 runner unchanged |
| Local dev runtime | **Medium** | tomcat7 plugin replacement (cargo/Tomcat 9 or Docker) |
| Frontend (JSF/RichFaces) | **None** | No Spring coupling |

## 5. Estimated Refactoring Scope

- **Required code changes: ~0 classes.** The bump is `pom.xml` (`spring-framework.version` → 5.3.39) plus removing the surefire `--add-opens`.
- **Optional, same PR:** `spring-webmvc` → `spring-web`.
- **Optional, separate:** replace tomcat7-maven-plugin for local runs (~1–2 h).
- **Verification:** full suite twice consecutively (idempotency), JSF browser smoke test.

## 6. Final Recommendation

**Yes — low-risk migration.** Do it as its own small branch *after* `upgrade/hibernate5` merges, so each framework jump stays independently revertable. Sequence: bump → remove `--add-opens` → suite ×2 → smoke test.

### The strategic picture: Spring 5.3 is the terminal station for this architecture

Worth being explicit, because it frames every future decision:

- **Spring 6 requires Jakarta EE 9+ (the `jakarta.*` namespace), Tomcat 10+, and Java 17.** JSF 1.2 and RichFaces 3.3 are `javax.*` forever — they cannot run on a Jakarta container. Spring 6 is therefore **blocked by the frontend**, not by the backend.
- **Spring 6 also removes `orm.hibernate5`** (`HibernateTemplate`/`HibernateDaoSupport` are gone), which would force the DAO layer onto JPA/Hibernate 6 at the same time.
- Consequently: **Spring 5.3.39 + Hibernate 5.4/5.6 + Java 11/17 + Tomcat 9 is the highest stack this application can reach without replacing the view layer.** Everything beyond it is a frontend-replacement project first and a framework migration second.

That makes this bump attractive precisely *because* it's the last cheap one: it collects the remaining security/compatibility value available to the current architecture at near-zero risk, and cleanly marks the boundary where modernization stops being incremental.

---

## 7. Execution results (2026-07-08, branch `upgrade/spring5`)

Migration performed and validated: full suite **400/400 green twice consecutively**, idempotent, no manual DB intervention. Effort landed inside the 0.5–1 day estimate. Two predictions in this assessment proved **wrong** and required small, mechanical fixes — recorded here for honesty:

1. **`<ref local="…"/>` is NOT valid in Spring 5** (contradicts §2, §3.3). Spring 5 removed the `local` attribute of `<ref>`/`<idref>`; the context failed to parse with *"'bean' or 'parent' is required for `<ref>` element"*. Fix: rewrote all 62 `<ref local="x"/>` → `<ref bean="x"/>` in `applicationContext-hibernate.xml` (semantically identical — every ref was same-file; `bean` is the superset of `local`). This was the DTD/XSD's only real Spring-5 incompatibility.
2. **`spring-context` had to be declared explicitly.** Narrowing `spring-webmvc` → `spring-web` (§2, §5) also dropped the transitive `spring-context` that `ServiceFinder`/servlets rely on (`ApplicationContext`, the supertype of `WebApplicationContext`). Added `spring-context` as a direct dependency — an honest declaration of a dependency the app always had, previously supplied by accident.

Changes made, total: `pom.xml` (spring 4.3.30 → **5.3.39**; `spring-webmvc` → `spring-web` + explicit `spring-context`; removed the surefire `--add-opens` block — JaCoCo's `prepare-agent` sets the `argLine` Surefire honors on its own); `applicationContext-hibernate.xml` (`ref local` → `ref bean` ×62). **Zero Java changes**, as predicted. Cargo `tomcat9x` runner (§3.1 mitigation) was already in place from the H4/H5 phase.

---

**Owner:** Claude Code + César
**Related:** `HIBERNATE5_MIGRATION_PLAN.md` (execution results), `HIBERNATE5_IMPACT_ASSESSMENT.md` (method/template)
