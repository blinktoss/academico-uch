# Grades & official records (Notas / Actas)

Grade entry per section and the official grade records (**Actas**) — sensitive documents that
become **immutable once published**. AS-IS implementation: `net.uch.academica` (`MbNota`,
`MbConvalidacion`, `GradeRecordService`, pure helpers `GradePolicy` / `WeightedGradeCalculator` /
`SubstituteGradeCalculator` / `GradeRecordNomenclature`), views `Academica/Notas.jsp`,
`Academica/Convalidacion.jsp`.

Engineering companion: [`docs/modules/academica.md` → Grades & Actas](../../modules/academica.md#grades--actas).

## Actors

- **Academic staff / clerk** — registers grades and publishes Actas on behalf of teachers (the
  teacher-facing web entry can be locked — UC-GRD-006).
- **Registrar** — consumes published Actas; other suite apps (intranet, reports) read them.

## Use cases

| ID | Use case | Status |
|---|---|---|
| UC-GRD-001 | Browse sections & select for grading | Identified |
| [UC-GRD-002](use-cases/UC-GRD-002-register-grades.md) | Register/update grades | Documented |
| [UC-GRD-003](use-cases/UC-GRD-003-publish-principal-acta.md) | Publish Principal Acta | Documented |
| [UC-GRD-004](use-cases/UC-GRD-004-substitute-acta.md) | Substitute-exam Acta | Documented |
| [UC-GRD-005](use-cases/UC-GRD-005-convalidacion.md) | Convalidación (transfer credit) | Documented |
| UC-GRD-006 | Lock/unlock web grade entry | Discovery needed |
| UC-GRD-007 | Import grades | Discovery needed |
| UC-GRD-008 | Preview/print grade reports | Identified |

## Related GitHub issues
- Convalidación service extraction: #24 (candidate next slice).
