# Academica module

## Purpose & scope

Core academic domain — the student lifecycle: students, curricular plans, course openings
(*cursos aperturados*), sections, enrollment (*matrícula*), evaluation systems, grade entry, and
official grade records (*Actas*). This is the source of truth other UCH apps read from.

Package `net.uch.academica` (`jsf` managed beans, `jsf.beans` view models, `dao` + `dao.impl`,
`service` + `service.impl`). Persistent entities live in `net.uch.mapping` (`Ac*` prefix).

This guide currently details the **Grades & Actas** sub-area (verified through hands-on work,
2026-06). Other sub-areas are noted but not yet fully documented.

| Sub-area | Managed bean | Guide status |
|---|---|---|
| Grades & Actas | `MbNota` | Documented below |
| Convalidación (transfer credit) | `MbConvalidacion` | Documented below |
| Enrollment (matrícula) | `MbMatricula` | Documented below |
| Students | `MbAlumno` | Pending |
| Schedules / sections / plans | `MbHorario`, others | Pending |

---

## Grades & Actas

### Concepts

- **Nota (`AcNota`)** — an individual grade: one student, one section, one personalized evaluation
  component (`AcSisEvaPersonalizado`). Freely created/edited throughout the semester.
- **Acta (grade record)** — the official, consolidated record for a section. Holds sensitive data;
  becomes **immutable once published**.
- **Acta types** (`tb_catalogo` group `036`): Principal `036001`, Sustitutoria `036002`,
  Convalidación `036003`.
- **Lifecycle** — `DRAFT` → `PUBLISHED`, backed by `ac_acta.act_status`. DRAFT is a *derived*
  view: recomputed from `AcNota` each time the modal opens, **never persisted**. Only PUBLISHED
  rows exist in `ac_acta`. Published records compute their results once, at publish time, and store
  them — no runtime recomputation afterward.
- **Frozen snapshot rendering (immutability in the UI).** Once an Acta Principal is PUBLISHED,
  reopening it renders the **stored** component grades + final, not a fresh recompute from live
  `AcNota` (`asignarNotasActas` branches on `act_status`: published → read back the stored
  `PROMEDIO_PARCIAL` detalles; draft → recompute from `AcNota`). This is what makes immutability
  real: editing a student's `AcNota` after publication can no longer silently change what the
  official published record shows or what the substitute recalculation starts from.

### Business rules

- **Publishing is deliberate and final.** The grade-entry modal's old "Guardar" is now "Publicar",
  gated by a `rich:modalPanel` confirm dialog (`mpConfirmarActaPrincipal` /
  `mpConfirmarActaSustitutoria`). It is **not** a JS `confirm()` — `return confirm()` does not
  reliably cancel an `a4j:commandButton`'s AJAX submit in RichFaces 3.3 (the actionListener fires
  anyway), so the modal idiom is used instead. Once a Principal Acta is published it cannot be
  modified — corrections only happen via the Sustitutoria.
- **Auto-naming** (`GradeRecordNomenclature`): código = `{SemCodigo}-{SecCodigo}-{P|S}` (e.g.
  `2026-1-EN01MA-P`); número is assigned from the generated `act_id` after the first save; these
  fields are read-only in the UI (no free text).
- **Sustitutorio replacement rule** (common Peruvian practice, confirmed by the maintainer): the
  substitute score **unconditionally replaces the student's single lowest original grade** —
  regardless of that component's weight, and even if the new score is itself lower. Which grade is
  lowest is `SubstituteGradeCalculator.lowestGradeIndex` (pure, no Spring/DB deps, fully unit-tested
  incl. n=0/1/2, all-equal, all-highest, all-lowest, tie-break). The course grade is then
  **recomputed with the full evaluation weights** (`WeightedGradeCalculator`) and truncated — the
  *same* calculation as the Principal (see **Grading policy** below), not a flat average of the
  adjusted grades. (The legacy code averaged them flat and `Math.round`ed, which both ignored the
  weights and rounded instead of truncating.)
