# Grades & Actas — future enhancements (candidates already surfaced)

- Extract the **grade-write service** (`MbNota.insertar` batch) — planned slice; removes the
  manual-transaction DAO pattern.
- Convalidación service extraction (#24) and surfacing `sp_convalidacion_cursos` rules into
  documented, testable logic.
- Browser click-test of the Acta UI (read-only fields + Publicar confirm) — pending.
- Define per-action authorization (who may publish vs enter grades) once RBAC Phase 2 lands.
