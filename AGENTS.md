# Agent working agreement

This repository uses Spec-Driven Development. Read this file before any task.

## Authoritative files
- `.sdd/spec.md`: behavior and acceptance criteria (source of intent)
- `.specify/memory/constitution.md`: project-wide principles
- `.sdd/plan.md`: approved design and traceability
- `.sdd/tasks.md`: the active unit of work
- `.sdd/decisions.md`: approved changes and their rationale

If these files disagree with the code, the spec is authoritative unless a newer decision says otherwise.

## Scope
- Work on one task at a time, as named in the prompt
- Do not edit files outside the task's "Files likely affected" without asking
- Never change an expected value in a test to make it pass
- Do not add dependencies or upgrade versions unless the task says so
- Do not add behavior listed under "Out of scope" in the spec, including notifications

## Required evidence
- Run the task's verification command and report the result
- Run `./mvnw test` before reporting a task as Done
- Report every changed file

## Stop and ask when
- A product decision is missing from the spec
- The spec, plan, and tasks disagree
- A suggested change is listed as out of scope
- A test fails for a reason outside the task
