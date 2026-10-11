# UC-GRD-005 — Convalidación (transfer-credit recognition)

## Objective
Recognize courses a transfer student approved elsewhere (e.g., another university) as approved in
their UCH curricular plan, producing an official Convalidación Acta.

## Actors
Academic staff (registrar function).

## Preconditions
- Student exists in the registry.
- Course-equivalence data available to the stored procedure `sp_convalidacion_cursos`.

## Main flow
1. Clerk selects the student (`MbConvalidacion.capturarAlumno`).
2. System lists the plan's courses; only those the stored procedure deems **equivalent** are
   eligible — ineligible ones render with a disabled grade box (BR-GRD-15).
3. Clerk enters the recognized grade per eligible course.
4. Clerk saves; the system creates the Convalidación Acta (`036003`) **already PUBLISHED**
   (BR-GRD-16) — it never shares a row with the teacher-driven Principal.

## Alternative flows
- **A1 — No equivalent courses:** nothing to convalidate; no Acta created.

## Business rules
BR-GRD-15, BR-GRD-16; grade-value rules BR-GRD-01/02 apply to entered grades.

## Edge cases
- Convalidated approvals feed the prerequisite check for enrollment (a convalidated course counts
  as attempted/approved history).

## Open questions
- Rules inside `sp_convalidacion_cursos` (how equivalence is decided) — not yet reverse-engineered;
  the procedure is the actual rule owner.
- Interaction with admission flow: when exactly is convalidación performed for an admitted
  transfer student (UC-ADM-003)?

## Related GitHub issues
- Convalidación service extraction: #24.
