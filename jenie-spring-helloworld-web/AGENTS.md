# Shared Helloworld business module instructions

Apply the [repository instructions](../AGENTS.md) first. This guide covers `jenie-spring-helloworld-web`.

## Responsibility

Despite its name, this module is the shared business and persistence layer for all six server applications.
`service/` and `repository/` contain imperative implementations; `reactive/service/` and `reactive/repository/` contain
their reactive counterparts. Entities, mappers, domain errors, configuration, and access-log integrations also live here
under `src/main/java/org/jenie/spring/helloworld/`.

## Changes

- Keep article and board rules, writer checks, state transitions, pagination, and DTO mappings consistent between the
  imperative and reactive paths when changing shared behavior.
- Preserve service/database keys through repository calls and `@MongoKeyBasedTransactional` boundaries. Multi-document
  writes must remain within the intended transaction.
- Compose reactive writes and reads into the returned publisher so completion and errors remain visible to the caller.
  Avoid detached subscriptions in business operations.
- `ConditionalOnImperative` and `ConditionalOnReactive` use `mongodb.setting.type` (`sync` or `reactive`). Preserve bean
  selection and the optional dependency boundaries that let different server applications consume this module.
- Keep protocol-specific controllers and gRPC service adapters in their application modules. Coordinate changes to
  shared errors and DTOs with those adapters and `jenie-spring-helloworld-common`.

## Verification

```sh
./gradlew :jenie-spring-helloworld-web:test :jenie-spring-helloworld-web:checkFormat :jenie-spring-helloworld-web:checkstyleMain :jenie-spring-helloworld-web:checkstyleTest
```

Extend the relevant service, repository, mapper, or configuration tests for behavior changes. Compile affected server
applications when changing APIs or configuration used across modules; use the integration module for live-server checks.
