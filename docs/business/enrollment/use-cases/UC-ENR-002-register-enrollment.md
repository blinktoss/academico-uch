# UC-ENR-002 — Register regular enrollment

## Objective
Enroll an eligible student in a set of open sections for the semester, producing a Regular
enrollment record (`022001`).

## Actors
Enrollment clerk.

## Preconditions
- Student found via UC-ENR-001 and not blocked by debt.
- Courses have been opened for the term with sections, vacancies, and schedules
  (UC-SCH-002/003/004).

## Main flow
1. Clerk opens the course offering for the student. The system lists only courses the student may
   take (BR-ENR-07): prerequisite chain approved or prerequisite-free, not already attempted,
   opened for the term and specialty.
2. Clerk selects the sections to enroll.
3. System validates **section vacancy** for every selected section (BR-ENR-08): live count of
   active enrollments vs. the section's vacancy ceiling.
4. System validates **schedule consistency** across the full selected set (BR-ENR-09): any two
   sections sharing a (day, period-slot) pair conflict.
5. System registers the enrollment in a single transaction (`EnrollmentService.registerEnrollment`):
   enrollment header + one row per course/section, stamped with audit fields.
6. Confirmation shown; the student's snapshot reflects the Regular enrollment.

## Alternative flows
- **A1 — Section full:** submission rejected with a message; nothing is written.
- **A2 — Schedule conflict:** hard block — a modal lists the conflicting sections; no override or
  "proceed anyway" exists; nothing is written.

## Business rules
BR-ENR-07, BR-ENR-08, BR-ENR-09, BR-ENR-10 (absence of credit/failure limits), BR-ENR-11.

## Edge cases
- **Concurrency on the last seat:** the vacancy check and the insert are not atomic — two
  simultaneous submissions can both pass the check (documented, unfixed; needs DB constraint or
  pessimistic lock).
- The prerequisite "approved" threshold is 10.5, while the Acta passing grade is 11 — two
  different thresholds coexist by design/history (see gap analysis).

## Open questions
- Should a credit limit / repeated-failure limit exist? Confirmed absent AS-IS; product decision
  pending.

## Related GitHub issues
- Schedule-conflict semester bug (hardcoded `sem_id = 9`) fixed as QW-02; regression-tested.
- Open enrollment edge cases: #33–#35.