- **Convalidación is a separate track** for transfer students (e.g. credits from another
  university). Only courses the stored procedure `sp_convalidacion_cursos` deems equivalent are
  eligible; ineligible ones render with a disabled grade box. Its Acta uses tipo `036003` so it
  **never shares a row** with the regular teacher-driven Principal Acta, and is created already
  `PUBLISHED`.
- **Evaluation system is data-driven, never hardcoded.** A section's columns (components and their
  groups/weights) come from its evaluation system in the DB (`ac_sis_eva_personalizado` grouped by
  `ac_sis_eva_detalle`). The Substitute modal builds its columns dynamically from that data
  (`SubstituteActRow` = one row per student, holding a `List<SubstituteComponent>`), so adding a new
  evaluation system needs **zero code change**. The old `switch` on fixed component codes
  (`EP`/`EF`/`PA-1`/`PA-2`) is gone — it both hardcoded one evaluation system and mismatched the
  stored `siseva_detalle_id` (a *personalizado* id, not a group id), so its cells never matched.

### Grading policy

The official grading convention for the academic system (decided 2026-06; encoded in `GradePolicy`
+ `WeightedGradeCalculator`, both pure and unit-tested):

- **Scale:** 0–20.
- **Allowed increments:** half-points only. A grade is valid iff it's in range and `grade × 2` is a
  whole number (so `13`, `13.5` ok; `13.2` rejected). Enforced **both ends**: JS on the input
  (`gradeKeyFilter`/`gradeValidate` in `Notas.jsp`) for UX, and `GradePolicy.isValid` on the server
  in `insertar` / `insertarSustitutorios` as the real guard (defense-in-depth — never trust the
  client).
- **Passing grade: 11.** `>= 11` → PASS, `< 11` → FAIL (`GradePolicy.PASSING_GRADE`). *(A draft of
  the policy doc said 13; that was a typo the maintainer corrected to 11.)* The red-text color
  convention on both the registration and substitute screens uses this same `< 11` threshold.
- **Final grade is truncated, never rounded.** Intermediate weighted math keeps full `double`
  precision; the reduction to the official integer grade happens **once**, at the end, by truncation
  (`GradePolicy.truncate`, e.g. `12.9 → 12`). This is deliberate — rounding would inflate a 12.5 to
  13 and hand out a pass that wasn't earned.
- **Weighting is two-level** (`WeightedGradeCalculator`): components are averaged within their group
  by component weight (`siseva_per_peso`), then group averages are combined by group weight
  (`siseva_detalle_peso`). The same calculator produces both the Principal final and the Substitute
  final, so the two can never drift apart.
- **Published storage keeps an audit trail** (migration `V7`, additive/nullable): each published
  `PROMEDIO_FINAL` detalle stores the official truncated grade in `actdet_nota`, the **raw decimal**
  in `actdet_raw_grade DECIMAL(5,2)`, and **PASS/FAIL** in `actdet_status`. `DECIMAL(5,2)` means the
  stored raw is 2-decimal (e.g. `12.86` for `12.857…`); widen later if more precision is ever needed.

### Core flow

1. Teacher registers grades for a section → `AcNota` rows (`MbNota.insertar`).
2. "Acta Princ." opens a preview recomputed from those grades (`MbNota.asignarNotasActas`).
3. "Publicar" → `MbNota.insertarActa` builds the `AcActa` + details and publishes it.
4. End-of-semester second chance: "Acta Sust." (`MbNota.llenarActaSustitutoria` →
   `insertarSustitutorios`) requires the Principal to be PUBLISHED first, then applies the
   substitute rule and publishes the Sustitutoria.

### Design notes

- **`GradeRecordService` (`@Transactional`) owns the publish lifecycle rule.** `publishPrincipal` /
  `publishSubstitute` run the "already published?" guard, the write, **and** the
  `GradeRecordNomenclature` número assignment (which needs the generated `act_id`) all in a single
  transaction — closing a TOCTOU gap (inline in the bean, the guard read and the persist were two
  separate transactions a concurrent publish could interleave). `MbNota` just builds the `AcActa`
  and calls the service; the bean no longer touches `daoActa` for publishing. It throws
  `GradeRecordAlreadyPublishedException` (extends `net.uch.common.service.ServiceException`);
  `MbNota` catches it and maps to the same `oncomplete` alert — business rule in the service,
  rendering concern in the bean.
