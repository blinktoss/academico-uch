# Technical Review — System-Wide State Management Discovery

> Evidence-based, code-only investigation produced 2026-06-27 against `sistema_academico` on branch `feature/enrollment-validation-pipeline`. Scope: identify implicit state machines across modules and prepare ground for introducing explicit state management. Not an implementation — no code changed. Builds on the prior `Student_Enrollment_State_Analysis.md` (reused here, not re-investigated) plus new evidence on Grade Record, Semester, Free Courses, and Payment/Debt.

---

## 1. Lifecycle Candidates

| Module | Entity | State Field(s) | Known States | Catalog-backed? |
|---|---|---|---|---|
| Student | `AcAlumno` | `AluActivo`, `AluTipo` (009), `AluEstado` (011) | active/inactive; ESTUDIANTE/RETIRADO/ANULACIÓN/EN ESPERA; REGULAR/OBSERVADO/CONAFU | Yes (Tipo/Estado) |
| Enrollment | `AcMatricula` | `MatTipo` (022) | REGULAR, RESERVA, RECTIFICADA, PRE_MATRICULA, PRE_MATRICULA_RECTIFICADA, ANULADA | Yes |
| Grade Record | `AcActa` | `ActStatus` (plain string, not catalog) | DRAFT, PUBLISHED | **No** — hardcoded strings |
| Semester | `AcSemestre` | `semActual`, `semVigente`, `semActivo` | current/not-current; vigente/not; active/deleted | No |
| Free Courses | `ClMatricula`, `ClNota` | `matTipo` (022, reused), `matActivo`, `notActivo` | Pending payment → Active (022001) → Annulled; grade always active | Partially (matTipo only) |
| Payment/Debt | `AdAlumnoTarifa`, `AdProrroga` | `AlutarEstado`, `AlutarFechaProrroga`, `ProActivo` | "0" DEBE / "1" PAGO PARCIAL / "2" PAGO TOTAL (catalog group 013, see Addendum); Prórroga active/inactive | Yes (013), but via raw literals, no named constants |
| Attendance (Docente) | `AcSesionEfectivaAsisDoc` | `sesefeasisdocTipo` (catalog 046) | ASISTIO / TARDANZA / JUSTIFICACIÓN TARDANZA / TARDANZA JUSTIFICADA | Yes (046), raw literals only |

---

## 2. Hidden State Machine Findings

**Pattern confirmed across every module: states exist as fields, but transitions are admin-toggled or write-path-incidental — no module has a centralized transition function, validator, or guard table.**

- **Student/Enrollment** (prior assessment): no automatic transitions anywhere; 🔴 no guard against two simultaneous REGULAR enrollments for the same student+semester.
- **Grade Record (`AcActa`)**: the *best-governed* state in the system — `GradeRecordServiceImpl.rejectIfPublished()` is a real (if minimal) transition guard for Principal/Substitute Actas. But **Convalidación bypasses it entirely** (`MbConvalidacion:272` writes `PUBLISHED` directly, no guard, can overwrite an existing published record) — the one inconsistency in an otherwise well-behaved entity. Also: enrollment's prerequisite query (`HSMatriculaDAOImpl.seleccionarCursosAprobados`) never reads `ActStatus`, relying on the incidental fact that only published Actas carry final grades — works today, but is an implicit dependency, not an enforced one.
- **Semester**: weakest model found. `semActual` has a UI-level "only one current semester" guard (`MbSemestre.java:357-366`) but **no DB constraint** — concurrent admin edits or direct writes can produce multiple "current" semesters with no detection. No closing/locking concept exists at all; `semFechaInicio`/`semFechaFin` are pure metadata, never enforced.
- **Free Courses**: confirmed as the system's simplest lifecycle — 1-2 real transitions per entity (payment-driven promotion to REGULAR; manual annulment), no eligibility/schedule-conflict/prerequisite logic at all (none of academica's complexity was ported over). Grades have **zero immutability** — `insertarActualizarNotas()` allows unlimited re-entry, no published/locked state, no audit trail of changes.
- **Payment/Debt — confirms and resolves issue #33**: granting a Prórroga (`MbEdicionEstructPago:1080`) writes only to the standalone `AdProrroga.ProFecha`; the enrollment debt-gate (`MbMatricula.esDeudaBloqueante`, line 795) reads `AdAlumnoTarifa.AlutarFechaProrroga`, which is **never updated** by the grant. **The two are disconnected — granting a Prórroga today does not unblock enrollment.** This is the clearest, most concretely actionable bug surfaced in this whole review. Also found: the debt-blocking predicate is duplicated (a reusable method at `MbMatricula:792-796` plus an inline copy at `MbMatricula:1119-1123`) — same rule, two places, drift risk. Correction from initial pass: `AlutarEstado` is a genuine 3-state field (catalog group 013: "0" DEBE / "1" PAGO PARCIAL / "2" PAGO TOTAL — verified via `MbPago.java:1047-1055`'s switch statement), not the binary "0"/"2" first reported — the enrollment blocking-check (`!= "2"`) still treats PAGO PARCIAL as blocking, so the issue #33 finding is unaffected, but the state model itself is one notch more granular than originally documented.

**Cross-cutting risk:** every module's "state" is a raw `String` field ("0"/"1", "DRAFT"/"PUBLISHED", catalog codes) checked with scattered `.equals()` calls rather than read through one access point. No module has a transition table, an event log, or a single place that knows "what states exist and what can follow what."

