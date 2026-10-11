# Gap analysis (Deliverable 5) — the functional analysis backlog

What we **don't** know yet, ranked so future planning sessions can pick work off the top. Status
after this sprint: 9 of 68 use cases fully documented, 18 flagged *discovery needed*.

## Coverage snapshot

| Module | Functional coverage | Confidence in what's written |
|---|---|---|
| Enrollment | High (4 UCs documented, rules verified) | High — evidence-backed |
| Grades & Actas | High (4 UCs documented, policy verified) | High — evidence-backed |
| Students | Medium (rules known, no UC docs) | Medium |
| Schedules & offering | Medium (conflict/vacancy rules verified via enrollment work) | Medium |
| Curriculum | Medium (prereq/eval-system rules verified via consumers) | Medium |
| Administration & security | Medium (RBAC/lockout delivered recently, fresh knowledge) | Medium-high |
| Free courses | Low (structure known, business rules not) | Low |
| Teachers / attendance | Low | Low |
| **Cashier / payments** | **Very low** (7 of 10 UCs discovery-needed) | **Low** |
| Admission (cross-repo) | Boundary only | Low |

## Missing functional documentation (by size of hole)

1. **Cashier/payments** — `MbPago` (~40 actions), prórrogas, personalization, bank files, and the
   production boundary with `sigu_caja`. No prior functional docs exist anywhere.
2. **Free courses** — full parallel model; only the tree/structure mechanics are documented.
3. **Teacher attendance** — session/marks model unexplored.
4. **Students** — lifecycle known at analysis level (state-analysis doc) but no UC docs.
5. **Schedules/curriculum** — supporting rules known through their consumers; their own
   maintenance flows (edit-after-use, versioning) undocumented.

## Missing business rules (specific, already-flagged unknowns)

| # | Unknown | Where it bites |
|---|---|---|
| G-01 | Prórroga grant → tariff grace-date linkage (may not exist) | Enrollment unblocking may be broken |
| G-02 | "Ampliaciones" semantics (UC-ENR-008) | Enrollment |
| G-03 | Reservation limits & interaction with later enrollment | Enrollment |
| G-04 | Credit/failure limits — absent in code; is that policy? | Enrollment (PO decision) |
| G-05 | Null/missing component grades at Acta publish time | Grades |
| G-06 | Substitute-exam eligibility (who may take it) | Grades |
| G-07 | `sp_convalidacion_cursos` equivalence rules | Grades/Admission |
| G-08 | Web grade-lock & grade-import semantics | Grades |
| G-09 | Payment collection flows, void semantics, series/numbering | Cashier (print/listing slice documented — [UC-CSH-007](cashier/use-cases/UC-CSH-007-print-voucher-and-listings.md); issue #4 root cause captured) |
| G-10 | Bank-file format & reconciliation rules | Cashier |
| G-11 | Free-course payment verification & grading policy applicability | Free courses |
| G-12 | Teacher availability → scheduling enforcement link | Teachers/Schedules |
| G-13 | Semester lifecycle (open/close) and its downstream effects | Administration |
| G-14 | Admission → student handoff mechanism (UC-ADM-003) | Cross-repo |
| G-15 | Student-code generation rule | Students |
| G-16 | Plan versioning / equivalence resolution for continuing students | Curriculum |
| G-17 | Rectification deadline/limit policy | Enrollment |

## Modules requiring additional discovery — recommended analysis order

Ranked by (business criticality × uncertainty × dependency for other work):

1. **Cashier/payments** — gates enrollment; biggest hole; also decides the `sigu_caja` boundary.
   Start with UC-CSH-005 (collection) and G-01 (prórroga gap — small, high-impact).
2. **Students lifecycle** — feeds the planned state-machine ADR; the analysis doc exists, needs
   conversion into UC docs + rule confirmation (G-15).
3. **Schedules & offering** — edit-after-use rules and teacher assignment (G-12) before any
   scheduling enhancement work.
4. **Curriculum** — plan versioning (G-16); prerequisite maintenance flows.
5. **Free courses** — full pass (G-11); decide whether it converges with core policies.
6. **Teachers & attendance** — G-12 plus attendance purpose/consequences.
7. **Administration** — semester lifecycle (G-13), catalog governance, role catalog.
8. **Cross-repo boundaries** — admission handoff (G-14), then intranet/reports read-contracts.

## Areas with the highest uncertainty (watch list)

- Anything inside `MbPago` (god bean, 40+ actions, mixes collection, printing, personalization).
- Stored-procedure-owned logic (`sp_convalidacion_cursos`) — rules live in the DB, invisible to
  code review.
- Legacy dual thresholds (10.5 vs 11) — any future unification is a **business** decision, not a
  refactor.
- Production behavior that diverges from this repo because `sigu_caja`/intranet own the live flow.
