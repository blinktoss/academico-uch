# Module guides

Per-module documentation for the Sistema Académico. One file per business module, capturing the
**non-obvious**: domain rules, why designs were chosen, and what's pending — the things you can't
get by reading the code.

> **Functional (business-analyst) documentation lives in [`docs/business/`](../business/README.md)** —
> capability map, use-case inventory, business rules, and gap analysis. These guides stay the
> engineering view; link across rather than duplicating.

## How to use these

- **Read the relevant module guide before working on that module.** ("Read the docs.")
- Keep each guide focused on its module; cross-link instead of copying.

## What goes where (one home per fact)

To avoid the same fact rotting in three places:

| Surface | Owns |
|---|---|
| **These module guides** (`docs/modules/*.md`) | Domain rules, business logic, design decisions, per-module pending work |
| **`README.md` → Technical Debt Backlog** | The prioritized, severity-grouped debt checklist (source of truth for debt) |
| **Root `CLAUDE.md`** | Build/run/test commands, cross-cutting conventions, the architecture/layering pattern |

Two rules that keep these guides alive:
1. **Don't document what the code already states** (class lists, signatures, package layout — those
   are readable and rot fast). Document the *why* and the rules.
2. **A fact lives in exactly one surface.** Debt → README backlog. Conventions → CLAUDE.md.
   Domain rules/design → here. Link across them.

## Module index

| Module | Package | Guide | Status |
|---|---|---|---|
| Academica | `net.uch.academica` | [academica.md](academica.md) | Grades/Actas + tree optimization documented; enrollment/students/schedules pending |
| Administrativa (payments) | `net.uch.administrativa` | _pending_ | |
| Cursos Libres | `net.uch.cursolibre` | [cursolibre.md](cursolibre.md) | Tree optimization documented |
| Asistencia (teacher attendance) | `net.uch.asistencia` | _pending_ | |
| Banco | `net.uch.banco` | _pending_ | |
| Configuración | `net.uch.configuracion` | _pending_ | |
| Autenticación | `net.uch.autenticacion` | _pending_ | |

Write a guide when you next do substantial work in a module — populate it from verified knowledge,
not guesses. Empty stubs are worse than an honest "pending" in this table.

## Guide template

```markdown
# <Module> module

## Purpose & scope
What this module is responsible for; its package(s).

## Key domain concepts & rules
The business rules that aren't obvious from the code. The "why".

## Core flows
The main user/business flows, at a high level.

## Design notes
Non-obvious design decisions and the reasoning (e.g. service boundaries, patterns to follow).

## Known technical debt
Brief pointers to the relevant items in README.md's backlog — don't duplicate the detail.

## Pending / in-progress
What's being worked on or deliberately deferred.

## Related modules
Cross-links and how this module interacts with others.
```