- **This is the template for new services on this stack.** Declare each service *explicitly* in
  `applicationContext-services.xml` with `<constructor-arg ref="springHibernateDao…"/>` — do **not**
  use `<context:component-scan>`: Spring 3.2's bundled ASM can't read Java 14 bytecode (the same
  reason the whole app avoids annotation scanning). `<tx:annotation-driven>` only post-processes
  already-defined beans (no classpath scan), so `@Transactional` works fine on XML-declared beans.
  Only wrap DAOs that use plain `HibernateTemplate` (like `HSActaDAOImpl`); DAOs with manual
  `session.beginTransaction()` throw under an outer transaction until those manual blocks are removed.
- `AcActa` carries created/updated at/by audit fields via the shared `AuditInterceptor` (same
  pattern as `AcSemestre`).

### Test-data hygiene

Acta tests write to the live dev DB. `MbNotaTest` and `MbConvalidacionTest` clean up after
themselves via `HSTestSupportDao.cleanAcActa(...)` / `cleanAcActaConvalidacionByAlumno(...)`. If a
test run fails mid-write, check for orphaned `ac_acta`/`ac_acta_detalle` rows dated today (and
`ac_nota` rows with `not_nota IS NULL`) and purge them, scoped by date so backup data is untouched.

---

## Enrollment (Matrícula)

> Verified through hands-on work + a dedicated reverse-engineering pass, 2026-06. Full evidence
> trail: [`docs/Enrollment_Module_AS-IS_Report.md`](../Enrollment_Module_AS-IS_Report.md) and
> [`docs/Schedule_Conflict_Validation_Evaluation.md`](../Schedule_Conflict_Validation_Evaluation.md).
> Findings below reflect the **current** code state (after the service-layer migration and the
> Quick Wins fixes); the linked reports also record the *prior*, now-fixed state for context.

### Concepts

- **Three enrollment types share one table** (`AcMatricula`, `mat_tipo` from `tb_catalogo` group
  `022`): Regular (`022001`), Reserva (`022002`), Pre-Matrícula (`022005`). Rectifying either a
  Regular or a Pre-Matrícula produces `022004`/`022006` on the *superseded* row (see Rectificación
  below) — rectification is append-only, not an edit.
- **`EnrollmentService` (`@Transactional`)** owns enrollment writes (`registerEnrollment`,
  `reserveEnrollment`) and the batched read path (`findEnrollmentSnapshots`). `MbMatricula` no
  longer opens/closes its own Hibernate `Session` — that hand-rolled
  `HSMatriculaDAO.openSession()`/`closeSession()` workaround (3 call sites, no `try`/`finally`, a
  connection leak on exception) is gone.
- **Debt is computed, not stored.** There's no "is debtor" column — `AdAlumnoTarifa.AlutarEstado`
  + `AlutarFechaProrroga` are compared against "now" fresh on every `seleccionar()` call (see
  Business rules).
- **Section capacity is computed, not decremented.** `AcSeccion.SecVacantes` is a static ceiling;
  occupancy is a live `COUNT` of active `AcMatricula`/`AcMatriculaCurso` rows, checked at submission
  time inside `matricular()` — not reserved ahead of time.

### Business rules

