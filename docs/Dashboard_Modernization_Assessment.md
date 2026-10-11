# Dashboard Modernization Assessment (Spring 4 + Hibernate 5 Branch)

**Date:** 2026-07-06
**Baseline:** branch `upgrade/hibernate5` — Spring 4.3.30, Hibernate 5.4.24, Java 11, JSF 1.2 + RichFaces 3.3.3, MySQL 5.6 (393/393 tests green per the branch's own migration docs)
**Type:** analysis and proposal only. No code was written, no application behavior was changed, no database objects were created.
**Scope boundary:** this assessment covers only `sistema_academico`'s post-login home page (`Autenticacion/Bienvenida.jsp` / `MbDashboard`). It does not analyze `main` (Hibernate 3.6) and does not touch navigation.

---

## Executive Summary

The current dashboard is small and shallow enough to extend safely: one request-scoped bean (`MbDashboard`), three DAOs, four HQL count queries, zero AJAX, zero JSON. The real data scale is tiny (2,821 students, 10,605 enrollment rows, 58,608 payments, 2,226 grade records, 5 faculties — measured directly against the dev DB, not estimated), which changes the calculus for several of the questions below: at this size, raw query performance is not the constraint, so the simplest architecture wins on its own merits, not just on principle.

| # | Question | Recommendation |
|---|---|---|
| 1 | Current architecture | Well-understood, low coupling; the dashboard bypasses the service layer the rest of the app has started adopting — worth fixing before it grows (§3.1). |
| 2 | Chart library | **Chart.js** (MIT, v4.5.1, actively maintained, zero dependency on any JSF/RichFaces component — verified via web search, §2). |
| 3 | Dashboard architecture | Introduce a `DashboardService` now (same XML+constructor-injection template as `EnrollmentService`); gate widgets on the **existing** `BeanUser.grantedModules` set — no new authorization plumbing needed (§3). |
| 4 | Database strategy | **Option A** (DAO/Hibernate, the existing pattern) **+ a simple TTL application cache** via Spring's built-in caching abstraction. Views/triggers are not justified at this data volume (§4). |
| 5 | REST API | **No.** The app already has an authenticated, role-gated, non-JSF servlet pattern (`/report`, `/foto`, `/comprobante` via `ResourceAuthorizer`) — reuse it if live refresh is ever needed, instead of standing up a REST layer on top of a credential-storage mechanism that has a real, independent weakness (§5, §7). |
| 6 | Performance | Current volume is trivial for any option; the only genuine action items are four missing composite indexes (§6). |
| 7 | Security impact | Authorization (role→module cascade, session-cookie hardening, screen-level filter) is genuinely solid — the weak point is credential storage, and it's pre-existing, not introduced by this work (§7). |
| 8 | Open decisions | Five items need your call before implementation — see §8. |
| 9 | Roadmap | Four phases, schema-change-free until Phase 4, and Phase 4 is conditional on the cache proving insufficient (§9). |

---

## 1. Current Architecture Analysis

### 1.1 Login flow

Authentication is a plain JSF action, not a servlet filter:

- `Autenticacion/Login.jsp` submits to `MbLogin.checkUser()` (`net.uch.autenticacion.jsf.MbLogin`).
- `checkUser()` calls `HSUsuarioDAO.validateUser(usuario, password)` → `HSUsuarioDAOImpl.validateUser()` (`net/uch/configuracion/dao/impl/HSUsuarioDAOImpl.java:41-70`), which:
  1. Looks up `tb_usuario` by username (`usu_activo='1'`).
  2. Rejects if `usu_locked='1'`.
  3. Decrypts the stored password with Jasypt (`StandardPBEStringEncryptor`, passphrase hardcoded to the literal string `"jasypt"` — see §1.5 and §7) and compares.
  4. On success, resets the failed-attempt counter and stamps last-login; on failure, increments `usu_failed_attempts` and locks the account permanently at 5 attempts.
- On success, `MbLogin` builds a `BeanUser` (`net.uch.autenticacion.jsf.beans.BeanUser`) — `rowUsuId`, `nivel`, `rolId`, `superAdmin` (`usu_id == 1`), `grantedModules` (a `Set<String>` of module tokens from `HSMenuDAO.findGrantedModules(rolId)`) — and stores it in the HTTP session under the key `"usuario"`.
- Every page's shared chrome (`TablaSistema/Menu.jsp`) forwards to `index1.jsp` if `session.getAttribute("usuario")` is null. This is the page-level guard; §1.5 covers the deeper one.

### 1.2 Home page & current KPI computation

- Landing view: `Autenticacion/Bienvenida.jsp`. Bean: `MbDashboard` (`net.uch.inicio.jsf.MbDashboard`, **request-scoped**, registered as `managerDashboard`).
- `@PostConstruct init()` throws `UserNotFoundException` if `ServiceFinder.getLoggedUser()` is null (the same pattern used across the app), then calls `loadIndicators()`.
- The bean wires **three DAOs directly**, bypassing the service layer entirely:

```java
private final HSSemestreDAO daoSemestre = ServiceFinder.findBean("springHibernateDaoSemestre", HSSemestreDAO.class);
private final HSAlumnoDAO daoAlumno = ServiceFinder.findBean("springHibernateDaoAlumno", HSAlumnoDAO.class);
private final HSMatriculaDAO daoMatricula = ServiceFinder.findBean("springHibernateDaoMatricula", HSMatriculaDAO.class);
```

- The four KPIs, in order of appearance on the page:

| KPI | Source | Query shape |
|---|---|---|
| Current Semester | `daoSemestre.seleccionarSemestreActual()` | `DetachedCriteria` on `AcSemestre`, `sem_activo='1' AND sem_actual='1'`, required-unique-result |
| Total Active Students | `daoAlumno.getActiveStudentCount()` | `select count(a.Id) from AcAlumno a where a.AluActivo='1' and a.AluTipo=:tipo` |
| Enrolled This Semester | `daoMatricula.getEnrolledStudentCount(semId)` | `select count(distinct m.Alu.Id) from AcMatricula m where m.MatActivo='1' and m.SemId=:semesterId and m.MatTipo in (:tipoRegular, :tipoPreMatricula)` |
| Enrollment % | computed in the bean | `Math.round(enrolled * 100f / registered)`, guarded against division by zero |

- Rendering is plain HTML (`kpi-card` divs) — **no RichFaces AJAX** (`a4j:*`) and **no JSON serialization library** anywhere in the app (no Jackson/Gson/org.json in `pom.xml`). Any new widget that wants JSON will need either a hand-built string (fine for the simple `{label, value}` shapes these widgets need) or a small new dependency (Jackson is the obvious, low-risk choice) — flagged for Phase 1 in §9, not a blocker.

### 1.3 Service layer conventions on this branch

A service layer exists but the dashboard doesn't use it yet:

- `applicationContext-services.xml` declares services explicitly (constructor injection), deliberately **not** using `<context:component-scan>`. `GradeRecordService` and `EnrollmentService` are the two examples today, both `@Transactional`-annotated classes wired via `<tx:annotation-driven/>` over XML-declared beans.
- Per `docs/modules/academica.md`, this is explicitly **"the template for new services on this stack."**
- *Aside, not actionable here:* the comment justifying the no-component-scan rule in `applicationContext-services.xml` still cites "Spring 3.2's bundled ASM can't read Java 14 bytecode" — but this branch is Spring 4.3.30 targeting Java 11, so that specific justification is stale (copied forward from the legacy branch). Whether Spring 4.3.30's ASM can actually read Java 11 bytecode is untested; regardless, switching to component-scan is an unrelated, larger change and out of scope for a chart feature — noted here only so the comment doesn't get taken at face value later.
- **`MbDashboard` does not have a service and calls DAOs directly** — the same pattern the app is actively moving *away* from elsewhere (see §3.1).

### 1.4 DAO layer pattern

Standard `HibernateDaoSupport` + `TransactionProxyFactoryBean` pairing, same as the rest of the app: HQL with named parameters for aggregations (`getActiveStudentCount`, `getEnrolledStudentCount`), `DetachedCriteria` for single-row lookups (`seleccionarSemestreActual`). Both existing count queries are clean templates to copy for new widget queries — no N+1 risk, no manual session handling.

### 1.5 Security / authorization mechanism

This is more mature than "relatively weak" suggests at first read — the weakness is narrower and specific (see below). The mechanism, confirmed via `docs/authorization.md` and direct code reading:

```
tb_usuario.rol_id ──> tb_roles ──< tb_menu_rol >── tb_menu
```

- **`ScreenAccessFilter`** (`net.uch.common.security`, mapped to `*.jsf`) reads `BeanUser` from the session and calls **`ScreenAccessGuard.isAllowed(user, viewId)`**, which resolves a view's owning module from `faces-config.xml` navigation outcomes and checks it against `user.getGrantedModules()` (bypassed for `superAdmin`). Unmapped/ungated views (login, welcome) pass through.
- **`ResourceAuthorizer`** applies the identical rule to non-JSF servlets: `/report`, `/foto`, `/foto-cl`, `/comprobante`, `/foto-actividad` all require a `Modules` token and return **403** (with `Referrer-Policy: no-referrer`) on failure — this is the precedent §5 leans on.
- Module tokens (`ALUMNOS`, `MATRICULA`, `NOTAS`, `PAGOS`, `EDICION_ESTRUCT_PAGO`, `CONVALIDACION`, `PROCESO_BANCO`, `ALUMNOS_CL`, `NOTAS_CL`, `ACTIVIDADES`) are constants in `net.uch.common.security.Modules`, matching `men_accion` values in `tb_menu`.
- Confirmed roles in the dev data: `admin` (rol 1, `superAdmin`, sees everything), `caja` (rol 2 → `PAGOS`, `EDICION_ESTRUCT_PAGO`, `ALUMNOS_CL`, `MATRICULA_CL`), `academica` (rol 3 → `ALUMNOS`, `NOTAS`, `MATRICULA`, `CONVALIDACION`), `operador` (rol 5 → `ALUMNOS` only).
- Session tracking is pinned to `COOKIE` (`web.xml`), specifically to stop `;jsessionid=` leaking into copied report/print links and enabling session hijacking — a real, deliberate hardening, not an oversight.
- Login lockout (5 failed attempts → permanent lock, `V3_add_login_lockout_tb_usuario.sql`) exists where it didn't before.

**The actual weak point:** password storage. `HSUsuarioDAOImpl` uses Jasypt **reversible** symmetric encryption (`StandardPBEStringEncryptor`) — not a one-way hash — and the encryption passphrase is the hardcoded literal `"jasypt"` (the library's own name), committed to source:

```java
textEncryptor = new StandardPBEStringEncryptor();
textEncryptor.setPassword("jasypt");
```

Anyone with read access to the source (which now includes this assistant) or the DB can decrypt every password in `tb_usuario` trivially. There is also **no CSRF protection anywhere in the codebase** (confirmed by grep — expected for a pre-JSF2 app, but worth stating plainly), no rate limiting beyond the 5-attempt lockout, and no Spring Security (hand-rolled everything). Relative to the *rest* of the 7-repo suite, `sistema_academico` is actually the most hardened app (per `docs/Suite_Wide_Architecture_Review.md`, the other apps mostly do plaintext password comparison and have no centralized authorization filter at all) — but that's a low bar, and the Jasypt issue stands on its own regardless of the comparison. **This is pre-existing and independent of the dashboard work; see §8 for a recommendation to track it as its own fix.**

### 1.6 Best extension points

1. **`BeanUser.grantedModules`** is already exactly the role signal needed for widget-level gating — no new authorization mechanism required (§3.2).
2. **The `applicationContext-services.xml` service template** is the right home for dashboard business logic, not more DAO fields on the bean (§3.1).
3. **`ResourceAuthorizer` + `Modules`** is the right home for any future data-refresh endpoint, if one is ever needed (§5).

---

## 2. Recommended Chart Library

### 2.1 Candidates considered

| Library | Verdict | Why |
|---|---|---|
| **Chart.js** | **Recommended** | Pure client-side (canvas), zero coupling to any server-side component library — it doesn't know or care that the page is JSF 1.2. MIT license, v4.5.1, actively maintained (confirmed via web search, July 2026). Covers pie/line/area/bar natively. Tiny footprint, vendored as one static `.js` file — no CDN dependency, no license cost. |
| Apache ECharts | Credible #2 | Same client-side, framework-agnostic story; more built-in chart types/interactivity than needed here. Heavier. Worth it only if the widget set grows well beyond what's proposed. |
| PrimeFaces charts | **Disqualified** | PrimeFaces is a competing JSF component library. Versions compatible with JSF 1.2 (pre-JSF2) are ~2.x, from around 2010, unmaintained and stale. Running it alongside RichFaces 3.3 risks resource/AJAX-lifecycle collisions even where versions did line up. This isn't a stylistic preference — it's likely a compatibility non-starter with this specific JSF 1.2 runtime. |
| Google Charts | Not recommended | Client-side, but the default loader pulls from Google's CDN at runtime — a poor fit if the production network is restricted/on-prem (consistent with how this suite already treats prod vs. local dev as distinct network contexts elsewhere). |
| Highcharts | Not recommended | Excellent quality, but commercial licensing for non-personal use — unjustified cost/procurement overhead for four chart types. |
| JFreeChart | Not recommended, but the "safe fallback" | The classic JSF-native approach: server renders a PNG via a servlet, page embeds it with `<h:graphicImage>`. Zero client JS. But static images mean no hover/tooltips/legend-toggle, which undercuts the point of "modernizing" the dashboard. Mentioned only because it's the lowest-risk fallback if client-side JS were ever unacceptable — it isn't; the app already ships JS via RichFaces. |
| D3.js | Not recommended | Extremely capable but low-level — you build charts from primitives. Overkill and directly against the brief's KISS instruction for four standard chart types. |

### 2.2 Integration shape (still analysis — describing, not implementing)

Chart.js needs a `<canvas>` element and a JSON data array per widget; nothing about it requires a REST endpoint (that question is fully answered in §5). The simplest integration:
- Vendor `chart.js` as a static resource (`resources/js/`), included once in the shared page chrome the way `formulario.css` already is.
- Feed each widget's data as an inline JSON literal, rendered server-side by the bean/EL (e.g. a getter returning a JSON string) directly into a `<script>` block on page load — same request-scoped rendering the four existing KPIs already use, so no new security surface is introduced.

---

## 3. Recommended Dashboard Architecture

### 3.1 Don't grow `MbDashboard` into another God Bean — introduce `DashboardService` now

`docs/modules/academica.md` documents `MbNota` as a known, actively-being-fixed "God Bean" (~1,250 LOC) — the remedy pattern is extracting a `@Transactional` service declared explicitly in `applicationContext-services.xml`, following `GradeRecordService`/`EnrollmentService`. `MbDashboard` is small today (3 DAO fields) specifically *because* it only has 4 KPIs. Adding 8 more widgets the same way — wiring DAOs directly into the bean — reproduces the exact pattern the team is already paying down elsewhere. Recommendation: add a `DashboardService` (or one per role-group if that reads more cleanly — `AcademicDashboardService`/`CashierDashboardService`) now, before the bean grows, using the established template. This is a Single-Responsibility call: `MbDashboard` should render; a service should assemble the numbers.

### 3.2 Role-gating: reuse the existing grant model, add nothing new

`BeanUser.grantedModules` (populated at login from `tb_menu_rol`) already distinguishes exactly the groups the brief asks for:

| Dashboard role | Existing signal |
|---|---|
| Academic | `grantedModules` contains `NOTAS` or `MATRICULA` |
| Cashier | `grantedModules` contains `PAGOS` |
| Administrator | `superAdmin == true` |

This means widget visibility can be a `rendered="#{managerDashboard.showX}"` check backed by a one-line grant lookup — **no new `tb_menu` rows, no new filter, no new authorization code.** This is deliberately simpler than extending `ScreenAccessFilter`/`ResourceAuthorizer`, which protect whole *screens* (separate URLs); dashboard widgets are fragments of one existing screen, so screen-level gating machinery doesn't apply here — the lighter-weight grant check is the correct-sized tool, not a shortcut.

Whether that mapping should stay a hardcoded `if (grantedModules.contains(...))` or become data-driven is exactly the question left open for the Product Owner in §8 — this section only establishes that the *signal* to gate on already exists.

### 3.3 Widget catalog: joins, complexity, and gotchas

| Widget | Role | Tables / join chain | Complexity | Notes |
|---|---|---|---|---|
| Students by Faculty (Pie) | Academic | `ac_alumno → ac_especialidad → ac_facultad` (2 FK hops, direct) | **Low** | Only 5 faculties (confirmed row count) — a clean, small pie chart by construction. |
| Enrollment Trend (Line) | Academic | `ac_matricula → ac_semestre` | **Low, with a real gotcha** | Enrollment rectification is **append-only** (`docs/modules/academica.md`): a corrected enrollment leaves the superseded row in place, retagged `022004`/`022006`. A naive `COUNT(*) GROUP BY sem_id` over-counts. Must reuse the **exact** filter the existing `getEnrolledStudentCount` HQL already applies (`mat_activo='1' AND mat_tipo IN (022001, 022005)`), not a fresh handwritten query. |
| Academic Status Distribution (Pie) | Academic | `ac_alumno` (catalog group `011` on `alu_estado`) | **Low** | Single table; labels need a catalog lookup, which is a solved pattern already used throughout the app (`TB_CATALOGO` group+element scheme). |
| Grade Record Status (Pie) | Academic | `ac_acta` (+ section→specialty→faculty if broken out by faculty) | **Medium–High** | `act_status` is, in practice, **almost always `PUBLISHED`** — DRAFT is a derived, in-memory-only state (`docs/modules/academica.md`: "Only PUBLISHED rows exist in `ac_acta`"). A plain `GROUP BY act_status` is close to a degenerate 100%-published chart and won't tell anyone anything useful. The *meaningful* version needs a `LEFT JOIN`/`NOT EXISTS` between "sections that should have a Principal Acta this semester" and "sections that actually have one PUBLISHED," which is a materially different (and pricier) query than it first looks. If broken out by faculty, note that section→faculty is **not a direct FK** — it runs through the curriculum plan, and neither research pass could confirm that exact join chain; treat as a pre-implementation spike, not a given. |
| Payments Processed Today (KPI) | Cashier | `ad_pago` | **Low** | Single table, `SUM(pag_monto) WHERE DATE(pag_fecha)=CURDATE() AND pag_activo='1'`. |
| Payments by Concept (Pie) | Cashier | `ad_pago → ad_concepto_pago` | **Low** | One FK join, `GROUP BY conpag_id`. |
| Daily Revenue Trend (Area/Line) | Cashier | `ad_pago` | **Low** | Single table, `GROUP BY DATE(pag_fecha)` over a date range. |
| Outstanding Debt by Faculty (Bar) | Cashier | Either `ad_saldo → ac_alumno → ac_especialidad → ac_facultad`, **or** `ad_alumno_tarifa` (filtered on `AlutarEstado`) via the same chain | **Medium** | Two independent research passes surfaced **two different candidate sources of truth** — `ad_saldo.sal_monto` (a running balance) and `ad_alumno_tarifa.AlutarEstado`/`AlutarFechaProrroga` (the per-line status this session's own mora work reads/writes). They may represent different things (running ledger vs. per-obligation-line status) and need product/data verification before picking one — flagged again in §8. Faculty attribution itself (`esp_id → fac_id`) is a confirmed, direct, cheap join regardless of which source wins. |
| Administrator (all of the above) | Admin | — | Sum of the above | No new queries — just no grant filter applied (`superAdmin` bypasses §3.2's check entirely, consistent with how it already bypasses `ScreenAccessGuard`). |

Sizing above is deliberately qualitative (Low/Medium/High), not day-estimates — there's no track record in this codebase for "how long does one dashboard widget take" to calibrate against, unlike the Hibernate/Spring migration docs in this same `docs/` folder, which had prior completed phases to measure from.

---

## 4. Database Strategy

### 4.1 The four options, evaluated

| Option | Pros | Cons |
|---|---|---|
| **A. DAO + Hibernate queries** | Matches 100% of existing conventions (§1.4); zero new infrastructure; easiest to test with the existing `SpringConfigurationTest` harness. | Every widget re-runs its aggregation on every page load — fine at today's volume (§6.1), a real cost only at much larger scale. |
| **B. Database Views** | Hides multi-join queries behind a single `SELECT * FROM v_x`; reduces SQL duplication across the codebase. | **Common misconception worth correcting directly: a plain MySQL view is not materialized.** It is a stored `SELECT`, re-executed in full on every query against it — MySQL has never supported materialized views, in 5.6 or any later version. A view gives code-organization benefits, not performance benefits. If the goal is "reduce repetitive aggregation queries" in the sense of *reducing computation*, a view alone does not achieve that. |
| **C. Views + lightweight services** | Same correction as B applies to the view half; the service half is a legitimate, small win — centralizes the query-shape-to-DTO mapping in one place instead of scattering HQL across beans. Reasonable **stage-2** evolution once/if the widget count grows enough that query duplication becomes a real maintenance cost. | Two moving parts to introduce for a benefit that, at 8-ish widgets, Option A's services (§3.1) already deliver on their own. |
| **D. Triggers / DB-side pre-aggregation** | Genuinely pre-computes (unlike B) — a summary table updated by triggers has real, non-recomputed data, and triggers fire regardless of which of the 7 apps writes the row. | Adds write-path logic to shared, "sacred" tables (`ad_pago`, `ac_matricula` — payment and enrollment writes are explicitly called out project-wide as not to be compromised for tech upgrades). A bug in trigger logic can break an unrelated insert from a completely different app. This is real coordination/blast-radius cost on shared schema, for a dashboard that can tolerate being a few minutes stale. Not proportionate here. |

### 4.2 Recommendation: A, with a TTL cache — nothing else yet

Given the confirmed data volume (§6.1), the actual cost these widgets need to solve for is **"don't run 8 aggregation queries on every single page view,"** not "these queries are individually slow." Spring 4.3 already ships a caching abstraction (`spring-context`, present since Spring 3.1 — no new dependency): add `<cache:annotation-driven/>` next to the existing `<tx:annotation-driven/>` in `applicationContext-services.xml`, back it with a `ConcurrentMapCacheManager` (in-process, zero new infrastructure), and put `@Cacheable` on the new `DashboardService` methods with a short TTL (5–15 minutes is generous for a KPI dashboard — nobody needs millisecond-fresh enrollment counts). This:

- Solves the actual stated goal (repetitive aggregation queries → queries only run once per TTL window, not once per page view).
- Requires **zero new database objects**, consistent with this assessment's constraint.
- Requires **zero shared-schema coordination** — the cache lives entirely inside `sistema_academico`, invisible to the other 6 repos.
- Is trivially reversible — delete the annotations, the cache is gone.

Option C (views + services) is a reasonable thing to revisit if the widget count grows substantially or query duplication becomes a real pain point; Option D is not recommended at this scale given the shared-schema blast radius it would add to write paths the rest of the project explicitly protects.

---

## 5. REST Feasibility Analysis

### 5.1 What "relatively weak" auth means for this specific decision

As detailed in §1.5: the module/role authorization cascade is solid, but there is (a) no CSRF protection anywhere in the app, and (b) a real, independent credential-storage weakness (Jasypt reversible encryption, hardcoded passphrase). Neither of these is caused by the dashboard work, but both are relevant to *how much new surface is prudent to add* on top of the current foundation.

- **CSRF:** every dashboard widget is a read-only `GET` — there is no state-changing request to forge, so CSRF is a non-issue for this feature regardless of which option below is chosen. This would become relevant only if a future widget accepted user input (e.g., a posted date-range filter), which isn't in the current proposal.
- **Authorization bypass risk:** the real risk of adding *any* new endpoint class is forgetting to gate it the way `ScreenAccessFilter`/`ResourceAuthorizer` already gate everything else — i.e., a new data endpoint that's reachable directly by URL without a grant check, undermining the otherwise-solid module cascade.

### 5.2 There's already a precedent for exactly this need

`/report`, `/foto`, `/foto-cl`, `/comprobante`, and `/foto-actividad` are **already** plain (non-JSF) authenticated servlets, each declaring an owning `Modules` token and gated by `ResourceAuthorizer` — proof that "a lightweight endpoint that isn't a JSF page" is an established, already-audited pattern in this codebase, not something novel.

### 5.3 Recommendation: no new REST API layer

Introducing Spring MVC, JAX-RS, or any general REST framework for this feature would be new infrastructure solving a problem that doesn't exist yet — the four existing KPIs, and every widget in §3.3, can be rendered as inline JSON on the same request-scoped page load the app already does (§2.2), which inherits the existing session/authorization protection automatically, with **zero new attack surface**.

**If** a specific widget later genuinely needs live refresh without a full page reload (the most plausible candidate is the cashier's "Payments Processed Today" ticking up through a shift), the right-sized answer is **one more plain servlet, modeled exactly on `/report`, gated by `ResourceAuthorizer` with a new `Modules` token** — not a REST API in the framework sense. This is deliberately the smallest possible increment, reuses infrastructure that's already been through security review, and doesn't touch the weak credential layer at all (it rides the existing session, same as everything else). Build it only if/when that concrete need shows up — not preemptively.

---

## 6. Performance Assessment

### 6.1 Current data scale (measured, not estimated)

Queried directly against the dev DB (`db_sis_academica`):

| Table | Row count |
|---|---|
| `ac_alumno` | 2,821 |
| `ac_matricula` | 10,605 |
| `ad_pago` | 58,608 |
| `ad_comprobante_pago` | 48,186 |
| `ac_acta` | 2,226 |
| `ac_facultad` | 5 |

This is a small dataset by any modern standard. **None of the four data-architecture options in §4 would have a performance problem at this volume** — the choice between them is entirely about coordination cost and maintainability, not raw speed. This is worth stating plainly because it removes "but views/triggers would be faster" as a real argument here.

### 6.2 Missing indexes

No explicit index DDL exists beyond MySQL's implicit PK/FK indexes. Recommended additions, useful regardless of which caching approach is chosen (cheap, standard, and they'll matter more as the tables grow):

- `ac_matricula(sem_id, mat_tipo, mat_activo)` — every enrollment-count/trend query filters on exactly this triple.
- `ad_pago(pag_fecha, pag_activo)` — daily revenue and "payments today" both filter on this.
- `ac_acta(act_status, act_tipo, act_activo)` — grade-record-status filtering.
- `ac_alumno(esp_id, alu_activo)` — students-by-faculty and academic-status-distribution both group through this.

### 6.3 MySQL 5.6 constraints worth keeping in mind

- **No materialized views** — reiterating §4.1's point, since it also bears on caching design: nothing in MySQL (5.6 or any later version) auto-refreshes a view's contents.
- **No CTEs, no window functions** — both were added in 8.0. Any aggregation needs to be expressible as plain `GROUP BY`/subqueries — every query proposed in §3.3 already is, so this isn't a blocker, just a constraint to remember if the widget set grows more sophisticated later.

### 6.4 Caching

Covered in §4.2 — this is the actual performance lever worth pulling now, ahead of any index/view/trigger work.

---

## 7. Security Impact

No new attack surface, by design of the recommendations above:

- **Chart library (§2):** Chart.js is a rendering library with no network calls of its own and no server-side component — it can't introduce a server-side vulnerability, and self-hosting it avoids a third-party CDN dependency at runtime.
- **Data delivery (§5):** inline JSON on the existing request-scoped page inherits the existing session/authorization checks automatically. No new endpoint, no new auth code, no new CSRF exposure (widgets are read-only).
- **Role gating (§3.2):** reuses `BeanUser.grantedModules`, already-audited infrastructure — no new authorization logic to get wrong.
- **Caching (§4.2):** in-process only, no new stored data beyond what Hibernate already reads; nothing to leak that a direct query wouldn't already expose to the same authorized user.

**What this work does *not* fix, and shouldn't try to:** the Jasypt hardcoded-passphrase/reversible-encryption issue (§1.5) is real, independent of the dashboard, and deserves a dedicated fix with its own migration plan for existing encrypted passwords — bundling it into a charting feature would blur two unrelated changes and make both harder to review. See §8.

---

## 8. Open Decisions Requiring Product Owner Input

1. **Widget-visibility persistence model** (per the brief's explicit instruction not to decide this here). Alternatives, not mutually exclusive with a phased approach:
   - **Reuse `tb_roles`/`grantedModules` directly** (§3.2) — zero new schema, fastest to ship, tied to existing screen-permission roles rather than a dashboard-specific concept.
   - **New `dashboard_widget` + role-mapping junction table** — cleanest long-term, extensible without a deploy, but a new shared-schema object (coordination cost per the project's cross-repo schema conventions).
   - **Hardcoded role→widget-set in application config** — no schema at all, fastest, but changing widget assignment needs a code deploy.
   - **Per-user customization** — most flexible, most complex; nothing in the brief's examples suggests this is actually wanted yet (role-based, not user-customized) — likely YAGNI for v1.
2. **Outstanding Debt source of truth** (§3.3): `ad_saldo.sal_monto` vs. `ad_alumno_tarifa`/`AlutarEstado` — these may not represent the same thing and need product/data verification before the widget is built.
3. **Grade Record Status v1 scope** (§3.3): ship suite-wide (no faculty breakdown) first, since the section→faculty join chain isn't confirmed, or spike that join chain before committing to the faculty-level version?
4. **Live refresh for cashier widgets:** is a full-page-refresh cadence acceptable for "Payments Processed Today," or is in-shift live ticking actually wanted (which would justify the one additional servlet endpoint described in §5.3)?
5. **The Jasypt/password-storage weakness (§1.5, §7):** not this task's scope, but worth an explicit decision on priority now that it's been surfaced formally, rather than leaving it implicit.

---

## 9. High-Level Implementation Roadmap

- **Phase 0 — Decisions.** Resolve the five items in §8. Nothing in Phase 1 is blocked on this except the exact widget-visibility mechanism.
- **Phase 1 — Foundation (no schema changes).** Vendor Chart.js; introduce `DashboardService` (§3.1); wire `<cache:annotation-driven/>` + `ConcurrentMapCacheManager` (§4.2); re-render the 4 existing KPIs through the new service as a walking skeleton before adding anything new. This phase alone satisfies "do NOT create database objects."
- **Phase 2 — Role-gated rendering.** Apply the §3.2 grant check once the Phase-0 persistence decision lands.
- **Phase 3 — Widget rollout, cheapest first.** Build in ascending complexity order from §3.3: Payments Today → Payments by Concept → Daily Revenue → Students by Faculty → Enrollment Trend (mind the rectification filter) → Academic Status Distribution → Outstanding Debt by Faculty (once §8.2 is resolved) → Grade Record Status last (the genuinely tricky one). This ordering front-loads value and defers the riskiest query.
- **Phase 4 — Conditional.** Only if the TTL cache stops being sufficient at future scale: revisit Option C (views + services) from §4.1. Add the four indexes from §6.2 regardless, ahead of Phase 3 rather than after — they're cheap and every widget benefits.

---

## Addendum (2026-07-06): Cross-Suite Data Considerations

Two findings from the parent UCH-suite analysis (entity-ownership matrix and the datetime debt record) that bear directly on the cashier widgets and were not covered above:

### A.1 Payments are dual-written — `sigu_caja` writes the same tables

The suite-wide entity ownership sweep (`uch/db/Entity_Ownership_Matrix.md`) confirmed `ad_pago`/`ad_comprobante_pago`/`ad_alumno_tarifa` are written by **both** `sistema_academico` and `sigu_caja`. Consequences for this assessment:

- **Mostly good news:** "Payments Processed Today", "Payments by Concept", and "Daily Revenue" aggregate the shared tables, so they *automatically* include payments captured in `sigu_caja` — the dashboard reflects university-wide cashier activity, not just this app's. This is presumably the desired semantics, but it should be stated to the Product Owner explicitly (§8), because the alternative reading ("payments processed *in this app* today") would require filtering neither app currently supports (no writer-app column exists).
- **Cache staleness note (§4.2):** with a TTL cache, payments registered in `sigu_caja` appear on this dashboard up to one TTL window late. Irrelevant at 5–15 min TTL for trend charts; worth remembering if the cashier "today" KPI ever gets a live-refresh servlet (§5.3).
- **Debt widget (§8.2) reinforcement:** the QA-era decision record says **caja is the source of truth for mora** — one more reason the outstanding-debt rule must be confirmed against `sigu_caja`'s logic, not derived only from this codebase's tables.

### A.2 The "today" boundary sits on top of a known timezone bug

The dev/prod JDBC URL carries `serverTimezone=UTC` while the business operates in America/Lima (UTC−5) — an already-documented debt item that produced a real off-by-one date bug in `MbActividad`'s date filtering. `DATE(pag_fecha) = CURDATE()` evaluates **in the MySQL session timezone**: depending on how the JDBC driver and server TZ interact, every payment recorded after ~19:00 Lima time can fall on "tomorrow", making the "Payments Processed Today" KPI and the daily-revenue buckets wrong precisely during evening cashier shifts.

**Recommendation:** treat the timezone normalization (or an explicit `CONVERT_TZ`/Lima-day definition in the widget queries) as a **prerequisite for the cashier date widgets** — added as open decision **§8.6**. The academic widgets are unaffected (no day-boundary semantics).

### A.3 Open decision list — one addition

6. **"Today" definition for cashier widgets:** resolve the `serverTimezone` debt first, or explicitly define "today" as the America/Lima calendar day inside the widget queries (`CONVERT_TZ` or app-side date computation). Choosing neither means evening payments silently land in the wrong day's numbers.

---

### Key files referenced

`net/uch/inicio/jsf/MbDashboard.java` · `net/uch/autenticacion/jsf/MbLogin.java` · `net/uch/autenticacion/jsf/beans/BeanUser.java` · `net/uch/configuracion/dao/impl/HSUsuarioDAOImpl.java` · `net/uch/common/security/{ScreenAccessFilter,ScreenAccessGuard,ResourceAuthorizer,Modules}.java` · `net/uch/academica/dao/impl/{HSAlumnoDAOImpl,HSMatriculaDAOImpl}.java` · `WEB-INF/applicationContext-services.xml` · `docs/authorization.md` · `docs/modules/academica.md` · `docs/Suite_Wide_Architecture_Review.md`
