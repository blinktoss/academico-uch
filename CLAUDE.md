# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Sistema Académico (UCH) — a Java EE academic management webapp (enrollment, grades, schedules, payments, attendance) built on **JSF 1.2 + RichFaces 3.3 + Spring 5.3.39 + Hibernate 5.4.24**, packaged as a WAR and deployed to Tomcat. Java source/target level is 11. On `main` since 2026-07-09 (PR #60); this is the suite's **final baseline** — Spring 6 / Hibernate 6 (Jakarta) are ruled out while the JSF 1.2 frontend stays. See `uch-env/docs/Backend_Foundation_Charter.md`.

### Ecosystem context

This repo is the **core application** in a wider UCH (Universidad de Ciencias y Humanidades) stack of cooperating apps, all backed by a **single central MySQL 5.6 database**:

- **academic-system (this repo)** — core system: students, teachers, enrollment, free courses, grades, payments, etc. Source of truth other apps read from.
- **intranet** — student portal for those registered in the academic system.
- **checkout** — payment system for enrollment-period payments; carved out part of this app's original payment functionality plus extra payment features.
- **survey** — create/publish surveys; surfaced as a popup in the intranet on student login.
- **scoring** — processes admission-exam results; qualifying candidates who secure a vacancy become students managed here.
- **admissions** — full admissions flow, from candidate registration to exam day.
- **reports** — collection of reports.

**Implication for schema changes:** because the MySQL DB is shared across all these apps, altering tables here (columns, types, constraints) can affect apps outside this repo that read the same schema. Prefer additive, backward-compatible migrations (new nullable columns, widening types) over destructive ones, and keep migrations as scripts under `db/migrations/`.

Docker setup for the whole stack lives in a separate repo and represents the **PROD** environment; a separate container is used for development. This session's work is scoped to **academic-system only**.

## Development Workflow

