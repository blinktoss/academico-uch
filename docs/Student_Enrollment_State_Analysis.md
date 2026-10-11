# Technical Assessment — Student & Enrollment State Analysis

> Evidence-based, code-only investigation produced 2026-06-27 against `sistema_academico` on branch `feature/enrollment-validation-pipeline`. Scope: states, transitions, lifecycle rules, AS-IS behavior only — not a full system assessment. All claims are backed by file:line citations gathered via direct codebase search.

---

## 1. Student State Model

### A. Field usage summary

| Field | Written by | Read by (business rules) |
|---|---|---|
| `AluActivo` ("0"/"1") | `MbAlumno.insertar()` (always "1" on create/update); `HSAlumnoDAOImpl.eliminarAlumno()` ("0" on delete); `MbAlumnoCL` (cursolibre, always "1") | `HSAlumnoDAOImpl`: `esElegibleParaMatricula`, `seleccionarAlumnosElegiblesParaMatricula`, `seleccionarAlumnos`, `seleccionarUnAlumno`, `buscarAlumnoCodigo`, `getActiveStudentCount`; `HSSaldoDAOImpl`, `HSEdicionEstructPagoDAOImpl` (billing) |
| `AluTipo` (group 009) | `MbAlumno.insertar()` (admin-selected dropdown, create/update); `MbAlumnoCL` (cursolibre, copies or hardcodes "057002") | `HSAlumnoDAOImpl.seleccionarAlumnosElegiblesParaMatricula` / `esElegibleParaMatricula` (must = `009001` ESTUDIANTE); `getActiveStudentCount`; `seleccionarAlumnoEstPago` |
| `AluEstado` (group 011) | `MbAlumno.insertar()` (admin-selected dropdown, create/update) | `HSAlumnoDAOImpl.seleccionarAlumnosElegiblesParaMatricula` / `esElegibleParaMatricula` (must = `011001` REGULAR); `MbMatricula.matricular()`/`reservarMatricula()` (submission-time re-check) |

### B. Lifecycle findings

