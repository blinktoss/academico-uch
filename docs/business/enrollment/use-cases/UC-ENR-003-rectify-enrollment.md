# UC-ENR-003 — Rectify enrollment

## Objective
Correct an existing enrollment's course/section selection while preserving the original record as
history.

## Actors
Enrollment clerk.

## Preconditions
- Student has an active Regular (`022001`) or Pre-Matrícula (`022005`) enrollment for the
  semester.
- Student is not blocked by debt (BR-ENR-04).

## Main flow
1. Clerk selects the student and opens rectification.
2. Clerk adjusts the course/section selection.
3. System runs the **same validations as a fresh enrollment** — vacancy (BR-ENR-08) and schedule
   conflict (BR-ENR-09); no special treatment.
4. System **inserts a brand-new enrollment row** with the corrected selection.
5. System retags the superseded row's type to Rectificada (`022004` for Regular, `022006` for
   Pre-Matrícula) via load-then-update, so audit fields are stamped on the retag too.

## Alternative flows
- **A1 — Validation failure (vacancy/conflict):** same hard blocks as UC-ENR-002; the original
  enrollment remains untouched.

## Business rules
BR-ENR-12 (append-only correction — never edits or deletes the prior row), BR-ENR-08, BR-ENR-09.

## Edge cases
- `ac_matricula` accumulates one row per correction by design; history queries must filter by
  type to distinguish the active enrollment from superseded ones.
- The new-row insert and the old-row retag should be atomic; both go through the service layer.

## Open questions
- Is there a business limit on how many times an enrollment can be rectified, or a deadline
  (rectification period)? Not enforced in code; unknown whether it is manual policy.

## Related GitHub issues
- Covered by the service-migration PRs #25/#26; open edge cases #33–#35.
