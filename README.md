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


| app                                           | pod resource | virtual thread | iter/s | req/s   | avg     | p95 | p99      |
|:----------------------------------------------|:-------------|:---------------|:-------|:--------|:--------|:----|:---------|
| jenie-spring-helloworld-rest                  | 1 core, 1 Gi | -              | 250    | 754     | 109 ms  | 250 ms | 353 ms   |
| jenie-spring-helloworld-rest                  | 1 core, 1 Gi | enabled        | 280    | 840     | 5 ms    | 6 ms   | 61 ms    |
| jenie-spring-helloworld-rest-reactive         | 1 core, 1 Gi | -              | 210    | 630     | 193 ms  | 526 ms | 840 ms   |
| jenie-spring-helloworld-rest                  | 2 core, 2 Gi | -              | 490    | 1460    | 54 ms   | 154 ms | 188 ms   |
| jenie-spring-helloworld-rest                  | 2 core, 2 Gi | enabled        | 550    | 1650    | 3.9 ms  | 5 ms   | 28 ms    |
| jenie-spring-helloworld-rest-reactive         | 2 core, 2 Gi | -              | 480    | 1420    | 75 ms   | 229 ms | 424 ms   |


- [Spring MVC REST API Performance Report](./jenie-spring-test/report/perf/rest/rest.md)
- [Spring Webflux REST API Performance Report](./jenie-spring-test/report/perf/rest/rest-reactive.md)

#### gRPC Performance Test Summary

| app                                           | pod resource | virtual thread | iter/s | req/s   | avg     | p95 | p99      |
|:----------------------------------------------|:-------------|:---------------|:-------|:--------|:--------|:----|:---------|
| jenie-spring-helloworld-armeria-grpc          | 1 core, 1 Gi | -              | 290    | 880     | 140 ms  | 393 ms | 527 ms   |
| jenie-spring-helloworld-armeria-grpc          | 1 core, 1 Gi | enabled        | 270    | 800     | 244 ms  | 891 ms | 1120 ms  |
| jenie-spring-helloworld-armeria-grpc-reactive | 1 core, 1 Gi | -              | 290    | 860     | 255 ms  | 698 ms | 883 ms   |
| jenie-spring-helloworld-grpc                  | 1 core, 1 Gi | -              | 290    | 850     | 236 ms | 515 ms | 799 ms   |
| jenie-spring-helloworld-grpc                  | 1 core, 1 Gi | enabled        | 290    | 850     | 358 ms | 757 ms | 884 ms   |
| jenie-spring-helloworld-armeria-grpc          | 2 core, 2 Gi | -              | 590    | 1780    | 100 ms  | 209 ms | 286 ms   |
| jenie-spring-helloworld-armeria-grpc          | 2 core, 2 Gi | enabled        | 630    | 1910    | 181 ms  | 405 ms | 521 ms   |
| jenie-spring-helloworld-armeria-grpc-reactive | 2 core, 2 Gi | -              | 560    | 1690    | 26 ms   | 90 ms  | 109 ms   |
| jenie-spring-helloworld-grpc                  | 2 core, 2 Gi | -              | 670    | 1970  | 150 ms | 387 ms | 515 ms   |
| jenie-spring-helloworld-grpc                  | 2 core, 2 Gi | enabled        | 670    | 1950  | 164 ms | 397 ms | 529 ms   |

- [Armeria gRPC Performance Report](./jenie-spring-test/report/perf/grpc/armeria-grpc.md)
- [Armeria gRPC Reactive Performance Report](./jenie-spring-test/report/perf/grpc/armeria-grpc-reactive.md)
- [Spring gRPC Performance Report](./jenie-spring-test/report/perf/grpc/spring-grpc.md)


## Coding Style, Formatting
The coding formatting and style settings are stored in the [config](config/README.md).

## License
The Jenie project is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).