**All changes in this and every UCH repo must follow the branching & PR workflow documented in the root [`CLAUDE.md`](../CLAUDE.md#branching--pull-request-workflow):**

- **One task/feature/fix** → **One branch** → **Multiple commits (as needed)** → **One PR**
- Never commit or push directly to `main`. Create a branch from `main`, make all related commits in that branch, then open a PR when complete.
- Branch naming: `feat/`, `fix/`, `docs/`, `refactor/`, etc. + short description.

This is a suite-wide rule. If you're starting a new task, create a fresh branch; do not reuse branches across multiple unrelated tasks.

## Module guides

Per-module documentation lives under [`docs/modules/`](docs/modules/) — domain rules, design
decisions, and pending work that aren't obvious from the code. **Read the relevant module guide
before working on that module.** Start at [`docs/modules/README.md`](docs/modules/README.md) for the
index and the "what goes where" split (module guides = domain/design; README.md = tech-debt backlog;
this file = build/conventions). First populated: [`academica.md`](docs/modules/academica.md)
(Grades/Actas + the service-layer pattern; Enrollment/Matrícula added 2026-06-27).

## Build, Run, Test

```bash
mvn clean package          # build the WAR (target/sistema_academico.war)
mvn test                   # run the full test suite
mvn test -Dtest=MbHorarioTest                  # run a single test class
mvn test -Dtest=MbHorarioTest#seleccionNodoArbolTest  # run a single test method
mvn org.apache.tomcat.maven:tomcat7-maven-plugin:run  # run locally on port 8084, context /Sistema_Academico
```

### Database setup (dev DB + tests)

Tests and local runs query a **long-lived development MySQL 5.6 instance** (the real Spring/Hibernate context is loaded from `applicationContext-hibernate.xml`), reachable per `src/main/webapp/WEB-INF/hibernate.properties` (default: `jdbc:mysql://localhost:3308/db_sis_academica`). Its data comes from the maintainer's **backup of `db_sis_academica`** — that backup is owned by the maintainer and **out of Claude's scope** (Claude never creates, restores, or runs it).

```bash
docker run --name mysql-uch-v5_6 -p 3308:3306 --restart on-failure -e MYSQL_DATABASE=db_sis_academica -e MYSQL_ROOT_PASSWORD=rootroot -e TZ='America/Lima' -d mysql:5.6.51 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
```

`db/db_sis_academica_setup.sql` is **not a routine step** — do not run it on every setup or before tests. It is a one-time fixup applied **only after restoring a fresh copy from the backup** (e.g. when accumulated schema/data changes by the maintainer and Claude break the dev DB and the maintainer restores it). It patches legacy data inconsistencies present in the original backup — `0000-00-00` date values, the last student code, data integrity in `ac_curso_aperturado`, plus a few seed menu/role/attendance rows:

```bash
mysql -u root -p -P 3308 --show-warnings
source db/db_sis_academica_setup.sql   # only after a fresh restore, not routinely
```

May need to increase MySQL's `max_connections` (HikariCP pool: maximumPoolSize 25 / minimumIdle 25, fixed).

#### Test-data cleanup (Actas)

The grade-record test paths insert rows into `ac_acta` and `ac_acta_detalle` against the live dev DB and **do not purge them afterwards**. They accumulate across runs and, after roughly **4 test-run cycles**, the volume can cause table locks that make further test runs hang. After that many cycles (or whenever test runs start blocking), purge the test-created actas — child rows first to respect the FK, scoped by creation date so real backup data is untouched:

```sql
DELETE FROM ac_acta_detalle
WHERE act_id IN (SELECT act_id FROM ac_acta a WHERE a.act_creacion > '2026-06-01');

DELETE FROM ac_acta
WHERE act_creacion > '2026-06-01';
```

Adjust the `act_creacion` cutoff to a date just before the current testing period so only test-run rows are removed. This is a maintenance step for the dev DB, not part of a normal build.

### Docker

```bash
docker build -t uch-sistema-academico .
docker run -p 8084:8080 --name uch-sistema-academico --network uch-net -d uch-sistema-academico
```

The Dockerfile just copies `target/*.war` into a `tomcat:9.0.119-jre11` image, so `mvn package` must run first.

### Static analysis

`sonar-project.properties` defines SonarQube settings (sources `src/main/`, JaCoCo coverage via `target/site/jacoco/jacoco.xml`). JaCoCo is wired into `mvn package` via the `prepare-agent`/`report` goals.

## Architecture

### Layering pattern (per business entity)

Each persistent entity follows a consistent four-layer pattern, repeated across modules:

1. **Hibernate mapping**: `net/uch/hbm/<Entity>.hbm.xml` — provided by the shared **`net.uch:uch-shared-model`** library (repo `lib-shared-model-uch`; extracted verbatim from this repo, which remains the mappings' source of truth). Registered in `src/main/webapp/WEB-INF/applicationContext-hibernate.xml`'s `sessionFactory.mappingResources` list, resolved from the jar.
2. **Domain classes**: `net.uch.mapping.<Entity>` extends a generated `net.uch.mapping.base.Base<Entity>` (hbm2java-style generated base + hand-written subclass with custom getters/constants like `PROP_PLAN_ACTIVO`) — also provided by `uch-shared-model`, packages unchanged.
3. **DAO**: `net.uch.<module>.dao.HS<Entity>DAO` interface + `net.uch.<module>.dao.impl.HS<Entity>DAOImpl` extending Spring's `HibernateDaoSupport`, using `getHibernateTemplate()` with HQL strings or `DetachedCriteria`/`Restrictions`.
4. **Spring wiring**: every DAO impl is registered twice in `applicationContext-hibernate.xml`:
   - `hibernateSpringDaoTarget<Entity>` — the raw DAO bean with `sessionFactory` injected.
   - `springHibernateDao<Entity>` — a `TransactionProxyFactoryBean` wrapping the target, with declarative tx rules (`get*`/`find*`/`load*` → `PROPAGATION_REQUIRED,readOnly`; `store*`/`add*` → `PROPAGATION_REQUIRED`).

   Adding a new entity/DAO requires: the entity class + hbm.xml added to `uch-shared-model` (new lib release, additive only), the version bump here, the `mappingResources` registration, and both Spring bean entries.

### JSF managed beans (controllers)

- Located in `net.uch.<module>.jsf` (e.g. `net.uch.academica.jsf.MbHorario`), registered in `src/main/webapp/WEB-INF/faces-config.xml`.
- Beans look up DAOs via `ServiceFinder.findBean("springHibernateDao<Entity>", HS<Entity>DAO.class)` — a static helper (`net/uch/common/ServiceFinder.java`) that pulls beans from the Spring `WebApplicationContext` attached to the servlet context. DAOs are typically resolved as instance-field initializers, not via `@Autowired`. **`ServiceFinder` is frozen legacy** (charter §5): it stays for existing beans, is never propagated to other apps, and is replaced by the `SpringBeanFacesELResolver` pattern only when a bean is substantially reworked anyway.
- `ServiceFinder.getLoggedUser()` reads the `BeanUser` from the HTTP session attribute `"usuario"`; beans commonly throw `UserNotFoundException` from a `@PostConstruct init()` if no user is logged in (see `MbHorario.init()`).
- Common base classes in `net/uch/common/`: `AbstractBaseMbViewTree` (RichFaces tree-based beans), `AbstractBaseMbEdicionEstructuraPagoBloque`, plus shared helpers `Print`, `SystemHelper`, `Constants` (catalog codes, image paths, param names). `TB_CATALOGO`-backed lookup codes are a concatenation of `Grupo` (3 chars) + `Elemento` (3 chars), giving the `NNNNNN` numeric scheme referenced throughout `Constants`/`Stubs` — e.g. `015002` = group `015` (payment voucher type) + element `002` = EFECTIVO; `009001` = group `009` (student type) + element `001` = Estudiante; `036001`/`036003` = group `036` (grade record type) + element `001`/`003` = Principal/Convalidación. Same pattern across every business domain that uses `TbCatalogo`.
- "Bean" support classes (row/table view models, not Spring beans) live under `net.uch.<module>.jsf.beans` (e.g. `BeanNotas`, `ObjNodeGnral` for tree nodes, `ObjUpdate`).

### JSF views (JSP pages) & layout

- Pages are JSPs under `src/main/webapp/<Module>/` (e.g. `Academica/Alumnos.jsp`, `Administrativa/Saldos.jsp`). Each page includes the shared chrome with `<jsp:include page="../TablaSistema/Menu.jsp"/>` and implements its own feature inside a `<rich:panel>` / `<h:form>`.
- `TablaSistema/Menu.jsp` is the common base for every page: it guards the session (forwards to `index1.jsp` if session attribute `"usuario"` is null), loads global styles via `<a4j:loadStyle src="resource:///css/formulario.css"/>` (+ `richStyle.xcss`), sets the favicon, and renders the main app toolbar (`rich:toolBar` bound to `managerLogin.menu.toolBar`). Because the CSS is loaded here, individual pages don't link it themselves.
- **Layout convention:** legacy pages use HTML `<table>` for *layout*; new/modernized pages use `<div>` + flexbox utility classes from `src/main/webapp/css/formulario.css`: `.row` (flex container), `.col` / `.col-3` / `.col-10` (columns), `.colspan-2` (simulated colspan), `.uch-label` (fixed-width right-aligned label), `.uch-input` (flexible input). Migrated references: `Administrativa/Saldos.jsp`, `Academica/Notas.jsp`. Not-yet-migrated table example: `Academica/Alumnos.jsp`.
- Tables remain appropriate for genuinely **tabular data** (`rich:dataTable`, grade matrices like the `rich-table` blocks in `Notas.jsp` modals); migrate only table-based *layout* to flex divs, incrementally (including modal panels).

### Module breakdown (`src/main/java/net/uch/`)

- `academica` — core academic domain: students, courses, schedules, enrollment, grades, sections, evaluation systems, attendance, curricular plans.
- `administrativa` — payments, fees, balances, payment structures, extensions ("prórrogas").
- `cursolibre` — "free courses" (CL-prefixed entities: separate parallel curricular/enrollment/grading model from `academica`).
- `asistencia` — teacher attendance tracking.
- `banco` — bank reconciliation / payment-file import.
- `configuracion` — reference data: faculties, specialties, classrooms, locals, semesters, menus, roles, users.
- `autenticacion` — login (`MbLogin`, `BeanUser`).
- `common` — `ServiceFinder`, `Constants`, shared abstract managed-bean base classes.
- `mapping` / `mapping/base` — Hibernate entity classes (hand-written subclass + generated base pair).

### Test conventions

- Tests extend `net.uch.SpringConfigurationTest`, which boots a real Spring `WebApplicationContext` from `file:src/main/webapp/WEB-INF/applicationContext-hibernate.xml` + `classpath:hibernateContextForTest.xml`, and mocks JSF's `FacesContext`/`ExternalContext`/session via `ContextMocker` (`net.uch.util`).
- `session.getAttribute("usuario")` is stubbed to return a logged-in `BeanUser` by default — tests for the "not logged in" path explicitly override it to `null` and expect `UserNotFoundException`.
- `net.uch.Stubs` holds shared fixture constants (IDs of known rows in the dev DB, e.g. `SEMESTRE_2010_2_ID`) — tests run against this real data (restored from the maintainer's backup and patched by `db/db_sis_academica_setup.sql`), not in-memory fakes.
- RichFaces components (`UITree`, `NodeSelectedEvent`, etc.) and JSF events are mocked with Mockito.

### Reports

JasperReports `.jasper` files live in `src/main/webapp/WEB-INF/Reportes/` (boletas, actas, comprobantes, consolidados, etc.), rendered via `a4j:mediaOutput`. Per the README, do not toggle `rendered`/`reRender` on `a4j:mediaOutput` for reports — instead set `cacheable="false" session="true"`.

### Payment voucher type codes (`ad_comprobantes_pago`)

| Code | Type 1 | Type 2 |
|:----:|--------|--------|
| 001  | 015002 EFECTIVO | 015003 SALDO |
| 002  | 015001 VOUCHER - COMPROBANTE ||
| 003  | 015004 VALE | 015005 BOLETA-VALE |
| 004  | 015006 TRANSFERENCIA | 015007 PAGO BANCO |

### Suite foundation role (charter, 2026-07-11)

Per `uch-env/docs/Backend_Foundation_Charter.md`, this repo's mapping layer (`net.uch.mapping`, `net.uch.mapping.base`, `net/uch/hbm/*.hbm.xml`) is the **suite's source of truth for the shared `db_sis_academica` schema** and was moved **verbatim, packages preserved** into the shared library `net.uch:uch-shared-model` (repo `lib-shared-model-uch`, PR #65 merged); adoption here was a pure dependency swap (local copies deleted, jar added — `mappingResources` paths unchanged). Related standing rules: eager→lazy fetch changes are a **separate later phase** (never bundled with the extraction); new service work uses `@Transactional` + constructor injection (issue #24 direction) — the `TransactionProxyFactoryBean`/`HibernateTemplate` wiring is frozen for existing code; c3p0 → HikariCP is **done (#62, `HikariDataSource` `uch-academico-pool`, HikariCP 5.1.0)**; logging is standardizing on **Log4j 2** (`uch-env/docs/Logging_Standard.md` — this repo only swaps the slf4j binding + config since its pool is already 5.1.0).

## Context

### Development history

Originally an Eclipse project on Java 7 + MySQL 5.3. The maintainer modernized it while moving from Software Developer → Technical Leader → Technology Manager: migrated to Maven, added unit + integration tests (coverage 0% → 80%+), containerized the DB with Docker, and surfaced/resolved many anti-patterns. During the pandemic the wider **UCH-stack** was created, adding observability, monitoring, reverse-proxy, and security layers (that stack lives in a separate repo and is the PROD environment). Because the app leans on legacy frameworks, expect latent security vulnerabilities and outdated patterns. Current focus: close the feature gap and harden the developer pathway, using AI tooling to accelerate work and raise engineering quality.

### Domain / project scope

Manages the full student lifecycle for a **private** university — enrollment, academic progression, grading, and complementary services.

- **Enrollment** is the most critical operational process. Students belong to a **Faculty** + **Specialty** and must **re-enroll every semester** subject to academic and administrative requirements. Academic structures (semesters, shifts/*turnos*, schedules, courses, sections) must be configured in advance and made available before students can register.
- **Financial gating:** enrollment validates the student's financial status. Students with outstanding debt may be blocked from enrolling or flagged for administrative review; in some cases they keep studying, but progression to the next academic cycle is held until debts are resolved.
- **Grades / Actas:** teachers register grades and generate official records — **Actas (Official Grade Records)**. These hold sensitive data and become **immutable once registered, closed, and published** — never mutate a closed/published Acta.
- **Free Courses (*Cursos Libres*):** optional complementary offerings (Office tools, Databases, Sports, etc.) — a separate parallel curricular/enrollment/grading model from the core academic flow (see the `cursolibre` module).

### Architecture stack

- **Frontend:** JSP, JSF 1.2, RichFaces 3.3, JavaScript, CSS
- **Backend:** Java (source/target 11), Spring 5.3.39, Hibernate 5.4.24 — the suite's final baseline (no Jakarta migration)
- **Database:** MySQL 5.6 (shared across the UCH stack — see Ecosystem context)
- **Build:** Maven · **Testing:** JUnit, Spring Test, Mockito · **Infra:** Docker

### Development conventions

- **Sole developer** project.
- **Language:** the codebase is largely Spanish (built in Peru). All **new or rewritten code uses English** naming wherever possible; existing legacy names stay as-is.
- **Logging: Log4j 2 — ✅ DONE + verified 2026-07-12** (suite standard, see [`uch-env/docs/Logging_Standard.md`](../uch-env/docs/Logging_Standard.md)). Config is now `src/main/resources/log4j2.xml` (the legacy `log4j.properties` + `slf4j-log4j12` binding were replaced by `log4j-slf4j2-impl` + `log4j-jcl` `2.23.1` + an explicit `slf4j-api:2.0.9` pin; commons-logging excluded from `jasperreports`). Pool was **already HikariCP 5.1.0** (#62), so no pool bump — only the binding + config changed; `Print`/Jasper and the `@Transactional` wiring are logging-agnostic and untouched. Levels carried over verbatim (`org.hibernate=INFO`, `net.uch=INFO`, root WARN). Verified: **403/403 tests green, no NOP warning**. Keep using levels deliberately — observability / log-monitoring tooling may be introduced, so choose the right level rather than logging everything at INFO.
- **Concurrency:** production is expected to serve ~50 concurrent administrative users across different roles.

### Database naming conventions

Table prefixes:
- **`ac_`** — academic tables.
- **`cl_`** — Free Courses (*Cursos Libres*) tables.
- **`tb_`** — general-purpose tables (users, configuration, catalogs).

All **new tables and columns must be named in English** (legacy tables/columns keep their Spanish names). Keep schema changes additive and backward-compatible, written as scripts under `db/migrations/` — the DB is shared across the UCH stack.

**Logical deletion, not physical deletion.** Most tables carry an `*_activo` column (`"1"` active / `"0"` logically deleted — `AluActivo`, `MatActivo`, `SecActivo`, etc.). Rows are essentially never `DELETE`d; queries filter `*_activo = '1'` to exclude soft-deleted records. New entities should follow this same convention rather than introducing physical deletes, to stay consistent with the rest of the schema and to keep the audit/history trail intact.

#### SQL migration policy

**Don't create a new migration file for every SQL statement.** The decision is driven by the *purpose* of the change, not the number of statements.

**Create a new migration — `db/migrations/Vxxx_description.sql`** (next in the `V1…V14` sequence) when:
- The database **schema** changes (`CREATE`, `ALTER`, `DROP`; indexes; constraints).
- You add or modify **stored procedures, functions, triggers, or views** (e.g. `V13_create_voucher_allocation_procedure.sql`).
- You perform a **significant data migration tied to a schema or functional change** (e.g. `V14_voucher_preview_and_counter_resync.sql`).

**Use `db/db_sis_academica_setup.sql`** (applied after a fresh restore, not routinely) when:
- Adding or updating **seed / reference data**.
- Applying **small `INSERT`/`UPDATE`** statements.
- **Fixing legacy or corrupted data.**
- Making **compatibility adjustments required after restoring the legacy backup** (`db_sis_academica_28-09-2010.sql`).

**Guiding principle — prefer consolidation over fragmentation.** If multiple small SQL changes serve the same purpose, keep them together in `db_sis_academica_setup.sql` rather than spawning many tiny migration files. When in doubt, update `db_sis_academica_setup.sql` unless the change is a true schema evolution or an independent functional milestone.

### Testing conventions

- **Working coverage target: ~90%+** (the README states a 95%+ aspirational goal; there is no enforced JaCoCo coverage gate in `pom.xml`).
- Tests run as **integration tests against the live development MySQL 5.6** instance (real Spring/Hibernate context), not in-memory fakes — see Database setup.
- New business logic should ship with unit and/or integration tests.

### Known technical debt

- Legacy JSP views with table-based layouts.
- JSF 1.2 lifecycle complexity.
- RichFaces 3.3 is deprecated.
- Legacy anti-patterns in the service and DAO layers.
- Pending security review (latent vulnerabilities from old frameworks).
- Incomplete test coverage in some legacy modules.

### Working guidelines for AI contributions

- Prefer **minimal-impact refactors**; don't break legacy behavior.
- **Maintain backward compatibility** — doubly important because the MySQL schema is shared across the UCH stack.
- Prioritize **readability and testability**; suggest unit + integration tests for new business logic.
- Keep solutions **simple and pragmatic**; respect the existing architecture unless migration is explicitly requested.
- Be **careful with JSF lifecycle behavior and managed-bean scopes**.
- **Avoid unnecessary modernization** that introduces risk into stable legacy modules.
