# Obligations Tracker Constitution

## Core principles

### I. Dates are evaluated in Europe/Amsterdam
Every business date, including "today", is the local date in Europe/Amsterdam. The current date must be controllable in tests, so no production code reads the system clock directly.

### II. Errors use RFC 9457 Problem Details
Every error response uses `application/problem+json` with a stable `title`, `status`, and `detail`. Validation errors name the offending field.

### III. Dates on the wire are ISO-8601
The API accepts and returns dates as ISO-8601 strings (`yyyy-MM-dd`).

### IV. Every behavior traces to an acceptance criterion
No feature code is written without a linked acceptance criterion in the feature spec. Tests name the criterion they prove.

### V. The Maven wrapper is the build
All builds and tests run through `./mvnw`. A task is not verified until its verification command passes.

## Governance

The constitution changes only through a reviewed pull request with a recorded decision. Feature specs may add rules; they may not contradict these principles.

Version: 1.0.0 | Ratified: 2026-09-30