**Catalog-group sweep (`tb_catalogo` audit, requested follow-up):** cross-referenced every catalog group against `Constants.java` to find state-shaped data with zero named constants — i.e. states handled purely as inline literals, one level worse than the modules above (which at least have constants). Findings:
- **Group 046 (Estado Asistencia Docente)** — **live, unguarded.** Written at `MbAsistenciaDocente.java:542` from a free UI dropdown selection (`:105-107`), read back at `:484`. No transition guard at all: nothing stops re-marking an already-`ASISTIO` session, approving a justification twice, or downgrading `TARDANZA JUSTIFICADA` back to unjustified. No constants anywhere — every check is the raw literal.
- **Group 033 (Estado Curso Matriculado: VIGENTE/RETIRADO)** — **dormant infrastructure, not a bug, a missing feature.** `AcMatriculaCurso.matcurEstado` column and Hibernate mapping exist; the only write in the whole codebase is `MbConvalidacion.java:258` setting it to `null`. Nothing reads it. This is very likely the intended fix for the already-known "no independent per-course lifecycle" gap (§2 Student/Enrollment) — the column to support per-course withdrawal (drop one course, stay enrolled in the rest) was added but never wired into `matricular()`/rectification flow.
- **Group 044 (Estado Sesión Asistencia)** — written once on session creation (`MbAsistenciaDocente.java:676`, hardcoded `"044001"`), never read or transitioned afterward. Effectively inert.
- **Group 045 (Estado Asistencia Alumno) and Group 049 (Publicación Tarea)** — **fully dead.** Zero reads, zero writes, anywhere. Group 049 is notable because no "Tarea" (task/assignment) entity exists at all in this codebase — the catalog group was seeded for a feature that was apparently never built.
- **Group 038 (Bloqueo Web: ABIERTO/CERRADO)** — **fully dead.** No global self-service web-access lock exists; `MbLogin` only checks per-user account lockout, nothing system-wide.

---

## 3. State Management Architecture Proposal

Keep it incremental, no big-bang rewrite, no new framework dependency.

1. **Introduce a lightweight, in-house `Lifecycle<S>` contract per entity** — not a library. A `Lifecycle` is just: an enum of valid states, a `Map<S, Set<S>>` of allowed transitions, and a `transition(current, target)` method that throws if the move isn't allowed (`GradeRecordServiceImpl.rejectIfPublished()` is already this pattern in miniature — generalize it, don't replace it).
2. **One state-owning service per entity**, following the existing `EnrollmentService`/`GradeRecordService` precedent: all writes to a state field go through that service, never through a bean or DAO directly. This alone would have caught the Convalidación gap and the duplicated debt predicate — both are "two write/read paths instead of one."
3. **No new DB round-trips.** The guard is in-memory (compare current vs. target against the static transition map) — same cost as today's scattered `.equals()` checks, just centralized.
4. **Audit/history**: don't build a generic transition-log table yet. Reuse the existing audit-fields pattern (`created/updated at/by`, already shipped for `AcMatricula`) entity-by-entity; a dedicated transition-history table is only worth it if a module proves it needs it (Grade Record, given its compliance sensitivity, is the most likely candidate later — not now).
5. **No external state-machine library.** The transition sets here are small (2-6 states, 1-3 valid transitions each); a library would add a learning-curve and dependency-risk cost this legacy stack doesn't need.
6. **Sequencing**: harden Grade Record's existing guard first (close the Convalidación gap) since it's the closest to "done" already — cheap, high-value, and a template for the others before touching anything net-new.

---

## 4. Pilot Recommendation

**Free Courses (`cursolibre`) confirmed as the best pilot** — validates the candidate proposed in the request.

- Smallest state space found (2-3 states per entity, 1-2 transitions).
- Zero cross-module coupling (no eligibility/prerequisite/debt logic ported from academica — it's an island).
- Has a real, demonstrable gap to fix as the pilot's first deliverable: no immutability guard on `ClNota`, no re-validation that the enrollment/section is still active before grade write — both are small, contained fixes that would prove the `Lifecycle<S>` pattern end-to-end without touching the higher-risk core modules.
- Low blast radius: a mistake here doesn't touch the core Student/Enrollment/Grade pipeline this session has spent most effort hardening.

**Second-priority candidates, not for the pilot but flagged as urgent regardless of pacing:**
- The Prórroga/`AlutarFechaProrroga` disconnect (issue #33) is a confirmed live bug, independent of any architecture work — worth fixing on its own track since it may currently be silently blocking legitimately-extended students.
- **Group 046 (Attendance Docente)** has zero transition guards on a payroll-adjacent state (attendance feeds teacher pay per the "TARDANZA" codes) — re-marking or justification-tampering has no audit trail today. Smaller in scope than Free Courses but touches compensation, so worth a guard sooner rather than later, separate from the pilot.
- **Group 033's dormant `matcurEstado` column is a cheap half-step**, not full pilot work: wiring it into a "withdraw from one course" feature would close the long-standing `AcMatriculaCurso` lifecycle gap without inventing a new column — worth scoping once the `Lifecycle<S>` pattern exists, as a second, very small pilot extension rather than a from-scratch build.

---

## Open Questions

1. Should the Convalidación publish-guard gap be fixed now (cheap, isolated) or bundled into the Grade Record pilot hardening from §3.6?
2. Does the missing DB-level constraint on `semActual` (multiple "current" semesters possible) warrant a fix before any state-management foundation work, given it's a single-row UPDATE today?
3. Is the duplicated debt-blocking predicate in `MbMatricula` (lines 792-796 vs. 1119-1123) worth a Quick Win fix ahead of Sprint 3, given the same issue class already burned this session once during the Sprint 2 code review?
4. Should issue #33's fix (linking Prórroga grant to `AlutarFechaProrroga`) be scheduled immediately given it's a confirmed live bug, independent of whether/when the broader state-management foundation is built?
