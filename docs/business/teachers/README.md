# Teachers (Docentes)

Teacher registry, availability declaration, and attendance tracking. AS-IS implementation:
`net.uch.academica` (`MbDocente`, `MbDocenteDisponibilidad`) and `net.uch.asistencia`
(`MbAsistenciaDocente`), views `Academica/Docentes.jsp`, `DocenteDisponibilidad.jsp`,
`Asistencia/AsistenciaDocente.jsp`.

## Actors
- **Academic staff** — maintains registry and availability.
- **Attendance monitor** — records class sessions and marks.

## Use cases

| ID | Use case | Status |
|---|---|---|
| UC-TCH-001 | Manage teacher registry | Identified |
| UC-TCH-002 | Declare teacher availability | Identified |
| UC-TCH-003 | Track teacher attendance (sessions & marks) | Discovery needed |

## Known business rules (confirmed)

- **BR-TCH-01** — Availability is declared per teacher as time windows (modal per teacher) —
  consumed (presumably) by term planning; the enforcement link to section scheduling is
  **unconfirmed** (discovery).
- **BR-TCH-02** — Attendance is session-based: sessions are created/validated per section
  (`validarNuevaSesion`), then per-session marks are saved (`guardarMarcaAsistencia`) with
  attendance types from the catalog.
- **BR-TCH-03** — Registry follows logical deletion.

## Pending discovery
- Whether availability actually constrains schedule assignment or is informative only.
- Attendance types (catalog group) and any derived reporting/consequences (payroll? compliance?).
- Session lifecycle rules (`actualizarSesion` semantics; who may edit a past session).

## Placeholders
- User stories / edge cases / future enhancements — pending first analysis pass.