- **Eligibility is now enforced at the DB layer, not just `AluActivo`.** The original AS-IS
  investigation found a real bug: enrollment search filtered only on `AluActivo`, so 30 students
  typed `RETIRADO`/`ANULACIÓN`/`EN ESPERA` (group `009`, `AluTipo`) — who'd never had their active
  flag flipped on withdrawal — were findable and enrollable. Fixed by adding
  `HSAlumnoDAO.seleccionarAlumnosElegiblesParaMatricula(...)`, which adds
  `Restrictions.eq(AluTipo, CAT_TIPO_ALUMNO_ESTUDIANTE)` and
  `Restrictions.eq(AluEstado, CAT_ESTADO_ALUMNO_REGULAR)` directly to the `Criteria` used by
  `MbMatricula.seleccionar()`. **Pushed into the DB deliberately, not filtered in Java after the
  fetch** — filtering post-query would transfer and discard rows for the same reason the N+1 fix
  below avoids round-tripping. The original `seleccionarAlumnos()` (used by `MbAlumno`,
  `MbConvalidacion`, `MbAlumnoCL`) is untouched — both methods share a private
  `buildAlumnoSearchCriteria(...)` helper so the eligibility restriction never leaks into those
  other 3 callers' behavior.
- **`AluEstado` `OBSERVADO`/`CONAFU` exist in the catalog but have never been assigned** in the
  dev-DB dataset — every row is `REGULAR`. The DB-level filter above will exclude them once they
  ever appear, but this is unexercised against real non-Regular data; treat as logically correct,
  not empirically proven.
- **Debt gates enrollment, not academic progression directly.** On every `seleccionar()` call, each
  matched student's active `AdAlumnoTarifa` rows are evaluated: if `AlutarEstado != "2"` (not paid)
  **and** `now > AlutarFechaProrroga` (grace period passed), the row is flagged a debtor and
  `verMatricular`/`verRectificar`/`verReservar` are all disabled for that student. There is no
  partial-payment/balance field — debt is binary per tariff line, not amount-reconciled.
- **Prórroga (`AdProrroga`) is the extension history; `AdAlumnoTarifa.AlutarFechaProrroga` is the
  denormalized effective deadline every reader consumes** (enrollment debt gate, `sigu_caja` mora,
  `sigu_intranet` debt). Since #33 (building on #45), registering a Prórroga is atomic through
  `ProrrogaService` (append history + sync the deadline in one tx); the **latest-registered**
  extension wins — highest `pro_numero`, **not** `max(date)` — and every writer keeps history: the
  grant modal registers one at a time (append-only, no add/undo), the single-edit modal no longer
  touches the deadline, and the massive block editor also appends `ad_prorroga` rows ("From massive
  upload on …"). Free Courses (`cl_alumno_tarifa`) keeps the denormalized deadline with **no history
  table**, by design.
- **Enrollment and the payment-structure module are fully decoupled.** `AdAlumnoTarifa` rows are
  generated in bulk per payment-structure publish (`MbEstructuraPago`), one row per
  `(student × structure-detail-line)`, independent of which courses a student actually enrolls in.
  `MbMatricula`'s enrollment methods never write `AdAlumnoTarifa`.
- **Schedule-conflict detection is slot-identity matching, not interval-overlap math** — the
  institution's timetable is quantized into a fixed catalog of 45-minute period-slots per shift
  (`AcTurnoDetalle.TurdetHora`, a single `TIME` column with no duration/end field); a multi-period
  class occupies one `ac_horario` row per slot it spans. Two sections conflict iff they share at
  least one `(HorDia, TurdetHora)` pair — structurally valid given the quantized grid, not a
  simplification that misses overlaps.
  - **Was a confirmed bug, now fixed (QW-02):** `HSHorarioDAOImpl.duplicidadHoraria()`'s native SQL
    hardcoded `ac_curso_aperturado.sem_id = 9` — which happened to equal `Stubs.SEMESTRE_2010_2_ID`,
    the one semester every test enrolled against, masking the bug. For any other semester the clause
    silently filtered out all rows, so the check returned "no conflict" unconditionally — two
    genuinely overlapping sections could be enrolled together with no block, in every semester
    except one fossilized historical one. Fixed by parameterizing to `:sem_id`, threaded through
    `duplicidadHoraria(List<Integer> seccionIds, int semestreId)` from `MbMatricula.matricular()`
    (which already had `rowSemestreId` in scope). Covered by a regression test using a
    non-`SEMESTRE_2010_2_ID` semester, since the prior tests structurally could not have caught this
    class of bug.
  - The check is a **hard block**, not a dismissible warning: any conflict rows skip
    `enrollmentService.registerEnrollment(...)` entirely and show a RichFaces modal
    (`dupHorario`) listing the conflicting sections. No "proceed anyway" path exists.
  - Runs **after** the section-vacancy check and **before** the enrollment write, evaluated once per
    submission against the full set of selected section ids — not incrementally as sections are
    picked. Shared by both fresh enrollment and rectification (`rectificarMatricula()` ultimately
    calls `matricular()`).
- **Section capacity has a known, unfixed race condition.** The vacancy check
  (`secVacantes - liveCount <= 0` → reject) and the subsequent insert are not wrapped in a single
  atomic check-then-insert transaction at the section level — two concurrent enrollment submissions
  could both pass the check for the last seat. Documented but not fixed (out of Quick Wins scope;
  would need a DB-level constraint or pessimistic lock to close properly).
- **No credit-limit and no repeated-failure-limit enforcement exist**, confirmed absent (not just
  unconfirmed) by reading the full enrollment path — a student can enroll in any combination of
  courses regardless of total credits, and a failed course can be retaken indefinitely.
- **Prerequisites are genuinely enforced**, not just curricular ordering: `AcPreRequisitos` models a
  real course-dependency graph; `cursosAbiertos()` merges (a) courses whose full prerequisite chain
  is in the student's approved list (`>= 10.5`, hardcoded passing threshold for *this* check — note
  this predates and is independent of the `GradePolicy.PASSING_GRADE = 11` used for Actas) and (b)
  prerequisite-free courses in-specialty, both excluding courses already attempted, filtered by term
  aperture.

