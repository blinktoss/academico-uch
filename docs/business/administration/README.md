# Administration & security (Configuración / Autenticación)

Platform administration: authentication with lockout, users, role-based menu authorization,
reference data (semesters, faculties, specialties, premises), institutional activities, and the
home dashboard. AS-IS implementation: `net.uch.autenticacion` (`MbLogin`), `net.uch.configuracion`
(`MbUsuario`, `MbMenu`, `MbSemestre`, `MbFacultad`, `MbEspecialidad`, `MbLocal`, `MbPabellon`,
`MbAula`, `MbActividad`, `ActivityImageServlet`), `net.uch.inicio` (`MbDashboard`).

Engineering companion: [`docs/authorization.md`](../../authorization.md).

## Actors
- **System administrator** — users, roles, unlocking.
- **Administrative staff** — reference data, activities.
- **All users** — login, dashboard.

## Use cases

| ID | Use case | Status |
|---|---|---|
| UC-SEC-001 | Log in / log out (with lockout) | Identified |
| UC-SEC-002 | Manage users (create, unlock) | Identified |
| UC-SEC-003 | Role-based menu authorization (RBAC) | Identified |
| UC-SEC-004 | Manage semesters | Identified |
| UC-SEC-005 | Manage faculties & specialties | Identified |
| UC-SEC-006 | Manage locals, pavilions, classrooms | Identified |
| UC-SEC-007 | Publish institutional activities | Identified |
| UC-SEC-008 | Home dashboard | Identified |

## Known business rules (confirmed)

- **BR-SEC-01** — Authorization is **role-based via `tb_menu_rol`**: the menu a user sees and the
  servlets they may hit are gated by role (Phase 1 delivered; per-view JSF filtering, HMAC and
  IDOR hardening deferred to Phase 2).
- **BR-SEC-02** — **Login lockout**: repeated failed logins lock the account; an administrator
  unlocks it (`MbUsuario.desbloquear`).
- **BR-SEC-03** — Every page requires a session user (`"usuario"`); missing session forwards to
  the login page (guard in the shared menu include).
- **BR-SEC-04** — Activities are published with an image and date and are consumed by the
  **intranet** (student-facing) — publication here is an outward-facing act.
- **BR-SEC-05** — Reference data follows logical deletion.
- **BR-SEC-06** — Dashboard: Slice 1 delivered (role-based widget assessment done — Chart.js,
  DAO + cache, no REST); further widgets blocked on 6 product-owner decisions.

## Pending discovery
- Semester lifecycle (open/close a term? what depends on semester state?).
- Catalog administration (`tb_catalogo` groups) — where/how codes are maintained.
- Role catalog: the actual roles in production and their intended capability mapping.

## Placeholders
- User stories / edge cases / future enhancements — pending first analysis pass.
