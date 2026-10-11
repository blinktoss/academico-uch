# UC-ENR-001 — Search eligible students for enrollment

## Objective
Find students who may be enrolled for the selected semester and show, per student, their current
enrollment status and which actions (enroll / rectify / reserve) are available.

## Actors
Enrollment clerk.

## Preconditions
- Clerk is logged in with access to the enrollment page.
- Target semester exists; payment structures for it have been published (otherwise no tariff data
  exists and financial gating is vacuous).

## Main flow
1. Clerk selects search criteria (specialty and/or student name/code).
2. System queries **eligible** students only: active, type *Estudiante*, state *Regular*
   (BR-ENR-01/02 — filter applied at the database level).
3. For each match, the system loads the student's enrollment snapshot for the semester in one
   batched query (current enrollment type, courses).
4. For each match, the system evaluates financial standing fresh (BR-ENR-04): any unpaid tariff
   line past its grace date marks the student a debtor.
5. Results grid shows per student the available actions: enroll, rectify, reserve — all disabled
   when the student is a debtor.

## Alternative flows
- **A1 — No matches:** empty result; no error.
- **A2 — Debtor found:** student appears in results but with all enrollment actions disabled;
  clerk can open debt detail (UC-ENR-007).

## Business rules
BR-ENR-01, BR-ENR-02, BR-ENR-03, BR-ENR-04, BR-ENR-05.

## Edge cases
- Students withdrawn by type change (`RETIRADO`, `ANULACIÓN`, `EN ESPERA`) whose `AluActivo` flag
  was never flipped: historically findable/enrollable (real bug, 30 affected rows); now excluded
  by the DB-level type/state filter.
- Non-Regular states have never occurred in real data (BR-ENR-03) — behavior against them is
  logically correct but empirically unexercised.

## Open questions
- Should debtors be hidden entirely instead of shown-but-disabled? (current AS-IS: shown.)

## Related GitHub issues
- Eligibility fix and N+1 batching delivered in PRs #25/#26.