### Core flows

1. **Fresh enrollment** — `MbMatricula.seleccionar()` finds eligible students (DB-filtered by
   `AluTipo`/`AluEstado`, debt-checked) → `enrollmentService.findEnrollmentSnapshots(...)` loads
   each one's current status in a single batched query (see Design notes) → `matricular()` validates
   section vacancy, then schedule conflict, then calls
   `enrollmentService.registerEnrollment(matricula, rowTipoMatriculaId)`.
2. **Reserva** — same eligibility/vacancy path, written via `enrollmentService.reserveEnrollment(...)`
   instead of a Regular registration.
3. **Rectificación** — **never edits the existing `AcMatricula` row.** Always inserts a brand-new
   row via `registerEnrollment(...)` reflecting the corrected course selection, then retags the
   *old* row's type to `MATRICULA_RECTIFICADA`/`PRE_MATRICULA_RECTIFICADA` via
   `actualizarTipoMatricula` (load-then-update, so the audit interceptor fires on the retag too).
   Goes through the same vacancy and schedule-conflict checks as a fresh enrollment — no special
   treatment. This is an append-only correction model: every rectification preserves the prior row
   as a superseded historical artifact rather than deleting or overwriting it, at the cost of
   `ac_matricula` accumulating one row per correction.

### Design notes

- **N+1 fix via batching, not per-student queries.** `seleccionar()` used to load each matched
  student's enrollment snapshot one query at a time (18 queries for 18 students, observed via
  TRACE-level logging). `EnrollmentService.findEnrollmentSnapshots(List<Integer> studentIds, int
  semesterId)` batches the lookup with `Restrictions.in(...)` (3 queries regardless of result size).
  The single-id overload (`seleccionarMatricularRegular(int, int)`) keeps an explicit
  `FetchMode.JOIN` chain (`AcMatriculaCursos→Sec→Curape→Plancur→Cur`) to avoid
  `LazyInitializationException` after the session closes; the batched List-overload intentionally
  omits those joins to avoid fetching data most callers in the loop don't need.
