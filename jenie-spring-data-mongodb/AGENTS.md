# MongoDB module instructions

Apply the [repository instructions](../AGENTS.md) first. This guide covers `jenie-spring-data-mongodb`.

## Responsibility

Under `src/main/java/org/jenie/spring/data/mongodb/`, `connector/` owns cluster connections and configuration,
`operation/` owns template routing and caching, and `transaction/` owns database-key transaction aspects. Keep imperative
and reactive implementations usable through their respective configuration paths.

## Routing contracts

- Check all four template routers when changing shared routing behavior: Simple and Caffeine, imperative and reactive.
- Explicit request tag sets replace cluster tags. An empty request tag-set list uses cluster defaults; the `primary`
  read preference must remain free of tags.
- An empty list `[]` and a list containing an empty tag set `[{}]` have different routing meanings. `{}` is an explicit
  unrestricted tag match. In `[{"dc":"us"}, {}]`, it allows another eligible member when the preceding tag set finds
  none. Preserve this fallback rather than filtering it out.
- MongoDB read preferences are immutable. Use the value returned by `withTagSetList` without mutating the caller's
  requested settings.
- Cache keys must preserve tag-set grouping, order, and empty fallback entries. Cover distinct requests on the same
  router in both cache population orders when changing key generation or template configuration.

## Transaction contracts

- Keep `@DBKey`, the annotation's `key`, and its expression-based lookup behavior intact when changing key resolution.
- For reactive `noRollbackFor` business errors, commit first and propagate the original error after commit succeeds.
  Converting an error to empty completion outside `TransactionalOperator` cannot prevent a rollback already performed.
- Keep saved errors local to each subscription. Cover Mono and Flux, synchronous method exceptions, emitted values,
  resubscription, cancellation, and transaction failures when changing the reactive aspect.
- Preserve Reactor transaction context and defer transactional method invocation until the transaction is established.
  Do not turn transaction-manager failures into successful completion.

## Verification

```sh
./gradlew :jenie-spring-data-mongodb:test :jenie-spring-data-mongodb:checkFormat :jenie-spring-data-mongodb:checkstyleMain :jenie-spring-data-mongodb:checkstyleTest
```

The routing and aspect regression tests use real routers or AspectJ proxies with mocked clients/transaction managers.
Keep these tests independent of a running MongoDB instance, and describe their scope accurately when reporting results.
