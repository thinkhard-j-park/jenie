# Imperative Spring gRPC application instructions

Apply the [repository instructions](../AGENTS.md) first. This guide covers `jenie-spring-helloworld-grpc`.

## Responsibility

This application uses Spring gRPC with the shaded Netty transport. `grpc/ArticleGrpc.java` under
`src/main/java/org/jenie/spring/helloworld/` extends the generated `ArticleServiceGrpc.ArticleServiceImplBase` and calls
the shared imperative `ArticleService`. Server configuration and error translation live in `config/ServerConfig.java`
and `exception/GlobalGrpcExceptionHandler.java`.

## Changes

- Keep request/message conversion in the gRPC adapter and business rules in `jenie-spring-helloworld-web`.
- Preserve observer completion/error behavior and the mapping from domain errors to gRPC status and metadata.
- Edit shared RPC contracts in `jenie-spring-helloworld-common/src/main/proto/`; regenerate rather than modifying stubs.
- Keep Spring gRPC interceptor registration and shaded Netty dependencies distinct from the Armeria applications.
  Coordinate contract changes with the other gRPC variants and integration clients.
- Preserve the imperative configuration and explicitly document experiment changes to threading, logging, or profiles.

## Verification

```sh
./gradlew :jenie-spring-helloworld-grpc:classes :jenie-spring-helloworld-grpc:checkFormat :jenie-spring-helloworld-grpc:checkstyleMain
```

For changed adapter behavior, add focused tests and run this module's `test` and `checkstyleTest` tasks. Live RPC checks
use `jenie-spring-test` and require the appropriate server and MongoDB environment.