- **Audit fields follow the same Strategy A pattern as the rest of the system** (`Auditable` +
  `AuditInterceptor`, see audit-fields project memory) — `AcMatricula` got `createdAt/createdBy/
  updatedAt/updatedBy` added via an additive migration. Bulk HQL updates
  (`eliminarMatricula`/`actualizarTipoMatricula`) were converted to load-then-update specifically so
  the interceptor — which only fires on entity load/flush, not bulk HQL — actually stamps them.
- **DB-level filtering over in-memory filtering, as a deliberate tradeoff call.** When eligibility
  filtering was first added it was implemented as a post-fetch Java filter; this was corrected to
  push the restriction into the `Criteria` itself (see Business rules above) because filtering after
  the query wastes the same wire-transfer-and-discard cost the N+1 fix had just eliminated. Default
  to DB-level filtering for new eligibility rules in this module.
- **Quick Wins were kept on an independent branch/PR from the service-layer migration**, both based
  on `main` rather than stacked on each other, specifically so either could merge without waiting on
  the other. They later merged into each other cleanly (one trivial import-only conflict in
  `MbMatriculaTest.java`).

### Test-data hygiene

`MbMatriculaTest` includes a fixture that temporarily flips a real student's `AluEstado` to
`OBSERVADO` (via `HSTestSupportDao.updateAluEstado`) to exercise the eligibility filter, restoring it
in a `finally` block — if a test run is killed mid-test, check for a real student stuck in a
non-Regular state and restore it manually.

---

## Known technical debt

