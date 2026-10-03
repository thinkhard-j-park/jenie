# Repository instructions

## Scope and maintenance

- These instructions apply throughout the repository. Before changing a module, also read its `AGENTS.md` listed below.
  Module instructions add details for their subtree; more specific instructions take precedence within that scope.
- `AGENTS.md` is the canonical instruction file at every level. `CLAUDE.md` and `GEMINI.md` are relative symbolic links to
  the adjacent `AGENTS.md`. Edit the canonical file and preserve the links rather than maintaining separate copies.
- When adding a Gradle module, add its own `AGENTS.md` and both links, then update the module map here.
- Keep these instructions consistent with the code and build files. Put shared guidance here and module-specific
  contracts, entry points, and verification details in the module guide.

## Project intent and structure

Jenie is a single-maintainer research and experimentation project for Java backend technologies. It deliberately
maintains comparable imperative and reactive implementations across REST, Spring gRPC, and Armeria gRPC. Preserve the
purpose and workload of an experiment when improving its implementation.

| Module guide | Responsibility |
| --- | --- |
| [jenie-spring-core](jenie-spring-core/AGENTS.md) | Small shared utilities and resources |
| [jenie-spring-data-mongodb](jenie-spring-data-mongodb/AGENTS.md) | MongoDB connectors, database routing, and transactions |
| [jenie-spring-helloworld-common](jenie-spring-helloworld-common/AGENTS.md) | Shared DTOs, protobuf definitions, and generated contracts |
| [jenie-spring-helloworld-web](jenie-spring-helloworld-web/AGENTS.md) | Shared business services, repositories, entities, and mappings |
| [jenie-spring-helloworld-rest](jenie-spring-helloworld-rest/AGENTS.md) | Spring MVC REST application |
| [jenie-spring-helloworld-rest-reactive](jenie-spring-helloworld-rest-reactive/AGENTS.md) | Spring WebFlux REST application |
| [jenie-spring-helloworld-grpc](jenie-spring-helloworld-grpc/AGENTS.md) | Imperative Spring gRPC application |
| [jenie-spring-helloworld-grpc-reactive](jenie-spring-helloworld-grpc-reactive/AGENTS.md) | Reactive Spring gRPC application |
| [jenie-spring-helloworld-armeria-grpc](jenie-spring-helloworld-armeria-grpc/AGENTS.md) | Imperative Armeria gRPC application |
| [jenie-spring-helloworld-armeria-grpc-reactive](jenie-spring-helloworld-armeria-grpc-reactive/AGENTS.md) | Reactive Armeria gRPC application |
| [jenie-spring-test](jenie-spring-test/AGENTS.md) | Integration clients, JMH benchmarks, fixtures, and performance reports |

`settings.gradle` defines the actual module list. `build-jenie.gradle` groups modules for shared build configuration.

## Implementation conventions

- Use JDK 25 and the checked-in Gradle wrapper. Run Gradle commands from the repository root.
- Check `gradle.properties` for dependency and plugin versions. Shared build behavior lives in `build.gradle`,
  `build-jenie.gradle`, `build-protobuf.gradle`, `build-jacoco.gradle`, and `build-jib.gradle`.
- Follow Spring Java Format and Spring Checkstyle, existing package conventions, and the surrounding nullability
  annotations. Prefer the configured formatter over manual formatting changes.
- Keep business behavior in the shared modules and transport adaptation in the six application modules. When changing
  shared contracts, inspect the affected imperative and reactive consumers together.
- In reactive request paths, compose publishers so errors, cancellation, and transaction context remain connected.
  Blocking at an explicit test or benchmark boundary has a different purpose; follow the test module's guidance.
- Add focused regression coverage for behavior changes. Use the existing JUnit Jupiter, AssertJ, Mockito, and Reactor
  testing patterns where applicable. Comments should explain decisions and non-obvious semantics.
- Keep changes scoped to the task and preserve unrelated worktree changes. Generated build output, coverage reports,
  local IDE state, and `.DS_Store` files do not belong in implementation commits.
- Follow the repository's English commit-message convention and describe the behavior changed and relevant validation.

## Verification

Use the affected module's guide to choose checks. For example, MongoDB unit tests and style checks run with:

```sh
./gradlew :jenie-spring-data-mongodb:test :jenie-spring-data-mongodb:checkFormat :jenie-spring-data-mongodb:checkstyleMain :jenie-spring-data-mongodb:checkstyleTest
```

- Use `:<module>:formatMain` and `:<module>:formatTest` when Java formatting needs correction. Checkstyle task names are
  `checkstyleMain` and `checkstyleTest`.
- Test behavior at the affected layer and compile affected consumers after shared API or protobuf changes. A Gradle
  task reporting `NO-SOURCE` or discovering no tests is not evidence that behavior was tested.
- `jenie-spring-test` contains environment-dependent work and excludes its `helloworld` tests from the default test
  task. Read its guide before running integration tests or benchmarks.
- `publish` and `jib` publish artifacts; they are not routine local verification commands.
- For documentation or symbolic-link changes, verify coverage of the module list, relative link targets, and Markdown
  references. Java tests are unnecessary unless executable code or build behavior also changes.
- Report the checks actually run and distinguish mocked transaction tests, live database tests, and benchmarks. Keep
  historical performance reports tied to their recorded JDK, workload, and environment; current JDK 25 builds do not
  change the meaning of older JDK 21 results.
