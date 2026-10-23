# Obligations Tracker

Demo application for the Pluralsight course *Spec-Driven Development with AI in Practice*.

The app is a small Spring Boot REST API for regulatory filing obligations: create an obligation, see its deadline state, escalate and acknowledge it, and list what is due soon. It is built step by step with Spec-Driven Development.

## Prerequisites

- JDK 21 or newer
- Git
- An AI coding assistant that can use the Spec Kit skills in `.github/skills` (for example GitHub Copilot in VS Code)
- Always use the Maven wrapper (`./mvnw`), never a separately installed Maven

```bash
./mvnw test
./mvnw spring-boot:run
```

The server listens on `http://localhost:8080/`. To start with demo data and a fixed date, run `./mvnw spring-boot:run -Dspring-boot.run.profiles=demo`.

## How to follow along

Each clip has a branch named `recording-m<module>c<clip>`. The branch is the **starting point of that clip**, or the state shown in the clip, so you can check it out and follow what is on screen.

```bash
git clone https://github.com/BrightBoost/ps-spec-driven-development-in-practice.git
cd ps-spec-driven-development-in-practice
git checkout recording-m2c1
./mvnw test
```

Commit or stash your own changes before switching branches. Branches build on each other, so you can diff two of them to see exactly what a clip changed:

```bash
git diff recording-m2c1 recording-m2c2
```

## Branches per clip

| Clip | Branch | What it contains |
|---|---|---|
| M1C1 | `recording-m1c1` | Empty Spring Boot project and the first draft of the spec |
| M1C2 | `recording-m1c2` | Scoped regulatory filing obligations feature |
| M1C3 | `recording-m1c3` | Work from the M1C3 and M1C4 recordings |
| M1C4 | `recording-m1c4` | Requirements, EARS acceptance criteria, test outline; spec approved for planning |
| M2C1 | `recording-m2c1` | Spec Kit init, constitution, spec and plan |
| M2C2 | `recording-m2c2` | Plan review, traceability table, DEC-001 |
| M2C3 | `recording-m2c3` | Task breakdown (`tasks.md`) |
| M2C4 | `recording-m2c4` | DEC-002 (escalation recipients); specs aligned with the reference code |
| M3C1 | `recording-m3c1` | Implementing T-003 (deadline state policy) |
| M3C2 | `recording-m3c2` | Improved tests |
| M3C3 | `recording-m3c3` | Baseline with deliberately fabricated drift between spec and code |
| M3C4 | `recording-m3c4` | Drift review |
| M3C5 | `recording-m3c5` | `AGENTS.md` added at the repository root |
| M5C1 | `recording-m5c1` | DEC-003 demo: acknowledge an escalated obligation (T-011) |

`main` holds the finished project, including DEC-003 and T-011. Module 4 has no branch. If a clip you are watching is not in this table, stay on the closest earlier branch.

## Repository layout

- `specs/001-regulatory-filing-obligations/` is the feature folder. `.sdd` is a symlink to it, so use either path.
  - `spec.md`: behavior and acceptance criteria (AC-IDs)
  - `plan.md`: approved design and traceability
  - `tasks.md`: tasks (T-IDs) with verification commands
  - `decisions.md`: approved changes (DEC-IDs)
  - `data-model.md`, `contracts/openapi.yaml`, `quickstart.md`, `research.md`
- `.specify/memory/constitution.md`: project-wide principles
- `.github/skills/`: Spec Kit skills such as `speckit-specify`, `speckit-plan`, `speckit-tasks` and `speckit-implement`
- `AGENTS.md`: the working agreement the AI assistant reads first (from M3C5 onward)
- `src/`: the application and its tests

## Working rules

- Work on one task at a time and run its verification command from `tasks.md`.
- Run `./mvnw test` before calling a task done.
- If the spec, plan and tasks disagree, the spec wins unless a newer decision says otherwise.
- Never change a test's expected value just to make it pass.
- The H2 database is written to `./data/` (ignored by git). Delete that folder to reset the data.
