# Free courses (Cursos Libres)

Optional complementary offerings (Office tools, databases, sports…) with a **parallel** model —
its own hierarchy, student registry, enrollment, grading, and payment structures, separate from
the core academic flow. AS-IS implementation: `net.uch.cursolibre` (`Cl*` entities, `cl_` tables),
views under `CursoLibre/`.

Engineering companion: [`docs/modules/cursolibre.md`](../../modules/cursolibre.md).

## Actors
- **Free-courses coordinator** — maintains hierarchy and offerings.
- **Clerk/cashier** — registers students, enrolls, verifies payments.
- **Public** — free-course students need not be university students (external, optionally
  university-affiliated).

## Use cases

| ID | Use case | Status |
|---|---|---|
| UC-FCL-001 | Manage areas | Identified |
| UC-FCL-002 | Manage modules & courses | Identified |
| UC-FCL-003 | Manage workshops (talleres) | Identified |
| UC-FCL-004 | Manage curricular plan | Identified |
| UC-FCL-005 | Open a workshop for a term (incl. evaluation system) | Identified |
| UC-FCL-006 | Manage workshop sections & schedules | Identified |
| UC-FCL-007 | Register free-course students | Discovery needed |
| UC-FCL-008 | Enroll in workshops (payment verification, custom amounts) | Discovery needed |
| UC-FCL-009 | Register grades | Identified |
| UC-FCL-010 | Manage payment structures | Discovery needed |

## Known business rules (confirmed)

- **BR-FCL-01** — Four-level hierarchy: Areas → Modules → Courses → Workshops/sections.
- **BR-FCL-02** — Completely parallel to the core model: separate entities/tables (`cl_`),
  separate Acta track, separate payment structures.
- **BR-FCL-03** — Students may be external; a university affiliation can be recorded
  (`relacionarUniv` / university registry).
- **BR-FCL-04** — Enrollment supports per-enrollment **custom amounts** (`editarMonto`) and
  **payment verification** (`verificarPagosMatricula`) — semantics pending discovery.
- **BR-FCL-05** — Logical deletion throughout, like the core model.

## Pending discovery
- Payment-verification rule in enrollment (gate like core debt-gating, or receipt check?).
- Grading policy: does the core GradePolicy (0–20, pass 11, truncation) apply to `MbNotasCL`?
  (Its Acta flow predates the core grading overhaul — verify before assuming.)
- Workshop capacity rules; schedule-conflict handling (if any) for `MbSeccionCL`.

## Placeholders
- User stories / edge cases / future enhancements — pending first analysis pass.
