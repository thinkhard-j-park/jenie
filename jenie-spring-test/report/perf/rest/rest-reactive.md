# Performance test result

- For the test environment, refer to [this document](../perf.md).
- Measure the performance of the jenie-spring-helloworld-rest server in k8s envrionment.
- Used [CaffeineMongoTemplateRouter](../../../../jenie-spring-data-mongodb/src/main/java/org/jenie/spring/data/mongodb/operation/CaffeineMongoTemplateRouter.java)

## Thresholds

- error rate < 0.01
- p(95) < 1000 ms

## Summary
- TBD


| pod resource | virtual thread | iter/s | tps | cpu | memory |
|:-------------|:---------------|:-------|:----|:----|:-------|
| 1 core, 1 Gi | -              | 200    | 600 | 1   | 478 Mi |
| 1 core, 1 Gi | enabled        |        |     |     | Mi     |
| 2 core, 2 Gi | -              |        |     |     | Mi     |
| 2 core, 2 Gi | enabled        |        |     |     | Mi     |

## 1 core, 1 Gi, -Xms700M -Xmx700M
```
```

## 1 core, 1 Gi, -Xms700M -Xmx700M, Virtual Thread
```
```

## 2 core, 2 Gi, -Xms1700M -Xmx1700M
```
```

## 2 core, 2 Gi, -Xms1700M -Xmx1700M, Virtual Thread
```

```