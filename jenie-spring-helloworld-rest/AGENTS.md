# Spring MVC REST application instructions

Apply the [repository instructions](../AGENTS.md) first. This guide covers `jenie-spring-helloworld-rest`.

## Responsibility

This application exposes imperative Helloworld services through Spring MVC. Main entry points under
`src/main/java/org/jenie/spring/helloworld/` include `controller/ArticleController.java`,
`controller/ErrorTypeController.java`, and `exception/GlobalRestExceptionHandler.java`.

## Changes

- Keep controllers focused on HTTP adaptation and delegate business operations to the shared `ArticleService`.
- Preserve the `/{service}/article` routing contract, request defaults, HTTP status codes, and error response structure.
  Compare changes with the reactive REST application and integration clients when they share the same API behavior.
- Retain the imperative MongoDB/application configuration. Changing the execution model or virtual-thread settings is
  an experiment change that should be explicit, rather than an incidental controller refactor.
- Application profiles and logging settings are under `src/main/resources/`. Verify the selected profile and external
  database configuration before exercising a running server.

## Verification

```sh
./gradlew :jenie-spring-helloworld-rest:test :jenie-spring-helloworld-rest:checkFormat :jenie-spring-helloworld-rest:checkstyleMain :jenie-spring-helloworld-rest:checkstyleTest
```

Use the existing controller and exception-handler tests for HTTP contract changes. Live endpoint validation belongs with
the environment-specific clients in `jenie-spring-test`.
