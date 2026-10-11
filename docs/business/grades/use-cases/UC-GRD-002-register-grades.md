# UC-GRD-002 — Register/update grades for a section

## Objective
Record students' grades for each evaluation component of a section, throughout the semester,
before any official record exists.

## Actors
Academic staff / clerk (teacher-entered grades arrive via the intranet; web entry can be locked —
UC-GRD-006).

## Preconditions
- Section exists for the term with an assigned evaluation system (UC-CUR-006, UC-SCH-002/003).
- Students are enrolled in the section.
- The section's Principal Acta is **not** published (grades feeding a published Acta no longer
  affect it — BR-GRD-09).

## Main flow
1. Clerk navigates the sections tree and selects a section (UC-GRD-001).
2. System renders one column per evaluation component, driven by the section's evaluation system
   (BR-GRD-05), one row per enrolled student.
3. Clerk enters/edits grades. Input is constrained to 0–20 in half-point steps (BR-GRD-01) —
   filtered in the browser and re-validated server-side.
4. Clerk saves; the system persists the batch of grades atomically.

## Alternative flows
- **A1 — Invalid grade value:** rejected server-side even if client validation is bypassed;
  nothing saved for the invalid entry.

## Business rules
BR-GRD-01, BR-GRD-05, BR-GRD-09 (interaction with published Actas).

## Edge cases
- Grades are freely editable all semester; only Acta publication freezes anything.
- The batch save is atomic today only via a manual transaction inside the DAO — a known
  service-extraction target (engineering detail; behavior is atomic either way).

## Open questions
- Is there a business deadline for grade entry per term (entry period)? Not enforced in code as
  far as verified — the web-lock mechanism (UC-GRD-006) may be the manual instrument for this.

## Related GitHub issues
- Grade-write service extraction is a planned slice on the service-layer foundation branch.
