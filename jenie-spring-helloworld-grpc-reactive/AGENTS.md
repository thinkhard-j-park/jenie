# Reactive Spring gRPC application instructions

Apply the [repository instructions](../AGENTS.md) first. This guide covers `jenie-spring-helloworld-grpc-reactive`.

## Responsibility

This application combines Spring gRPC, shaded Netty, Reactor gRPC stubs, and reactive MongoDB services.
`src/main/java/org/jenie/spring/helloworld/grpc/ArticleGrpc.java` extends
`ReactorArticleServiceGrpc.ArticleServiceImplBase` and delegates to `ReactiveArticleService`.

## Changes

- Preserve the generated Reactor RPC signatures and compose request and response publishers through the shared service.
  Avoid blocking or detached subscriptions in RPC handling; preserve cancellation and error propagation.
- Keep domain-error translation in `exception/GlobalGrpcExceptionHandler.java` consistent with the RPC contract and
  the other gRPC variants. Inspect `config/ServerConfig.java` for global access-log interceptor registration.
- Shared message and RPC definitions belong in `jenie-spring-helloworld-common/src/main/proto/`. Regenerate stubs there
  and compile consumers after contract changes.
- Preserve reactive MongoDB bean selection and Spring gRPC transport dependencies. Threading and profile changes should
  remain explicit parts of an experiment.

## Verification

```sh
./gradlew :jenie-spring-helloworld-grpc-reactive:classes :jenie-spring-helloworld-grpc-reactive:checkFormat :jenie-spring-helloworld-grpc-reactive:checkstyleMain
```

Add focused tests for changed publisher/error behavior and run this module's `test` and `checkstyleTest` tasks when
tests are added. Live comparisons use the reactive gRPC clients in `jenie-spring-test`.
