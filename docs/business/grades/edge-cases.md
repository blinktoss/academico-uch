# Grades & Actas — edge cases

- **Post-publication grade edits** never alter published Actas (frozen snapshot) — verified.
- **Substitute lower than replaced grade** lowers the final — by rule, not a bug.
- **Lowest-grade ties** — deterministic tie-break, unit-tested.
- **Dual thresholds**: prerequisite approval ≥ 10.5 vs Acta pass ≥ 11 (historic coexistence).
- **Historical Actas** published under the legacy substitute math (flat average + rounding)
  remain as-is; immutability forbids retroactive correction.
- **Missing/null component grades at publish time** — exact treatment (zero vs excluded) not yet
  pinned down; verify before building on it.

## Pending discovery
- Web-lock (UC-GRD-006) and import (UC-GRD-007) semantics.
- Substitute-exam eligibility policy (who may take it).
