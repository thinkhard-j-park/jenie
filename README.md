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
[jenie-spring-test](jenie-spring-test/README.md) contains a suite of integration tests, benchmark, performance results for modules.

#### Performance Test Summary

The following is a consolidated summary of performance test results, evaluating various server implementations, network protocols, and configuration options. 
The comprehensive details of the testing methodology and environment are documented in the [Performance Test Environment](./jenie-spring-test/report/perf/perf.md) report.


| app                                           | protocol | pod resource | virtual thread | iter/s | req/s | cpu  | memory  |
|:----------------------------------------------|:---------|:-------------|:---------------|:-------|:------|:-----|:--------|
| jenie-spring-helloworld-rest                  | rest     | 1 core, 1 Gi | -              | 250    | 754   | 1    | 900 Mi  |
| jenie-spring-helloworld-rest                  | rest     | 1 core, 1 Gi | enabled      | 280    | 840   | 0.87 | 612 Mi  |
| jenie-spring-helloworld-rest-reactive         | rest     | 1 core, 1 Gi | -            | 210    | 630   | 1   | 530 Mi  |
| jenie-spring-helloworld-armeria-grpc          | grpc     | 1 core, 1 Gi | -            | 290    | 880   | 1   | 467 Mi  |
| jenie-spring-helloworld-armeria-grpc          | grpc     | 1 core, 1 Gi | enabled      | 270    | 800   | 1   | 537 Mi  |
| jenie-spring-helloworld-armeria-grpc-reactive | grpc     | 1 core, 1 Gi | -              | 290    | 860   | 1   | 537 Mi  |
| jenie-spring-helloworld-rest                  | rest     | 2 core, 2 Gi | -            | 490    | 1460  | 2    | 1400 Mi |
| jenie-spring-helloworld-rest                  | rest     | 2 core, 2 Gi | enabled      | 550    | 1650  | 1.71 | 1910 Mi |
| jenie-spring-helloworld-rest-reactive         | rest     | 2 core, 2 Gi | -            | 480    | 1420  | 2   | 1340 Mi |
| jenie-spring-helloworld-armeria-grpc          | grpc     | 2 core, 2 Gi | -            | 590    | 1780  | 2   | 1340 Mi |
| jenie-spring-helloworld-armeria-grpc          | grpc     | 2 core, 2 Gi | enabled      | 630    | 1910  | 2   | 1360 Mi |
| jenie-spring-helloworld-armeria-grpc-reactive | grpc     | 2 core, 2 Gi | -            | 560    | 1690  | 2   | 1360 Mi |

- [Spring MVC REST API Performance Report](./jenie-spring-test/report/perf/rest/rest.md)
- [Spring Webflux REST API Performance Report](./jenie-spring-test/report/perf/rest/rest-reactive.md)
- [Armeria gRPC Performance Report](./jenie-spring-test/report/perf/grpc/armeria-grpc.md)
- [Armeria gRPC Reactive Performance Report](./jenie-spring-test/report/perf/grpc/armeria-grpc-reactive.md)

## Coding Style, Formatting
The coding formatting and style settings are stored in the [config](config/README.md).

## License
The Jenie project is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).
