# UC-ENR-004 — Reserve enrollment

## Objective
Register an enrollment **reservation** (`022002`) for a student who will not study the semester
but wants to keep their place (vacante) for a future term.

## Actors
Enrollment clerk.

## Preconditions
- Student eligible per UC-ENR-001 and not blocked by debt.

## Main flow
1. Clerk selects the student and chooses "Reservar".
2. System validates eligibility/vacancy through the same search path as regular enrollment.
3. System writes the reservation via `EnrollmentService.reserveEnrollment` (type `022002`),
   audit-stamped.

## Alternative flows
- **A1 — Debtor:** action disabled at the results grid (BR-ENR-04).

## Business rules
BR-ENR-11 (type catalog), BR-ENR-04.

## Edge cases
- Interaction between an existing reservation and a later regular enrollment (does the
  reservation get retagged/consumed?) — not yet traced in code.

## Open questions
- Maximum reservation duration (semesters)? Not enforced in code as far as verified.
- Does a reservation require any payment? (Reservation fee concept not confirmed.)

## Related GitHub issues
- None specific; part of the enrollment service migration (PRs #25/#26).
