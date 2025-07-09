# Jenie, Java Genie!

This is a project to experiment with Java-based technologies. The main goals of this project are to:
- Simplify and document the technology used in production-level system.
- Research, development, test technologies.
- Establish and apply best practices.

## Project Structure

### Jenie Common Module
A reusable module for shared functionality.
- [jenie-spring-core](jenie-spring-core/README.md)
- [jenie-spring-data-mongodb](jenie-spring-data-mongodb/README.md)

### Jenie Spring Helloworld
A backend server project that experiments with server-side technologies like Servlet, Webflux, Rest, gRPC, MSA, Service Mesh, etc.
- [jenie-spring-helloworld-common](jenie-spring-helloworld-common/README.md)
- [jenie-spring-helloworld-web](jenie-spring-helloworld-web/README.md)
- jenie-spring-helloworld-rest: rest server
- jenie-spring-helloworld-rest-reactive: reactive rest server.
- jenie-spring-helloworld-grpc: grpc server using [spring-grpc](https://github.com/spring-projects/spring-grpc).
- jenie-spring-helloworld-grpc-reactive: reactive grpc server using [spring-grpc](https://github.com/spring-projects/spring-grpc), [reactive-grpc](https://github.com/salesforce/reactive-grpc).
- jenie-spring-helloworld-armeria-grpc: grpc server using [armeria](https://armeria.dev).
- jenie-spring-helloworld-armeria-grpc-reactive: reactive grpc server using [armeria](https://armeria.dev), [reactive-grpc](https://github.com/salesforce/reactive-grpc).

### Jenie Spring Test
- [jenie-spring-test](jenie-spring-test/README.md) contains a suite of integration tests, benchmark, performance results for modules.
- The following is a consolidated summary of performance test results, evaluating various server implementations, network protocols, and configuration options.
- The comprehensive details of the testing methodology and environment are documented in the [Performance Test Environment](./jenie-spring-test/report/perf/perf.md) report.

#### REST Performance Test Summary


| app                                           | pod resource | virtual thread | iter/s | req/s   | avg (ms) | p95 (ms) | p99 (ms) |
|:----------------------------------------------|:-------------|:---------------|:-------|:--------|:---------|:---------|:--------|
| jenie-spring-helloworld-rest                  | 1 core, 1 Gi | -              | 250    | 754     | 109      | 250      | 353     |
| jenie-spring-helloworld-rest                  | 1 core, 1 Gi | enabled        | 280    | 840     | 5        | 6        | 61      |
| jenie-spring-helloworld-rest-reactive         | 1 core, 1 Gi | -              | 210    | 630     | 193      | 526      | 840     |
| jenie-spring-helloworld-rest                  | 2 core, 2 Gi | -              | 490    | 1460    | 54       | 154      | 188     |
| jenie-spring-helloworld-rest                  | 2 core, 2 Gi | enabled        | 550    | 1650    | 3.9      | 5        | 28      |
| jenie-spring-helloworld-rest-reactive         | 2 core, 2 Gi | -              | 480    | 1420    | 75       | 229      | 424     |


- [Spring MVC REST API Performance Report](./jenie-spring-test/report/perf/rest/rest.md)
- [Spring Webflux REST API Performance Report](./jenie-spring-test/report/perf/rest/rest-reactive.md)

#### gRPC Performance Test Summary

| app                                           | pod resource   | virtual thread | iter/s | req/s | avg (ms) | p95 (ms) | p99 (ms) |
|:----------------------------------------------|:---------------|:---------------|:-------|:------|:---------|:---------|:-----|
| jenie-spring-helloworld-armeria-grpc          | 1 core, 1 Gi   | -              | 290    | 880   | 140      | 393      | 527  |
| jenie-spring-helloworld-armeria-grpc          | 1 core, 1 Gi   | enabled        | 270    | 800   | 244      | 891      | 1120 |
| jenie-spring-helloworld-armeria-grpc-reactive | 1 core, 1 Gi   | -              | 290    | 860   | 255      | 698      | 883  |
| jenie-spring-helloworld-grpc                  | 1 core, 1 Gi   | -              | 290    | 850   | 236      | 515      | 799  |
| jenie-spring-helloworld-grpc                  | 1 core, 1 Gi   | enabled        | 290    | 850   | 358      | 757      | 884  |
| jenie-spring-helloworld-grpc-reactive         | 1 core, 1 Gi   | -              | 280    | 840   | 108      | 291      | 402  |
| jenie-spring-helloworld-armeria-grpc          | 2 core, 2 Gi   | -              | 590    | 1780  | 100      | 209      | 286  |
| jenie-spring-helloworld-armeria-grpc          | 2 core, 2 Gi   | enabled        | 630    | 1910  | 181      | 405      | 521  |
| jenie-spring-helloworld-armeria-grpc-reactive | 2 core, 2 Gi   | -              | 520    | 1560  | 27       | 89       | 108  |
| jenie-spring-helloworld-grpc                  | 2 core, 2 Gi   | -              | 670    | 1970  | 150      | 387      | 515  |
| jenie-spring-helloworld-grpc                  | 2 core, 2 Gi   | enabled        | 670    | 1950  | 164      | 397      | 529  |
| jenie-spring-helloworld-grpc-reactive         | 2 core, 2 Gi   | -              | 620    | 1870  | 400      | 813      | 1000 |

- [Armeria gRPC Performance Report](./jenie-spring-test/report/perf/grpc/armeria-grpc.md)
- [Armeria gRPC Reactive Performance Report](./jenie-spring-test/report/perf/grpc/armeria-grpc-reactive.md)
- [Spring gRPC Performance Report](./jenie-spring-test/report/perf/grpc/spring-grpc.md)
- [Spring gRPC Reactive Performance Report](./jenie-spring-test/report/perf/grpc/spring-grpc-reactive.md)


## Coding Style, Formatting
The coding formatting and style settings are stored in the [config](config/README.md).

## License
The Jenie project is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).
