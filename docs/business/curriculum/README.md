# Curriculum (Estructura académica)

What can be taught: the course catalog, curricular plans per specialty, plan↔course assignment,
prerequisites, thematic units (syllabus), and evaluation systems. AS-IS implementation:
`net.uch.academica` (`MbCurso`, `MbPlanCurricular`, `MbPlanCurricularCurso`, `MbPreRequisito`,
`MbUnidadTematica`, `MbSistemaEvaluacion`), views under `Academica/`.

## Actors
- **Academic authority / planner** — maintains structures; changes are infrequent and high-impact
  (every downstream capability consumes them).

## Use cases

| ID | Use case | Status |
|---|---|---|
| UC-CUR-001 | Manage course catalog | Identified |
| UC-CUR-002 | Manage curricular plans | Identified |
| UC-CUR-003 | Assign courses to a plan (cycle, credits, equivalences) | Identified |
| UC-CUR-004 | Manage prerequisites | Identified |
| UC-CUR-005 | Manage thematic units | Identified |
| UC-CUR-006 | Manage evaluation systems | Identified |

## Known business rules (confirmed)

- **BR-CUR-01** — Prerequisites form a real course-dependency graph (`AcPreRequisitos`) actually
  enforced at enrollment (BR-ENR-07) — not merely informative ordering.
- **BR-CUR-02** — Evaluation systems define grade columns **data-driven**: components grouped with
  two-level weights (component weight within group, group weight in final). Consumed verbatim by
  grading (BR-GRD-04/05).
- **BR-CUR-03** — Plans have an active flag (`PROP_PLAN_ACTIVO`) — logical deletion convention.
- **BR-CUR-04** — Course equivalences exist between plans (`openModalEquivalencias`) — used when
  plans migrate; exact resolution rules pending discovery.

## Pending discovery
- Plan versioning rules: when a new plan supersedes an old one, how are continuing students
  handled (relates to BR-STU-03)?
- Credits semantics (informational vs enforced — no credit limit exists at enrollment).
- Evaluation-system weight validation (must groups sum to 100%?).

## Placeholders
- User stories / edge cases / future enhancements — pending first analysis pass.
