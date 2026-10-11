# Enrollment (Matrícula)

The most critical operational process: students must re-enroll every semester, subject to
academic eligibility, financial standing, section vacancy, prerequisites, and schedule
consistency. AS-IS implementation: `net.uch.academica` (`MbMatricula`, `EnrollmentService`),
view `Academica/Matricula.jsp`.

Engineering companion: [`docs/modules/academica.md` → Enrollment](../../modules/academica.md#enrollment-matrícula)
and the full evidence trail in [`docs/Enrollment_Module_AS-IS_Report.md`](../../Enrollment_Module_AS-IS_Report.md).

## Actors

- **Enrollment clerk / administrative staff** — operates all use cases (the app serves back-office
  staff; students do not self-enroll in this system).
- **Cashier subsystem** — indirect actor: tariff/debt data gates every enrollment action.

## Use cases

| ID | Use case | Status |
|---|---|---|
| [UC-ENR-001](use-cases/UC-ENR-001-search-eligible-students.md) | Search eligible students | Documented |
| [UC-ENR-002](use-cases/UC-ENR-002-register-enrollment.md) | Register regular enrollment | Documented |
| [UC-ENR-003](use-cases/UC-ENR-003-rectify-enrollment.md) | Rectify enrollment | Documented |
| [UC-ENR-004](use-cases/UC-ENR-004-reserve-enrollment.md) | Reserve enrollment | Documented |
| UC-ENR-005 | Cancel enrollment (logical delete) | Identified |
| UC-ENR-006 | View enrolled courses / history | Identified |
| UC-ENR-007 | View debt detail | Identified |
| UC-ENR-008 | Enrollment-period extensions ("ampliaciones") | Discovery needed |

## Related GitHub issues

Open enrollment issues #33–#35 (edge cases from the service-migration sprint). The
implicit-state-machine gap (enrollment/student states) has a dedicated ADR plan — see
[`docs/Student_Enrollment_State_Analysis.md`](../../Student_Enrollment_State_Analysis.md).
