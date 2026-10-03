# Integration and performance module instructions

Apply the [repository instructions](../AGENTS.md) first. This guide covers `jenie-spring-test`.

## Responsibility

This module contains external-service integration clients and tests, MongoDB/router JMH benchmarks, performance input
files under `perf/`, and recorded results under `report/`. Keep unit tests for a production module in that module.
Benchmark classes currently live under `src/test/java/org/jenie/spring/helloworld/test/jmh/`.

## Environment and execution

- The Gradle `test` task excludes `**/helloworld/**` and permits no discovered tests. Its success does not establish that
  the integration suite or benchmarks ran; `--tests` alone does not remove that exclusion.
- Inspect the relevant `local`, `dev`, or `perf` profile, selected protocol, service endpoints, database keys, and required
  fixtures before an environment-dependent run. Use the existing test classes and client configuration as entry points.
- `PerfEnvTests` contains disabled bulk data preparation and ID-file generation. Keep these opt-in and run them only as
  part of an explicitly intended dataset preparation task against the selected experiment environment.
- Keep environment-specific connection values out of new documentation and test output. Refer to configuration locations
  and property names instead of copying credentials or private connection strings.
- Check benchmark discovery and its runner configuration before starting JMH. The configured JMH result destination is
  `report/jmh/result.txt`; preserve existing result provenance when collecting a new run.

## Measurement contracts

- `MongoDriverBenchmark` intentionally waits for each reactive database operation with `.block()` in a sequential
  workload. This ensures the operation finishes inside the measurement boundary, as it does in the synchronous case.
  Preserve that boundary when comparing drivers; a concurrent throughput experiment needs its own stated workload.
- Keep equivalent queries, data, operation order, completion boundaries, and workload parameters across compared variants.
  Record changes to forks, warmup, measurement duration, threads, resource limits, and server configuration.
- Report results with the actual JDK, dataset, topology, and workload. Do not infer general reactive or virtual-thread
  performance claims from one experiment, and do not relabel historical reports with the current build's JDK version.

## Verification without external services

```sh
./gradlew :jenie-spring-test:testClasses :jenie-spring-test:checkFormat :jenie-spring-test:checkstyleTest
```

This compiles test clients and checks source style without executing live integration tests or load generation. For an
actual experiment, record the selected tests/benchmarks, environment, execution command, and resulting artifact paths.
