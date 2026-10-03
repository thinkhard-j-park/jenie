# Imperative Armeria gRPC application instructions

Apply the [repository instructions](../AGENTS.md) first. This guide covers `jenie-spring-helloworld-armeria-grpc`.

## Responsibility

This application exposes imperative Helloworld services through Armeria gRPC. Under
`src/main/java/org/jenie/spring/helloworld/`, `grpc/ArticleGrpc.java` adapts the generated imperative service contract,
`config/ArmeriaSeverConfig.java` registers services and server options, and `exception/GrpcExceptionHandler.java`
translates failures. The module also includes Hello service/client examples.

## Changes

- Keep business behavior in the shared `ArticleService` and message conversion/observer handling in the gRPC adapter.
- Preserve Armeria service and exception-handler registration. Shared RPC definitions belong in
  `jenie-spring-helloworld-common/src/main/proto/`.
- Treat `useBlockingTaskExecutor`, virtual-thread executor selection, compression, access logging, documentation, and
  JSON transcoding options as explicit experiment settings. Consider the executor used for blocking business calls.
- Keep Armeria-specific configuration in this module and shared access-log behavior in the existing web-module helper.
  Compare RPC status and metadata with the Spring gRPC and reactive variants when changing error behavior.

## Verification

```sh
./gradlew :jenie-spring-helloworld-armeria-grpc:classes :jenie-spring-helloworld-armeria-grpc:checkFormat :jenie-spring-helloworld-armeria-grpc:checkstyleMain
```

Add focused tests for changed adapter/configuration behavior and run this module's `test` and `checkstyleTest` tasks when
tests are added. Use the configured Armeria clients in `jenie-spring-test` for live RPC verification.
