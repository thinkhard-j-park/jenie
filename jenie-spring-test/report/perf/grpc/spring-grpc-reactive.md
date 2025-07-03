# Performance test result - jenie-spring-helloworld-grpc-reactive

- For the test environment, refer to [this document](../perf.md).
- Measure the performance of the jenie-spring-helloworld-grpc-reactive server in k8s environment.
- Used [CaffeineMongoTemplateRouter](../../../../jenie-spring-data-mongodb/src/main/java/org/jenie/spring/data/mongodb/operation/CaffeineMongoTemplateRouter.java)

## Thresholds

- error rate < 0
- p(95) < 1000 ms

## Summary

- TBD
- 
| pod resource | virtual thread | iter/s | req/s | avg  | p95  | p99  | cpu | memory |
|:-------------|:---------------|:-------|:------|:-----|:-----|:-----|:----|:-------|
| 1 core, 1 Gi | -              | -      | -     | - ms | - ms | - ms | 1   | - Mi   |
| 2 core, 2 Gi | -              | -      | -     | - ms | - ms | - ms | 2   | - Mi   |

## 1 core, 1 Gi, -Xms700M -Xmx700M
```
```

## 1 core, 1 Gi, -Xms700M -Xmx700M, virtual thread
```
```

## 2 core, 2 Gi, -Xms1700M -Xmx1700M 
```
```

## 2 core, 2 Gi, -Xms1700M -Xmx1700M, virtual thread

```
```