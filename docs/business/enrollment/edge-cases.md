# Enrollment — edge cases

Known, verified edge cases (see also the per-use-case Edge cases sections):

- **Last-seat race condition** — vacancy check-then-insert is not atomic; two concurrent
  submissions can both win the last seat. Documented, unfixed.
- **Prórroga linkage** — RESOLVED (#33, building on #45): registering a Prórroga now atomically
  appends `AdProrroga` history and syncs `AlutarFechaProrroga` (the date the gate reads) via
  `ProrrogaService`; the latest-registered extension wins, and every writer (grant modal, massive
  block editor) keeps history. The single-edit modal no longer touches the deadline.
- **Dual passing thresholds** — prerequisite check uses ≥ 10.5; Acta pass/fail uses ≥ 11.
- **Never-exercised states** — `OBSERVADO`/`CONAFU` student states exist only in the catalog.
- **Withdrawn-but-active legacy rows** — students whose type says withdrawn but `AluActivo='1'`;
  excluded by the eligibility filter since the fix, but the rows still exist in data.

## Pending discovery
- Reservation ↔ later enrollment interaction.
- "Ampliaciones" modal semantics (UC-ENR-008).
- Rectification deadline/limit policy.
