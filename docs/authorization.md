# Authorization & Login Hardening

This document describes the role-based authorization layer and the login-lockout policy added to the
academic system. It is the reference for the `role → module → screen → report/servlet` mapping
required by the feature's acceptance criteria.

## 1. Model

Authentication only proves *who* you are (a `BeanUser` in the HTTP session attribute `usuario`).
Authorization decides *what* you may use, and reuses the role/menu model that already exists in the
shared database:

```
tb_usuario.rol_id ──> tb_roles ──< tb_menu_rol >── tb_menu
```

A **module** is the `men_accion` navigation outcome of an active **leaf** menu item
(`tb_menu.men_opc = '2'`). Each `men_accion` maps 1:1 to a screen in
`src/main/webapp/WEB-INF/faces-config.xml` (e.g. `Alumnos` → `/Academica/Alumnos.jsp`). A role's
**grant set** is the distinct set of `men_accion` outcomes it can reach through active
`tb_menu_rol` rows — computed once at login by
[`HSMenuDAO.findGrantedModules(rolId)`](../src/main/java/net/uch/configuracion/dao/impl/HSMenuDAOImpl.java)
and stored on the session [`BeanUser`](../src/main/java/net/uch/autenticacion/jsf/beans/BeanUser.java)
(`rolId`, `grantedModules`, `superAdmin`).

Because a report/photo servlet is always launched from a screen, it inherits that screen's module.
Access is granted when:

1. the user is the **god-admin** (`usu_id = 1`, `superAdmin = true`) — bypasses all checks; or
2. the user's role grant set **contains** the module that owns the requested resource.

Otherwise the response is **`403 Forbidden`** (plus `Referrer-Policy: no-referrer`). The rule lives in
one place, [`ResourceAuthorizer`](../src/main/java/net/uch/common/security/ResourceAuthorizer.java);
module tokens are constants in
[`Modules`](../src/main/java/net/uch/common/security/Modules.java); reports declare their owning
module in the [`ReportType`](../src/main/java/net/uch/common/web/ReportType.java) enum.

This means the cascade is automatic: a role that lacks the `Alumnos` screen (e.g. a cashier) is
forbidden from the student photo (`/foto`) and the student receipt report
(`/report?type=alumno-comprobante`) even when accessed directly by URL — not merely hidden from the
menu.

## 2. Endpoint → owning module

| Endpoint | Required module (`men_accion`) | Owning screen |
|---|---|---|
| `GET /report?type=matricula-ficha` | `Matricula` | `Academica/Matricula.jsp` |
| `GET /report?type=matricula-consolidado` | `Matricula` | `Academica/Matricula.jsp` |
| `GET /report?type=notas-seccion` | `Notas` | `Academica/Notas.jsp` |
| `GET /report?type=alumno-comprobante` | `Alumnos` | `Academica/Alumnos.jsp` |
| `GET /report?type=convalidacion-boleta` | `Convalidacion` | `Academica/Convalidacion.jsp` |
| `GET /report?type=notascl-seccion` | `NotasCL` | `CursoLibre/NotasCL.jsp` |
| `GET /report?type=historial-pagos` | `EdicionEstructPago` | `Administrativa/EdicionEstructPago.jsp` |
| `GET /report?type=cuadre-caja` | `Pagos` | `Administrativa/Pagos.jsp` |
| `GET /report?type=banco-respuesta` | `ProcesoBanco` | `Banco/ProcesoBanco.jsp` |
| `GET /foto` | `Alumnos` | `Academica/Alumnos.jsp` |
| `GET /foto-cl` | `AlumnosCL` | `CursoLibre/AlumnosCl.jsp` |
| `GET /comprobante` | `Pagos` | `Administrativa/Pagos.jsp` |
| `GET /foto-actividad` | `Actividades` | `TablaSistema/Actividades.jsp` |

## 3. Reading the live role → module snapshot

The grant set is data, not code. To see which modules (and therefore which reports) a role may use,
query the shared DB:

```sql
SELECT r.rol_id, r.rol_tipo,
       GROUP_CONCAT(m.men_accion ORDER BY m.men_accion SEPARATOR ', ') AS modules
FROM tb_roles r
JOIN tb_menu_rol mr ON mr.rol_id = r.rol_id AND mr.menrol_activo = '1'
JOIN tb_menu     m  ON m.men_id  = mr.men_id
                   AND m.men_activo = '1' AND m.men_opc = '2' AND m.men_accion IS NOT NULL
WHERE r.rol_activo = '1'
GROUP BY r.rol_id, r.rol_tipo
ORDER BY r.rol_id;
```

Example (development DB), illustrating the cascade:

| Role | Sample granted modules | Can run | Forbidden |
|---|---|---|---|
| `admin` (rol 1, god-admin) | *all* | everything | — |
| `caja` (rol 2) | `Pagos`, `EdicionEstructPago`, `AlumnosCL`, `MatriculaCL` | `/comprobante`, `cuadre-caja`, `historial-pagos`, `/foto-cl` | `/foto`, `alumno-comprobante`, `notas-seccion`, `matricula-ficha` |
| `academica` (rol 3) | `Alumnos`, `Notas`, `Matricula`, `Convalidacion` | `/foto`, `notas-seccion`, `matricula-ficha`, `convalidacion-boleta` | `/comprobante`, `cuadre-caja`, `banco-respuesta` |
| `operador` (rol 5) | `Alumnos` | `/foto`, `alumno-comprobante` | everything payment/notes related |

## 4. Login lockout

`HSUsuarioDAOImpl.validateUser` now hardens the login flow (no attempt counter existed before):

- A wrong password increments `tb_usuario.usu_failed_attempts`.
- On reaching **`MAX_FAILED_ATTEMPTS = 5`**, the account is **permanently locked**
  (`usu_locked = '1'`). Locked accounts are refused with a distinct message
  ("Cuenta bloqueada por intentos fallidos. Contacte al administrador.").
- A successful login resets the counter and clears the lock.

A dedicated `usu_locked` flag is used rather than overloading `usu_vigente` ("enabled/current"), so
"locked by brute force" stays distinct from "deactivated". Columns are added by the additive
migration [`db/migrations/V3_add_login_lockout_tb_usuario.sql`](../db/migrations/V3_add_login_lockout_tb_usuario.sql).

**Admin unlock** (there is no UI yet — flip the flag directly):

```sql
UPDATE tb_usuario SET usu_locked = '0', usu_failed_attempts = 0 WHERE usu_usuario = '<username>';
```

## 5. Session tracking (cookie-only)

`web.xml` pins session tracking to **`COOKIE`** with an `http-only` session cookie. This is required
for the authorization above to actually hold: by default Tomcat also tracks sessions via the URL, so
`h:outputLink`/`encodeURL` print and report links (which open in a new tab) get a `;jsessionid=<id>`
appended. Such a link carries the logged-in user's session, so anyone who opens a copied link rides
that user's session and bypasses their own role checks (session hijacking via URL). Cookie-only
tracking stops both the emission of `;jsessionid` in links and the acceptance of a session id from
the URL path.

> In PROD (HTTPS) set `<secure>true</secure>` on the cookie. Adding `SameSite` is a further
> hardening configured at the Tomcat `CookieProcessor` level (not `web.xml`).

## 6. Screen-level authorization (Phase 2)

`ScreenAccessFilter` (mapped `*.jsf`) extends the same rule to direct screen-URL access: a logged-in
user who opens a screen their role does not grant gets **403**, not just a hidden menu item.
`ScreenAccessGuard` resolves a view's owning module from `faces-config.xml` (a navigation
`from-outcome` is a `men_accion`, its `to-view-id` is the screen) and treats a view as gated only
when that outcome is a role-assignable module (`HSMenuDAO.findAllModules()`). Unmapped or non-gated
views (login, welcome, shared pages) pass. Authentication for anonymous requests is left to the
existing per-page menu redirect; the filter only adds the authorization check for logged-in users.

## 7. Out of scope (deferred follow-ups)

- Short-lived HMAC-signed resource parameters to block id tampering/enumeration.
- Per-record IDOR ownership checks (whether a specific row is within the user's remit).
- An admin screen to unlock accounts / reset attempt counters.