See `README.md` → **Technical Debt Backlog** (don't duplicate the detail here):

- DAO transaction-attribute name mismatch (Spanish verbs vs the English `get*/find*/…` wildcard
  template) — being retired feature-by-feature via the service layer, not a blanket fix.
- God Bean: `MbNota` (~1,250 LOC, down from ~1,380) — being shrunk by extracting services
  (`GradeRecordService` is the first) and pure helpers (`SubstituteGradeCalculator`,
  `GradeRecordNomenclature`, `GradePolicy`, `WeightedGradeCalculator`) plus view models
  (`SubstituteActRow`/`SubstituteComponent`, `PublishedActaRow`).

## Tree Builder N+1 Optimization Pattern

All tree builders in the system (5 total: `MbHorario`, `MbAperturaCursos`, `MbNota` + cursolibre
`BaseMbViewTreeCL` / `MbTallerAperturado`) have been optimized to eliminate N+1 queries.

### Pattern Overview

The **Problem:** Naive tree construction queries each parent once: loop areas → query modules per
area → loop modules → query courses per module, etc. With 10 areas + 100 modules, this becomes
1 + 10 + 100 = 111 queries.

The **Solution:** Batch queries + in-memory grouping:
1. Query all modules for all areas in **one HQL projection** (ordered by parent ID)
2. Group in-memory by `parentId` using `HashMap<Integer, List<Row>>`
3. Loop parents once, look up their children from the map (zero additional queries)
4. Result: 1 query per hierarchy level, regardless of parent count

### Implementation Steps

1. **Create a flat projection DTO** (e.g., `OpenedCourseRow`):
   ```java
   public class OpenedCourseRow {
     public final int especialidadId;
     public final int semestreId;
     public final int curapeId;
     // ... other fields needed for the tree node
   }
   ```

2. **Add batch query method to DAO interface**:
   ```java
   List<OpenedCourseRow> findOpenedCoursesBySemesters(List<Integer> semesterIds);
   ```

3. **Implement in DAO with HQL projection**:
   ```java
   public List<OpenedCourseRow> findOpenedCoursesBySemesters(List<Integer> semesterIds) {
     var hql = "select new net.uch.academica.dao.OpenedCourseRow(...) " +
              "from AcCursoAperturado curape " +
              "inner join curape.Plancur plancur " +
              "inner join plancur.Cur cur " +
              "where curape.Sem.Id in (:semesterIds) " +
              "order by cur.Esp.Id, curape.Sem.Id, ...";
     return find(OpenedCourseRow.class, hql, "semesterIds", semesterIds);
   }
   ```

4. **In managed bean, replace loops with grouped lookup**:
   ```java
   // OLD: Loop specialties, query modules per specialty (N+1)
   List<AcCursoAperturado> cursos = daoPlanCurricular.seleccionarAperturaporEspecialidad(esp.id, sem.id);
   
   // NEW: Batch query all, group by specialty
   Map<Integer, List<OpenedCourseRow>> planesPorEspecialidad = new HashMap<>();
   for (OpenedCourseRow row : daoPlan.findOpenedCoursesBySemesters(semesterIds)) {
     planesPorEspecialidad.computeIfAbsent(row.especialidadId, k -> new ArrayList<>()).add(row);
   }
   // Then: List<OpenedCourseRow> planes = planesPorEspecialidad.get(espId);
   ```

### Benefits

- **Performance:** Test suite speedup of 21% (177s → 139s). Production estimate: 21% reduction in DB
  query load with ~50 concurrent users.
- **Predictability:** ORDER BY in the batch query preserves original per-parent query order, so tree
  structure IDs and node strings are byte-identical — tests pass without changes.
- **Scalability:** Grows O(1) with parent count instead of O(N).

### Where Applied

| Managed Bean | DAO Method | Status |
|---|---|---|
| `MbHorario` | `HSPlanCurricularDAO.findPlanesByEspecialidades` | ✓ Optimized |
| `MbAperturaCursos` | `HSPlanCurricularDAO.findPlanesByEspecialidades` | ✓ Optimized |
| `MbNota` | `HSAperturaCursosDAO.findOpenedCoursesBySemesters` | ✓ Optimized |
| `BaseMbViewTreeCL` | `HSModuloDAO.findModulosByAreas`, `HSCursoCLDAO.findCursosByModulos` | ✓ Optimized |
| `MbTallerAperturado` | `HSModuloDAO.findModulosByAreas`, `HSCursoCLDAO.findCursosByModulos` | ✓ Optimized |

## Pending / in-progress

- **Acta UI browser click-test** (read-only código/nombre/número + "Publicar" confirm dialog) —
  deferred; needs the maintainer's desktop. All server-side logic is verified by integration tests.
- **Enrollment follow-ups flagged by the AS-IS investigation, not yet acted on:**
  - **Prórroga linkage gap** — no confirmed code path pushes a granted Prórroga's date into
    `AdAlumnoTarifa.AlutarFechaProrroga`; if that update genuinely doesn't exist, granting a
    Prórroga via the UI may not unblock enrollment at all. Needs a direct read of
    `MbEdicionEstructPago`'s save action or a manual test.
  - **Section-capacity race condition** — vacancy check-then-insert in `matricular()` isn't atomic;
    two concurrent submissions could both pass the check for the last seat.
  - **No credit-limit enforcement** — confirmed absent, not just unconfirmed; flag for César's
    judgment on whether this is institutional policy that should be enforced.
- **Grade-write service extraction** — the next natural slice after `GradeRecordService`, on the
  `chore/service-layer-foundation` branch. `MbNota.insertar` saves a whole class's grades via
  `HSNotaDAOImpl.insertarOrActualizarNotas`, which is atomic *today* only because the DAO opens a
  **manual** `session.beginTransaction()`/`tx.commit()` block inside a `HibernateTemplate.execute`
  callback — that manual block is precisely why a blanket Spring `@Transactional` fix breaks it (see
  README backlog). The proper move: a `@Transactional` grade service that owns the batch, and
  *delete* the manual tx block so the ambient transaction handles commit/rollback. `MbNota`'s
  `springHibernateDaoNota` proxy also has the dead Spanish-vs-`get*/find*` attribute mismatch (none
  of its methods match any pattern → no Spring advice at all), so it can collapse to a plain bean
  once the write moves to the service.

## Related modules

- **Administrativa (payments)** — enrollment is financially gated (outstanding debt can block
  re-enrollment).
- **Cursos Libres (`cursolibre`)** — a parallel `Cl*` curricular/enrollment/grading model, separate
  from this core flow.
- Cross-cutting: audit-fields strategy and the service-layer foundation work both originate here.
