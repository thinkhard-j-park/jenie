# Spring WebFlux REST application instructions

Apply the [repository instructions](../AGENTS.md) first. This guide covers `jenie-spring-helloworld-rest-reactive`.

## Responsibility

This is the reactive REST application using WebFlux, Reactor Netty, and `ReactiveArticleService`. Under
`src/main/java/org/jenie/spring/helloworld/`, inspect `controller/ArticleController.java`,
`exception/GlobalRestExceptionHandler.java`, and `config/ServerConfig.java` for HTTP adaptation and server behavior.

## Changes

- Keep the `/{service}/article` API, DTOs, request defaults, and error semantics aligned with the imperative REST
  application where the two variants implement the same operation.
- Return composed publishers through the controller boundary. Preserve error and cancellation signals; avoid adding
  `.block()` or detached `.subscribe()` calls to request handling.
- Put shared business changes in `jenie-spring-helloworld-web` and contract changes in `jenie-spring-helloworld-common`.
- Preserve reactive MongoDB bean selection and Reactor Netty configuration. Changes to access logging, compression,
  server configuration, or profiles can affect performance comparisons and should be recorded with the experiment.

## Verification

```sh
./gradlew :jenie-spring-helloworld-rest-reactive:classes :jenie-spring-helloworld-rest-reactive:checkFormat :jenie-spring-helloworld-rest-reactive:checkstyleMain
```

Add focused controller/error tests for changed HTTP behavior and run this module's `test` and `checkstyleTest` tasks when
tests are added. Use `jenie-spring-test` for live endpoint comparisons after its environment is configured.
