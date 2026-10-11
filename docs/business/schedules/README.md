# Schedules & course offering (Programación académica)

What is offered each semester and when: shifts, course opening (*apertura*), sections &
vacancies, weekly timetables, and schedule views. AS-IS implementation: `net.uch.academica`
(`MbTurno`, `MbAperturaCursos`, `MbHorario`, `MbHorarioArea`), views `Academica/Turno.jsp`,
`AperturaCursos.jsp`, `Horario.jsp`, `HorarioArea.jsp`.

## Actors
- **Academic planner / administrative staff** — configures the term before enrollment opens.

## Use cases

| ID | Use case | Status |
|---|---|---|
| UC-SCH-001 | Manage shifts (turnos) & period slots | Identified |
| UC-SCH-002 | Open courses for a semester (apertura) | Identified |
| UC-SCH-003 | Manage sections & vacancies | Identified |
| UC-SCH-004 | Assign weekly schedule to a section | Identified |
| UC-SCH-005 | View schedules by area | Identified |
| UC-SCH-006 | Section detail: teacher assignment, exam averages | Discovery needed |

## Known business rules (confirmed)

- **BR-SCH-01** — The timetable is **quantized**: each shift defines a fixed catalog of 45-minute
  period slots (single start-time per slot, no duration/end field); a multi-period class occupies
  one schedule row per slot it spans. All conflict logic relies on this grid (see BR-ENR-09).
- **BR-SCH-02** — Section vacancy (`SecVacantes`) is a static ceiling; occupancy is always
  computed live from active enrollments, never decremented/stored.
- **BR-SCH-03** — Academic structures must be configured **before** enrollment can run for a term
  (operational ordering — see the functional map's cycle).
- **BR-SCH-04** — Sections/openings follow logical deletion (`SecActivo` etc.).

## Pending discovery
- Rules for editing/removing an opening or section after enrollments exist.
- Teacher-assignment rules within apertura (relationship with teacher availability, UC-TCH-002).
- Classroom (aula) assignment to schedule rows — capacity/availability validation, if any.

## Placeholders
- `user-stories.md`, `edge-cases.md`, `future-enhancements.md` — pending first analysis pass
  (kept in this README until then to avoid empty stubs).
