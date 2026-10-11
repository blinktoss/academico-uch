# Sistema Acad&eacute;mico

```
docker run --name mysql-uch-v5_6 -p 3308:3306 --restart on-failure -e MYSQL_DATABASE=db_sis_academica -e MYSQL_ROOT_PASSWORD=rootroot -e TZ='America/Lima' -d mysql:5.6.51 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

docker network create --driver bridge uch-net

docker build -t uch-sistema-academico .

docker run -p 8084:8080 --name uch-sistema-academico --network uch-net -d uch-sistema-academico

docker network connect uch-net uch-sistema-academico

docker network connect uch-net mysql-uch-v5_6
```

Just in case, you have to increase the `max_connections` variable on MySQL.

```
mysql -u root -p -P 3308 --show-warnings
```

```
DROP DATABASE db_sis_academica;
```

```
CREATE DATABASE IF NOT EXISTS db_sis_academica CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

```
USE db_sis_academica
```

```
source <filename>.sql
```

or

```
mysql -p -u[user] [database] < db_backup.dump
```

You can use `setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)` to group results in Hibernate.

Setup database:
```
source db/db_sis_academica_setup.sql
```

For reports don't manage the `rendered` attribute on `a4j:mediaOutput` neither the `reRender` when invoke the modal
and add these attributes on `a4j:mediaOutput`:
```
cacheable="false" session="true"
```

> [!NOTE]
> For `ad_comprobantes_pago` consider the next table:

| Code | Type 1 | Type 2 |
|:----:|--------|--------|
| 001  | 015002 EFECTIVO | 015003 SALDO |
| 002  | 015001 VOUCHER - COMPROBANTE ||
| 003  | 015004 VALE | 015005 BOLETA-VALE |
| 004  | 015006 TRANSFERENCIA | 015007 PAGO BANCO |

---

## Context

### Development History

The original version of this application was an Eclipse-style project built with Java 7 and MySQL 5.3.

Before the pandemic, I was transitioning from Software Developer to Technical Leader. I decided to continue improving my skills by modernizing this legacy application. As part of that effort, I migrated the project to Maven, implemented unit and integration tests, and started using Docker to containerize the database.

I validated the application and increased test coverage from 0% to more than 80%, identifying multiple anti-patterns and resolving them by applying modern engineering practices, better design principles, and effective testing techniques.

During the pandemic, and while transitioning from Technical Leader to Technology Manager, I decided to further modernize the stack. I created the UCH-stack and introduced additional layers for observability, monitoring, reverse proxy support, and security improvements.

Because this application relies heavily on legacy frameworks, I expect it to contain multiple security vulnerabilities and outdated patterns that need to be addressed.

My current focus is to close the feature gap and strengthen the developer pathway for real-world applications, using AI tools to accelerate development and improve engineering quality.

---

### Project Scope

This academic system manages the full student lifecycle within the university, covering enrollment, academic progression, grading, and additional academic services.

Since the institution is private, every student must complete an enrollment process before participating in academic activities. Students begin their academic journey within a specific Faculty and Specialty and must renew their enrollment every semester based on academic and administrative requirements.

One of the core processes of the system is grade management. Teachers are responsible for registering student grades and generating official academic records known as **Actas (Official Grade Records)**.

These documents contain sensitive information and, once registered, closed, and published, become immutable.

Enrollment is one of the most critical operational processes for the institution. To support it, academic structures such as semesters, shifts, schedules, courses, and sections must be configured in advance and made available for student registration.

The system also validates students’ financial status during enrollment. Students with outstanding debts may be restricted from enrolling or flagged for administrative review.

In some cases, they may continue studying, but progression to the next academic cycle may be restricted until their financial obligations are resolved.

Additionally, the system supports optional academic activities through Free Courses, allowing students to expand their knowledge in areas such as Office Tools, Databases, Sports, and other complementary subjects.

---

### Architecture Stack

#### Frontend
- JSP
- JSF 1.2
- RichFaces 3.3
- JavaScript
- CSS

#### Backend
- Java 11
- Spring Framework 5.3.39
- Hibernate 5.4.24

> Final suite baseline (no Jakarta/Spring 6 migration — see `uch-env/docs/Backend_Foundation_Charter.md`). This repo's Hibernate mappings are the suite's source of truth, planned for verbatim extraction into `net.uch:uch-shared-model`.

#### Database
- MySQL 5.6

#### Build & Dependency Management
- Maven

#### Testing
- JUnit
- Spring Test
- Mockito

#### Infrastructure
- Docker

---

### Software Development Conventions

- I am currently the sole developer of this project.
- Logging is standardizing on **Log4j 2** (suite-wide backend; see `uch-env/docs/Logging_Standard.md`). The pool is already **HikariCP 5.1.0** (#62), so only the slf4j binding and config file change here.
- Logging levels are important because observability and log monitoring tools may be introduced in the future to analyze application behavior and concurrent user activity.
- In production, the system is expected to support around 50 concurrent administrative users with different roles.
- Since this project was originally developed in Peru, Spanish was widely used in the codebase.
- Moving forward, all new code should follow English naming conventions whenever possible.

### Database Naming Conventions

- Prefix **ac** refers to academic-related tables.
- Prefix **cl** refers to Free Courses tables (*Cursos Libres*).
- Prefix **tb** refers to general-purpose tables such as users and configuration data.

All new tables and columns should be named in English.

---

### Frontend Layout Conventions

Every page is a JSP under `src/main/webapp/<Module>/` that pulls in the shared chrome with `<jsp:include page="../TablaSistema/Menu.jsp"/>`, then implements its own feature inside a `<rich:panel>`. `TablaSistema/Menu.jsp` is the common base for every page: it guards the session, loads the global stylesheet (`<a4j:loadStyle src="resource:///css/formulario.css"/>`), and renders the application toolbar — so individual pages don't link the CSS themselves.

Legacy pages use HTML `<table>` elements for page **layout**. New and modernized pages should instead use `<div>` + CSS flexbox utility classes defined in `src/main/webapp/css/formulario.css`:

- `.row` — flex container (`display: flex; flex-wrap: wrap`)
- `.col` — equal-width column; `.col-3` / `.col-10` — fixed-width columns; `.colspan-2` — simulate a table colspan
- `.uch-label` — fixed-width, right-aligned label; `.uch-input` — flexible input cell

Migrated reference implementations: `src/main/webapp/Administrativa/Saldos.jsp` and `src/main/webapp/Academica/Notas.jsp`. A not-yet-migrated, table-based example is `src/main/webapp/Academica/Alumnos.jsp`.

Tables are still appropriate for genuinely **tabular data** (`rich:dataTable`, grade matrices); only table-based *layout* should migrate to flex divs, and modal panels are migrated incrementally.

---

### Testing Conventions

- The testing goal is to achieve more than 95% code coverage.
- For testing, a live development Docker instance running MySQL 5.6 is used with the latest backup.
- After restoring the backup, an additional setup script must be executed to fix legacy data inconsistencies.

---

### Known Technical Debt

- Legacy JSP views with table-based layouts
- JSF 1.2 lifecycle complexity
- RichFaces 3.3 is deprecated
- Multiple legacy anti-patterns in service and DAO layers
- Security vulnerabilities pending review
- Incomplete test coverage in some legacy modules

---

### Technical Debt Backlog (Anti-Pattern Audit — 2026-06-25)

Pending improvements identified by auditing the codebase against a Java/Spring legacy
anti-pattern checklist. Revisit periodically and decide which are worth addressing at the
time, balancing risk vs. the "minimal-impact, don't break legacy behavior" guideline.
Severity: 🔴 Critical (correctness/security) · 🟡 Caution (significant, not urgent) · 🟢 Advisory (maintainability).

#### 🔴 Critical

- [ ] **RichFaces 3.3.3 RCE (CVE-2018-14667, EL injection, CVSS 9.8; also CVE-2013-2165).**
  3.x is EOL with no fixed release. Short term: assess exposure of the RichFaces resource
  path and consider a filter/WAF mitigation. Long term: strongest driver for the framework
  migration. — `pom.xml` (`richfaces.version` 3.3.3.Final)
- [ ] **Secrets committed in source.** Real-looking DB credentials live in
  `src/main/webapp/WEB-INF/hibernate.properties` (`jdbc.username`/`jdbc.password` + commented
  alternates). `jasypt-hibernate3` 1.9.3 is already on the classpath — encrypt the password
  or externalize via `${DB_PASSWORD}`/env (matches the UCH Docker/PROD model), then rotate the
  committed credential.
- [ ] **HQL string concatenation (injection-shaped).** `HSPagoDAOImpl.maximoNroComprobante`
  (`src/main/java/net/uch/administrativa/dao/impl/HSPagoDAOImpl.java:110`) concatenates `serie`
  into the query; reachable from the user-bound field `nMpNumSerie` in `MbPago`. Fix: bind it as
  a parameter. Add a DAO test (this DAO is currently untested). Small, isolated commit.
- [ ] **Some DAO methods run with no Spring-managed transaction at all — but fixing this with a
  blanket catch-all is NOT safe, tried and reverted on 2026-06-25.** `TransactionProxyFactoryBean`
  beans in `applicationContext-hibernate.xml` declare `transactionAttributes` patterns
  (`get*`/`find*`/`load*`/`store*`/`add*`) that assume English-verb method names, but most DAO
  methods are named with Spanish verbs (`seleccionar*`, `registrar*`, `insertar*`, `guardar*`,
  `eliminar*`, `actualizar*`...). A method whose name matches none of the configured patterns gets
  no transaction demarcation whatsoever. Confirmed fixed for `springHibernateDaoActa` (see
  `feature/acta-immutability`) with explicit per-method patterns - that approach is safe.
  **Adding `<prop key="*">PROPAGATION_REQUIRED</prop>` to all 56 beans as a blanket fix was tried
  and broke 6 test classes** (`MbNota`, `MbMatricula`, `MbEstructuraPago`, `MbPago`,
  `MbMatriculaCL`, `MbTallerAperturado`), in two distinct ways:
  - `HSNotaDAOImpl.insertarOrActualizarNotas`, `HSSaldoDAOImpl`, `HSAlumnoTarifaDAOImpl` call
    `session.beginTransaction()`/`tx.commit()` *manually* inside a `HibernateTemplate.execute()`
    callback. Wrapping that call in an outer Spring-managed transaction makes the manual
    `tx.commit()` finish/invalidate the transaction Spring's interceptor still thinks is open,
    throwing `TransactionSystemException: Transaction not successfully started` on the outer
    commit. These manual blocks must be *removed* (let the ambient transaction handle it) before
    that bean can safely get a catch-all.
  - Several other failures were `LazyInitializationException` ("no Session"/"session was
    closed") — some code's lazy-loading currently "works" specifically *because* no transaction
    is open, in a way not yet fully understood; needs per-DAO investigation, not a blanket change.
  - Net: fixing this properly requires going DAO-by-DAO (or service-by-service, see the
    `chore/service-layer-foundation` plan), running the full suite after each one, not one
    sweeping change. A failed full-suite run can also corrupt dev-DB rows mid-write (saw 15
    `ac_nota` rows left with `not_nota IS NULL` after one such failure) - clean those up same as
    the Acta test-pollution rows below if you hit this again.

#### 🟡 Caution

- [x] **No service/transactional boundary — fixed for enrollment.** `MbMatricula` now delegates
  writes/batched reads to `EnrollmentService` (`@Transactional`, same template as
  `GradeRecordService` — see "Service layer pattern" below).
  [#23](https://github.com/cesardl/sistema_academico-uch/issues/23) (PR
  [#25](https://github.com/cesardl/sistema_academico-uch/pull/25), pending merge). Still
  outstanding for **Convalidación** (`MbConvalidacion`'s cross-DAO write isn't atomic) —
  [#24](https://github.com/cesardl/sistema_academico-uch/issues/24) — and for **Payments**
  (`MbPago`/`MbEstructuraPago`), not yet scoped.
- [ ] **God Beans.** Business logic concentrated in managed beans:
  `MbPago` (~2,700 LOC), `MbAlumno` (~1,990), `MbNota` (~1,250), `MbMatricula` (~1,270, shrinking
  as enrollment logic moves into `EnrollmentService`).
  Extract collaborators per business capability (continues the `MbPago → ComprobanteServlet` SRP split;
  the payment search was extracted to `PaymentSearchService`, issue #4).
- [ ] **No pagination on most list queries.** The **payment search (`MbPago`/`PaymentSearchService`)
  is now server-side paginated** with `setFirstResult`/`setMaxResults` (issue #4); student/grade
  searches remain unbounded. Continue with the remaining high-traffic lists (alumnos next).
- [ ] **Outdated dependencies.** JasperReports 3.1.2 (2009), pdfbox 0.7.3 (2008) still
  unaddressed. Spring/Hibernate were bumped to 4.3.30/5.4.24 (`upgrade/hibernate5`) — better, but
  Spring 4.3 has itself been EOL since December 2020; Spring 5 is the next planned step.
  **Scanning is now wired** (below); use it to prioritize these bumps by CVE severity.
- [x] **Dependency CVE scanning — wired.** OWASP `dependency-check-maven` (9.2.0, NVD API 2.0) is
  configured in `pom.xml`, on-demand and report-only (`failBuildOnCVSS=11`, not bound to a build
  phase, so it doesn't slow `package`/`test`). Run `mvn dependency-check:check` →
  `target/dependency-check-report.{html,json}`; accepted risks / false positives go in
  `owasp-suppressions.xml`. First run downloads the NVD dataset (slow without an `-Dnvd.api.key`).
- [x] **Open-Session-in-View was configured but never actually ran - removed.**
  `OpenSessionInViewFilter` was mapped to `*.faces` in `web.xml`, but `FacesServlet` is mapped to
  `*.jsf` - the two URL patterns never overlapped, so the filter never fired for real JSF traffic
  (confirmed: nothing in the app navigates to `.faces` URLs). Deleted on `chore/service-layer-foundation`
  rather than fixing the mapping: given this app's documented history of N+1 tree-building queries
  and connection-pool exhaustion, OSIV would make that worse (holds a connection for the whole
  view-render, hides N+1) more than help. The replacement direction is services that eagerly
  assemble / DTO-project what the view needs so nothing lazy-loads after the transaction closes.
- [ ] **Manual Hibernate session management scattered across several DAOs, predating/duplicating
  what a transaction boundary should provide.** Two distinct patterns, both missing
  `try`/`finally` (an exception skips the cleanup, leaking the connection until HikariCP's
  `maxLifetime` retires it — `leakDetectionThreshold` can be enabled to log the leak):
  - Self-contained: `HSPagoDAOImpl`, `HSSaldoDAOImpl`, `HSAulaDAOImpl`, `HSConceptoPagoDAOImpl`,
    `HSTurnoDAOImpl` call `getSessionFactory().openSession()` and `session.close()` within one
    method. `HSHorarioDAOImpl.duplicidadHoraria()` is the same shape and still does this
    (fixed its `try`/`finally` leak in passing while parameterizing the `sem_id` bug,
    [#30](https://github.com/cesardl/sistema_academico-uch/issues/30) — not separately tracked).
  - [x] **Caller-shared (`HSMatriculaDAO`/`MbMatricula`) — fixed.** `openSession()`/`closeSession()`
    were handed to `MbMatricula`, which shared one `Session` across a whole loop of DAO calls
    (three places) before closing it manually — exactly the shape of problem a `@Transactional`
    service solves for free, with a real `try`/`finally` guarantee. Both DAO methods and the
    `Session` parameter threaded through `seleccionarMatricularRegular`/etc. were deleted entirely
    by `EnrollmentService` ([#23](https://github.com/cesardl/sistema_academico-uch/issues/23), PR
    [#25](https://github.com/cesardl/sistema_academico-uch/pull/25)).
- [ ] **Service Locator instead of constructor injection.** `ServiceFinder.findBean` used ~266×
  across 52 files. Acceptable for this stack, but limits testability vs. explicit injection.
- [ ] **Timezone / date-model inconsistencies across App / JDBC / DB.** Surfaced during the
  Docker+MySQL modernization: a day-filtered query returned wrong results until the connection TZ
  was aligned with the server's. DB container is `TZ=America/Lima` (Lima wall-clock values).
  - **Root cause for date-only fields**: a true `DATE` column mapped in Hibernate as
    `type="java.util.Date"` binds the parameter as `TIMESTAMP`, so the driver applies time-of-day
    TZ conversion (~5h across midnight). **Fixed for `AcCalendario.cal_fecha`** by mapping it as
    Hibernate's `date` type (binds `as [DATE]`, TZ-robust, no schema change — the column was
    already `DATE`). The same mapping smell may exist on other date-only fields.
  - **Validated**: with the date-type mapping in place, the connection TZ no longer matters for the
    day-filter — the default `serverTimezone` was switched to `UTC` (externalized via `${JDBC_URL}`)
    and the activity test passes, confirming the fix is robust on UTC-based cloud envs rather than
    relying on a `serverTimezone=America/Lima` stopgap.
  - Remaining (cross-schema, decide scope): (1) audit all `DATE`/`DATETIME`/`TIMESTAMP` columns +
    their Hibernate mappings for the same `java.util.Date`→TIMESTAMP mismatch; (2) for genuine
    date-only business fields stored as `DATETIME`, narrow to `DATE` (additive review needed);
    (3) standardize table collations to `utf8mb4` (DB default is `utf8mb4_unicode_ci` but tables
    are still on `utf8_general_ci`/`latin1_swedish_ci` — collation conflicts in joins/comparisons);
    (4) document an explicit TZ strategy across App / JDBC / DB / containers so the cloud migration
    doesn't regress. Other UCH apps reach this DB via plain JDBC, so cross-app blast radius of
    these changes is low.
- [ ] **Teacher attendance check-in/check-out validation breaks across midnight.**
  `MbAsistenciaDocente.validarNuevaSesion` builds `checkIn`/`checkOut` as the same calendar date
  with only the hour/minute overridden, then rejects with `checkIn.isAfter(checkOut)` if the
  session crosses midnight (e.g. check-in at 23:xx, check-out at 00:xx the next day reads as
  23 > 00 and gets rejected as "hora de inicio no puede ser mayor que la de fin"). Real bug, not
  just test flakiness — `MbAsistenciaDocenteTest` only happens to fail when run close to midnight
  because it derives its check-in/out times from `LocalTime.now()`. Needs `checkOut` to roll to
  the next day when its hour is less than `checkIn`'s.

#### 🟢 Advisory

- [ ] **Anemic domain model** — entities are pure data (`mapping/base` + subclass); logic lives in
  beans. Idiomatic for hbm2java; low priority.
- [ ] **Parallel-model duplication** between `academica` and `cursolibre`
  (`MbMatricula`/`MbMatriculaCL`, etc.) — mirrored by design; extract shared logic only if it stays in sync cheaply.
- [ ] **Characterization tests before refactor** — add safety-net tests on the riskiest beans
  (`MbPago`, `MbMatricula`) prior to any extraction. (`MbMenu` is the only managed bean with no test.)

> **Verified already compliant** (no action): feature-oriented packaging, explicit XML bean wiring
> (no component-scan sprawl), specific + logged exception handling (no broad swallows), stateless DAO
> singletons, and behavior-based integration tests against real DB rows.

---

### Enrollment Module — AS-IS Findings (2026-06-26)

Reverse-engineering pass against `MbMatricula`/`EnrollmentService`; full evidence trail in
`docs/Enrollment_Module_AS-IS_Report.md` and `docs/Schedule_Conflict_Validation_Evaluation.md`.
Domain rules/design live in `docs/modules/academica.md` — this is just the debt/issue tracking view.

#### Fixed

- [x] **N+1 query in the enrollment status lookup** — `seleccionar()` loaded each matched student's
  status one query at a time (18 queries for 18 students). Batched via
  `EnrollmentService.findEnrollmentSnapshots(...)` (`Restrictions.in(...)`, 3 queries regardless of
  result size). [#27](https://github.com/cesardl/sistema_academico-uch/issues/27) (PR
  [#25](https://github.com/cesardl/sistema_academico-uch/pull/25)).
- [x] **No audit trail on `AcMatricula`** — added `createdAt/createdBy/updatedAt/updatedBy` via the
  existing Strategy A pattern. [#28](https://github.com/cesardl/sistema_academico-uch/issues/28)
  (PR [#25](https://github.com/cesardl/sistema_academico-uch/pull/25)).
- [x] **Enrollment search returned students with an invalid `AluTipo`** (Retirado/Anulación/En
  espera — 30 live rows confirmed enrollable) — pushed a DB-level eligibility filter into a new
  `seleccionarAlumnosElegiblesParaMatricula(...)` query, deliberately *not* a post-fetch Java
  filter (same wasted-transfer shape as the N+1 above).
  [#29](https://github.com/cesardl/sistema_academico-uch/issues/29) (PR
  [#26](https://github.com/cesardl/sistema_academico-uch/pull/26)).
- [x] **Schedule-conflict check hardcoded to `sem_id=9`** — silently returned "no conflict"
  unconditionally for every semester except one fossilized historical one (a coincidence with the
  one semester id every test happened to use, which is why it went undetected). Parameterized to
  `:sem_id`. [#30](https://github.com/cesardl/sistema_academico-uch/issues/30) (PR
  [#26](https://github.com/cesardl/sistema_academico-uch/pull/26)).
- [x] **`AluEstado` never validated during enrollment** (Regular/Observado/Conafu) — added to the
  same DB-level filter as the `AluTipo` fix above.
  [#31](https://github.com/cesardl/sistema_academico-uch/issues/31) (PR
  [#26](https://github.com/cesardl/sistema_academico-uch/pull/26)).
- [x] **No logging on enrollment blocks** (debt / schedule-conflict / no-vacancy) — added `LOG.warn`
  with enough context to diagnose without reproducing live.
  [#32](https://github.com/cesardl/sistema_academico-uch/issues/32) (PR
  [#26](https://github.com/cesardl/sistema_academico-uch/pull/26)).

#### Pending

- [x] **Prórroga grant → `AdAlumnoTarifa.AlutarFechaProrroga` linkage — RESOLVED (#45, then #33).**
  Registering a Prórroga now atomically appends `AdProrroga` history and syncs the deadline column
  the debt gate reads (via `ProrrogaService`); the latest-registered extension wins (highest
  `pro_numero`) and every writer keeps history.
  [#33](https://github.com/cesardl/sistema_academico-uch/issues/33)
- [ ] **Section-capacity check-then-insert race condition.** Vacancy check and insert aren't atomic
  at the section level under concurrent submissions for the same last seat.
  [#34](https://github.com/cesardl/sistema_academico-uch/issues/34)
- [ ] **No credit-limit or repeated-failure-limit enforcement** — confirmed absent, not just
  unconfirmed. Needs a product decision on whether either is institutional policy.
  [#35](https://github.com/cesardl/sistema_academico-uch/issues/35)

---

### Patterns Implemented

Reusable patterns established in this codebase — apply these rather than inventing a new shape
when the same problem recurs elsewhere.

- **Service layer (`@Transactional` use-case services).** Template: `GradeRecordService` (first),
  `EnrollmentService` (second). Declared *explicitly* in `applicationContext-services.xml` with
  `<constructor-arg ref="springHibernateDao…"/>` — no `<context:component-scan>` (Spring 3.2's
  bundled ASM can't read Java 14 bytecode). `<tx:annotation-driven>` only post-processes
  already-XML-defined beans, so `@Transactional` still works. Only safe to wrap DAOs using plain
  `HibernateTemplate`; DAOs with manual `session.beginTransaction()` blocks throw under an outer
  transaction until those manual blocks are removed first (see the 🔴 blanket-catch-all note above).
  Next candidates: `MbConvalidacion` ([#24](https://github.com/cesardl/sistema_academico-uch/issues/24)),
  payments (`MbPago`/`MbEstructuraPago`, not yet scoped).
- **Audit fields (Strategy A: Hibernate `EmptyInterceptor`).** `Auditable` interface +
  `AuditInterceptor` + `CurrentUserContext` (`ThreadLocal`) + `AuditUserFilter`. Stamps
  `createdAt/createdBy/updatedAt/updatedBy` on entity load/flush — **does not fire on bulk HQL
  updates**, so any bulk update on an `Auditable` entity must be converted to load-then-update first
  (done for `AcMatricula.eliminarMatricula`/`actualizarTipoMatricula`). Applied so far: `AcActa`,
  `AcSemestre`, `AcAlumno`, `AcMatricula`. Reuse directly for the next audited entity — no new
  pattern needed.
- **DB-level filtering over post-fetch Java filtering.** When adding an eligibility/visibility rule
  to an existing query, push it into the `Criteria`/SQL itself rather than filtering an
  already-fetched `List` — filtering after the fact wastes the same wire-transfer-and-discard cost
  an N+1 fix would otherwise eliminate. Established via the enrollment eligibility fix
  (`seleccionarAlumnosElegiblesParaMatricula`); the original shared method's other 3 callers were
  preserved by extracting a private `buildAlumnoSearchCriteria(...)` helper rather than duplicating
  or branching the existing method.
- **Batched lookups via `Restrictions.in(...)`** instead of one query per id in a loop — the N+1 fix
  template (`EnrollmentService.findEnrollmentSnapshots`). Keep single-id overloads with explicit
  `FetchMode.JOIN` chains separate from batched List-overloads — joins useful for one full object
  graph become wasted width when applied across a whole batch most callers don't need fully
  hydrated.

---

### AI Assistant Guidelines

When contributing to this project:

- Prefer minimal-impact refactors.
- Avoid breaking legacy behavior.
- Maintain backward compatibility.
- Prioritize readability and testability.
- Suggest unit and integration tests for new business logic.
- Keep solutions simple and pragmatic.
- Respect the existing architecture unless migration is explicitly requested.
- Be careful with JSF lifecycle behavior and managed bean scopes.
- Avoid unnecessary modernization that introduces risk into stable legacy modules.

---

### For remote-execution: when 4 test-run phases executed it's important to execute this query to avoid DB locks.

```
DELETE
FROM ac_acta_detalle
WHERE act_id IN (SELECT act_id
                 FROM ac_acta a
                 WHERE a.act_creacion > '2026-06-01')
;
DELETE
FROM ac_acta
WHERE act_creacion > '2026-06-01';
```
