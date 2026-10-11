# Students (Alumnos)

Student registry and lifecycle: personal data, origin (birthplace, previous school/academy),
faculty–specialty–plan assignment (entry vs current), type/state lifecycle, photo, and an
attendance calendar view. AS-IS implementation: `net.uch.academica` (`MbAlumno`,
`StudentPhotoServlet`), view `Academica/Alumnos.jsp`.

## Actors
- **Registrar / administrative staff** — maintains the registry.
- **Upstream**: admitted candidates arrive from the admission/scoring flow (cross-repo).

## Use cases

| ID | Use case | Status |
|---|---|---|
| UC-STU-001 | Search students | Identified |
| UC-STU-002 | Register new student | Identified |
| UC-STU-003 | Edit student (incl. specialty/plan changes) | Identified |
| UC-STU-004 | Deactivate student (logical delete) | Identified |
| UC-STU-005 | View attendance calendar | Discovery needed |
| UC-STU-006 | Serve student photo | Identified |

## Known business rules (confirmed)

- **BR-STU-01** — Student **type** (catalog group `009`: Estudiante `009001`, Retirado, Reserva,
  Anulación, En Espera…) and **state** (Regular / Observado / CONAFU) are separate dimensions;
  both gate enrollment eligibility (see enrollment BR-ENR-01).
- **BR-STU-02** — Logical deletion (`AluActivo`), never physical. Historically type changes did
  **not** always flip the active flag (source of the eligibility bug — data still carries such rows).
- **BR-STU-03** — A student keeps both an **entry** specialty/plan and a **current** one — plan
  migration is an expected lifecycle event (`cambiarEspecialidad*`/`cambiarPlan*`).
- **BR-STU-04** — Student code assignment: last-code data patched by the one-time DB setup script;
  generation rule not yet documented (discovery).

The type/state lifecycle has a dedicated analysis:
[`docs/Student_Enrollment_State_Analysis.md`](../../Student_Enrollment_State_Analysis.md) — the
implicit state machine is a known gap with an ADR plan (`Lifecycle<S>`).

## Placeholders
- `user-stories.md`, `edge-cases.md`, `future-enhancements.md` — see files.
