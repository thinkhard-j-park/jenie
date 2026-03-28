# MongoDB Driver Benchmark

> Benchmark results below are based on **JDK 21 (Eclipse Temurin)**.

> **The benchmark reveals a critical performance crossover, showing the synchronous driver becomes decisively more
performant than the reactive driver as concurrency escalates.**

This section compares the performance of the synchronous (`syncDriver`) and `reactiveDriver` under varying loads of
concurrent queries. All scores are in milliseconds (ms).

- One query consists of three MongoDB operations: two `find` operations and one `findAndModify`.

| Concurrent Queries | `reactiveDriver` (ms/op) | `syncDriver` (ms/op) | Winner       |
|--------------------|--------------------------|----------------------|--------------|
| **1**              | 214.727                  | 220.528              | **Reactive** |
| **2**              | 248.380                  | 266.764              | **Reactive** |
| **4**              | 277.806                  | 299.692              | **Reactive** |
| **8**              | 374.752                  | **352.291**          | **Sync**     |
| **16**             | 434.232                  | **402.215**          | **Sync**     |

- **Low Concurrency (1-4 queries):** The `reactiveDriver` demonstrates better performance. It handles a small number of
  concurrent requests more efficiently than the `syncDriver`.
- **High Concurrency (8-16 queries):** As the number of concurrent queries increases, the `syncDriver` becomes more
  performant. At 16 concurrent queries, the `syncDriver` is approximately **7.4% faster** than the `reactiveDriver`.

- See [actual test results](mongo-driver.txt)
  and [test code](../../src/test/java/org/jenie/spring/helloworld/test/jmh/MongoDriverBenchmark.java).
- [A post on the MongoDB community forums](https://www.mongodb.com/community/forums/t/java-reactive-driver-performances/117832)
  also reports similar performance results.




