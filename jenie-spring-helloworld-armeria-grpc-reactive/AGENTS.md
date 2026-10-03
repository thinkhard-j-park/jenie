# Reactive Armeria gRPC application instructions

Apply the [repository instructions](../AGENTS.md) first. This guide covers `jenie-spring-helloworld-armeria-grpc-reactive`.

## Responsibility

This application combines Armeria, Reactor gRPC stubs, and `ReactiveArticleService`.
`src/main/java/org/jenie/spring/helloworld/grpc/ArticleGrpc.java` extends the generated
`ReactorArticleServiceGrpc.ArticleServiceImplBase`. Server options and error translation live in
`config/ArmeriaSeverConfig.java` and `exception/GrpcExceptionHandler.java` in the same package tree.

## Changes

- Keep RPC work in the returned publisher and preserve cancellation, errors, and reactive transaction context. Avoid
  blocking or detached subscriptions in request handling.
- Keep shared business rules in the web module and shared protobuf contracts in the common module. Compare RPC results
  and domain-error translation with the other gRPC variants when changing a shared operation.
- Preserve Armeria service registration and the configured reactive MongoDB path. The available blocking-task and
  virtual-thread switches are experiment controls; changing them is separate from changing reactive business logic.
- Record changes to compression, access logs, documentation, JSON transcoding, and profiles when they affect benchmark
  conditions. Preserve the intended comparison with the imperative Armeria application.

## Verification

```sh
./gradlew :jenie-spring-helloworld-armeria-grpc-reactive:classes :jenie-spring-helloworld-armeria-grpc-reactive:checkFormat :jenie-spring-helloworld-armeria-grpc-reactive:checkstyleMain
```

Add focused tests for changed publisher/error behavior and run this module's `test` and `checkstyleTest` tasks when
tests are added. Live RPC checks use the environment-specific clients in `jenie-spring-test`.