1. **`AluActivo` is a one-way soft-delete flag.** No code path found that sets it back `"0" → "1"`. Reactivating a deleted student would require direct DB manipulation.
2. **`AluTipo`/`AluEstado` only change via explicit admin edit** in `MbAlumno`'s create/update form. No automatic transition exists anywhere (no code demotes a student to OBSERVADO on academic failure, no code promotes them back, no code auto-sets RETIRADO/ANULACIÓN on withdrawal).
3. **Eligibility is enforced twice**, by design (this session's own work): at search time (DB-level filter) and again at submission time (`esElegibleParaMatricula`), specifically because these fields can change between the two — confirmed by test fixtures that flip them mid-test.
4. Confirms the AS-IS report's earlier finding: 30 RETIRADO/ANULACIÓN/EN ESPERA students remained `AluActivo="1"` in the dataset — consistent with finding (1): nothing in the withdrawal/cancellation process flips the soft-delete flag.

### C. State transition matrix (evidenced only)

```text
AluActivo:  1 -> 0   (HSAlumnoDAOImpl.eliminarAlumno)
AluActivo:  0 -> 1   NOT FOUND in code
AluTipo:    any -> any   (MbAlumno.insertar, admin dropdown only)
AluEstado:  any -> any   (MbAlumno.insertar, admin dropdown only)
```

No automatic/system-driven transition exists for any of the three fields — every change is a direct admin edit.

---

## 2. Enrollment State Model

### A. Enrollment types summary (catalog group 022)

| Code | Type | Created by | Becomes (when superseded) |
|---|---|---|---|
| `022001` REGULAR | Confirmed enrollment | `MbMatricula.matricular()` — always, unconditionally | `022004` RECTIFICADA |
| `022002` RESERVA | Reservation | `MbMatricula.reservarMatricula()` | — |
| `022004` RECTIFICADA | Superseded Regular | retag only (`actualizarTipoMatricula`) | terminal |
| `022005` PRE_MATRICULA | Pre-enrollment | **origin not found in this codebase** (see Open Questions) | `022006` |
| `022006` PRE_MATRICULA_RECTIFICADA | Superseded Pre-Matrícula | retag only | terminal |
| `022007` ANULADA | Cancelled (via reservation) | retag only | terminal |

### B. Lifecycle findings

1. **`matricular()` always writes `MatTipo=REGULAR`**, regardless of whether it's a fresh enrollment, a rectification, or a Pre-Matrícula confirmation. The distinction is only in what happens to the *old* row.
2. **Rectification and Pre-Matrícula confirmation are append-only, never an edit**: a brand-new `AcMatricula` row is always inserted; the prior row is retagged (RECTIFICADA / PRE_MATRICULA_RECTIFICADA), never deleted.
3. **🔴 No code-level or DB-level guard prevents two simultaneously-active REGULAR enrollments** for the same `(AluId, SemId)`. `registerEnrollment()` performs INSERT-then-UPDATE-old-row as two sequential, non-atomic steps. A race or a failed second step leaves both rows active. This is a more fundamental gap than the previously-known section-capacity race (issue #34) — this one is at the *whole-enrollment* level, not per-section.
4. **`AcMatriculaCurso` (per-course link) has no independent lifecycle** — it's created with its parent `AcMatricula` and never individually retagged on rectification; only the parent's type changes.
5. Confirms the AS-IS report's earlier finding about Prórroga/debt and the QW-02 schedule-conflict bug — no new contradiction found.

### C. Transition matrix (evidenced only)

```text
(none) -> REGULAR                         MbMatricula.matricular()
(none) -> RESERVA                         MbMatricula.reservarMatricula()
REGULAR -> RECTIFICADA                    registerEnrollment() retag, on rectification
PRE_MATRICULA -> PRE_MATRICULA_RECTIFICADA  registerEnrollment() retag, on confirmation
(any) -> ANULADA                          reserveEnrollment() retag, on reservation-cancels-prior
PRE_MATRICULA origin                      NOT FOUND - no code creates this type
```

---

## 3. Semester Dependency Rules

| # | Question | Answer | Evidence |
|---|---|---|---|
| 1 | Enrollment period (date range) validated? | **NO** | `AcSemestre.semFechaInicio`/`semFechaFin` exist but are never read/compared in `MbMatricula` |
| 2 | "Classes started" checked? | **NO** | No cutoff-date logic found anywhere in the enrollment path |
| 3 | Pending/unpublished prior-semester grades block enrollment? | **NO** — and a related gap: `seleccionarCursosAprobados()` (the prerequisite-history query) does **not filter by `ActStatus=PUBLISHED`**, so a DRAFT Acta's grade could count toward prerequisite eligibility | `HSMatriculaDAOImpl.seleccionarCursosAprobados` |
| 4 | Pending Acta Sustitutoria (substitute record) blocks enrollment? | **NO** | Substitute-record publishing logic is fully isolated in `MbNota`/`GradeRecordService`; no enrollment-side check |
| 5 | `AcSemestre.semActual` ("current semester" flag) enforced for enrollment target? | **PARTIAL** — the flag exists and is used to *display* the current semester (`MbMatricula.init()`), but `seleccionar()`/`matricular()` accept any explicit `semId` parameter; nothing stops enrolling into a non-current semester | `HSSemestreDAOImpl.seleccionarSemestreActual`, `MbMatricula.init` |

**Item 3 needs independent verification before any business decision rests on it** — it's a one-agent finding on a complex query (`HSMatriculaDAOImpl.seleccionarCursosAprobados`, lines 23–85); worth a direct read before treating it as settled.

---

## 4. Cross-Module Ownership

| Rule | Owner | Boundary |
|---|---|---|
| Student eligibility (type/status/active) | `net.uch.academica.dao.HSAlumnoDAOImpl` | ✅ Clean |
| Student status writes | `net.uch.academica.jsf.MbAlumno` | ✅ Clean — sole writer |
| Failed-attempt / retake limits | — | ❌ **Does not exist in code.** `seleccionarCursosAprobados` only *reads* pass/fail history; nothing caps retakes |
| Pending-grades restriction | — | ❌ **Does not exist in code** (consistent with §3.3 above) |
| Convalidación | `net.uch.academica.jsf.MbConvalidacion` | ✅ Clean / fully independent — reads eligibility, writes its own `AcMatricula` (fixed transfer-credit semester), never touches the main enrollment gate |
| Debt/financial restriction | Computation: `net.uch.administrativa` (`HSAlumnoTarifaDAO`). Enforcement predicate: `net.uch.academica.jsf.MbMatricula.esDeudaBloqueante()` | ⚠️ **Blurry at the module level** — `administrativa` exposes raw tariff rows only; the "is this blocking" business rule itself lives in `academica`, not in a shared service. (Note: within `academica` this was already de-duplicated to one private method this session — the blurriness is specifically the cross-module boundary, not intra-file duplication.) |

---

## Key Findings

1. **No automatic state transitions exist anywhere** for student type/status or enrollment type — every transition is either a direct admin edit or a retag performed by the enrollment write path itself. If any business rule assumes a system-driven transition (e.g., "students auto-move to OBSERVADO"), it is not implemented today.
2. **🔴 Two active REGULAR enrollments for the same student+semester are not prevented** at any layer (app or DB). This is a deeper version of the already-tracked capacity race (#34) and should get its own tracking.
3. **Pre-Matrícula's origin is unaccounted for** in this codebase — `MbMatricula` consumes and converts it, but nothing here creates it, implying an external process/admin tool not covered by this assessment.
4. **No enrollment-period, classes-started, or current-semester enforcement exists** — `seleccionar()`/`matricular()` will operate on any semester id passed in, regardless of its dates or `semActual` flag.
5. **Failed-attempt limits and pending-grades restrictions are both absent**, not just unconfirmed — confirms and sharpens the AS-IS report's section 7/8 findings.
6. **Debt-rule ownership is split across modules** (data in `administrativa`, business predicate in `academica`) with no shared service — a maintenance risk if the rule ever needs to change.

## Open Questions

1. Where does a `PRE_MATRICULA` (`022005`) row actually get created? (Admissions/scoring pipeline? A different bean not covered here? Manual DB seed?)
2. Is the missing `ActStatus=PUBLISHED` filter in `seleccionarCursosAprobados` intentional (DRAFT grades are a fair preview) or a real bug? Needs a direct read of that method before deciding.
3. Is the lack of a multi-REGULAR-enrollment guard an accepted risk (low concurrency in practice) or something Sprint 3 should close alongside #34?
4. Should `semActual`/date-range enforcement be in scope for a future sprint, or is "any semester, any time" intentional given how this admin tool is operated?
