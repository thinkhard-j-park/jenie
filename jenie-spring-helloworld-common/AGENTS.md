# Helloworld common module instructions

Apply the [repository instructions](../AGENTS.md) first. This guide covers `jenie-spring-helloworld-common`.

## Responsibility

This module owns shared Java DTOs and value types under `src/main/java/org/jenie/spring/helloworld/`, plus protobuf
messages and service definitions under `src/main/proto/`. REST applications, gRPC applications, shared business logic,
and integration clients consume these contracts.

## Changes

- Preserve public field meanings, error codes, sort codes, and date/time conversion behavior across Java DTOs and
  protobuf messages. Check DTO conversion methods as well as the `.proto` definition when changing a field.
- Edit `.proto` sources and regenerate their Java, gRPC, and Reactor gRPC output through Gradle. Generated files under
  `build/` are not source files to edit or commit.
- Preserve protobuf field numbers and compatible RPC signatures unless a contract change is part of the task. Inspect
  all four gRPC applications and the clients in `jenie-spring-test` for affected generated types.
- Keep generated-code tooling and its formatting exclusions coordinated with `../build-protobuf.gradle` and this
  module's `build.gradle`. Keep persistence operations and server implementations out of this contract module.

## Verification

```sh
./gradlew :jenie-spring-helloworld-common:generateProto :jenie-spring-helloworld-common:classes :jenie-spring-helloworld-common:checkFormat :jenie-spring-helloworld-common:checkstyleMain
```

After contract changes, compile the affected consumers and run their relevant tests. Generating protobuf output alone
does not establish that client and server adapters still compile or agree on behavior.
