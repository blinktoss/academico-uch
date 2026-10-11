# Admission (cross-repo pointer)

Admissions are **not implemented in this repository**. The flow lives in:

- **`sigu_admision`** — full admissions: candidate registration through exam day (JSF 1.2 +
  RichFaces 3.3 + stored procedures).
- **`wcali`** — scoring: processes admission-exam results; qualifying candidates who secure a
  vacancy become students.

This folder documents only the **boundary** with the academic system.

## Use cases

| ID | Use case | Status |
|---|---|---|
| UC-ADM-001 | Candidate registration → exam | Identified (owned by `sigu_admision`) |
| UC-ADM-002 | Exam scoring & vacancy assignment | Identified (owned by `wcali`) |
| UC-ADM-003 | Admitted candidate becomes a student here | Discovery needed |

## What we know about the boundary

- Admitted candidates enter this system's **student registry** (UC-STU-002); transfer students
  additionally go through **Convalidación** (UC-GRD-005).
- All apps share the single MySQL schema, so the handoff is data-level, not API-level.

## Pending discovery
- Whether the handoff (UC-ADM-003) is automated (shared tables / stored procedure) or manual
  re-registration by staff — requires inspecting `sigu_admision`/`wcali` (separate repos, separate
  session; this session is scoped to `sistema_academico`).
- Where entry specialty/plan and student code are first assigned in that handoff.
