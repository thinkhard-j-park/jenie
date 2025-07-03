# Performance test result - jenie-spring-helloworld-grpc-reactive

- For the test environment, refer to [this document](../perf.md).
- Measure the performance of the jenie-spring-helloworld-grpc-reactive server in k8s environment.
- Used [ReactiveCaffeineMongoTemplateRouter](../../../../jenie-spring-data-mongodb/src/main/java/org/jenie/spring/data/mongodb/operation/ReactiveCaffeineMongoTemplateRouter.java)

## Thresholds

- error rate < 0
- p(95) < 1000 ms

## Summary

- Doubling the resources from 1 to 2 cores more than doubled the throughput (iter/s and req/s), but response times (avg, p95) also increased significantly.
- The non-linear performance gain is because the 1-core environment was a severe bottleneck; adding a core greatly reduced CPU contention and maximized the efficiency of the reactive, non-blocking architecture.
- The higher latency was a direct trade-off for this massive throughput gain, as the system handled a much larger concurrent load, leading to increased queuing and processing times per request.

| pod resource | virtual thread | iter/s | req/s | avg    | p95    | p99     | cpu | memory  |
|:-------------|:---------------|:-------|:------|:-------|:-------|:--------|:----|:--------|
| 1 core, 1 Gi | -              | 280    | 840   | 108 ms | 291 ms | 402 ms  | 1   | 442 Mi  |
| 2 core, 2 Gi | -              | 620    | 1870  | 400 ms | 813 ms | 1000 ms | 2   | 1380 Mi |

## 1 core, 1 Gi, -Xms700M -Xmx700M
```
        /\      Grafana   /‾‾/
    /\  /  \     |\  __   /  /
   /  \/    \    | |/ /  /   ‾‾\
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/

     execution: local
        script: perf-stress-grpc-reactive.js
        output: Prometheus remote write (http://192.168.0.14:9090/api/v1/write)

     scenarios: (100.00%) 1 scenario, 5000 max VUs, 15m30s max duration (incl. graceful stop):
              * rampingStress: Up to 280.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-5000, gracefulStop: 30s)


     ✓ status is OK

   ✓ checks...............: 100.00% 627978 out of 627978
     data_received........: 2.3 GB  2.5 MB/s
     data_sent............: 116 MB  128 kB/s
     dropped_iterations...: 674     0.748533/s
   ✓ grpc_req_duration....: avg=108.2ms  min=868.95µs med=99.57ms  max=606.34ms p(90)=209.18ms p(95)=291.84ms p(99)=402.03ms count=627978
     grpc_reqs............: 627978  697.421565/s
     grpc_success_reqs....: 627978  697.421565/s
     iteration_duration...: avg=369.48ms min=4.18ms   med=346.65ms max=1.84s    p(90)=716.88ms p(95)=823.15ms p(99)=1.29s    count=209326
     iterations...........: 209326  232.473855/s
     vus..................: 150     min=0                max=440
     vus_max..............: 457     min=100              max=457


running (15m00.4s), 0000/0457 VUs, 209326 complete and 0 interrupted iterations
```


## 2 core, 2 Gi, -Xms1700M -Xmx1700M 
```
         /\      Grafana   /‾‾/
    /\  /  \     |\  __   /  /
   /  \/    \    | |/ /  /   ‾‾\
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/

     execution: local
        script: perf-stress-grpc-reactive.js
        output: Prometheus remote write (http://192.168.0.14:9090/api/v1/write)

     scenarios: (100.00%) 1 scenario, 5000 max VUs, 15m30s max duration (incl. graceful stop):
              * rampingStress: Up to 620.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-5000, gracefulStop: 30s)


     ✓ status is OK

   ✓ checks...............: 100.00% 1380042 out of 1380042
     data_received........: 5.0 GB  5.5 MB/s
     data_sent............: 254 MB  282 kB/s
     dropped_iterations...: 4986    5.532178/s
   ✓ grpc_req_duration....: avg=400.79ms min=714.66µs med=409.78ms max=1.64s p(90)=725.18ms p(95)=813.37ms p(99)=1s   count=1380042
     grpc_reqs............: 1380042 1531.214988/s
     grpc_success_reqs....: 1380042 1531.214988/s
     iteration_duration...: avg=1.35s    min=3.57ms   med=1.42s    max=4.25s p(90)=2.43s    p(95)=2.69s    p(99)=3.2s count=460014
     iterations...........: 460014  510.404996/s
     vus..................: 308     min=0                  max=1566
     vus_max..............: 1618    min=100                max=1618


running (15m01.3s), 0000/1618 VUs, 460014 complete and 0 interrupted iterations
rampingStress ✓ [======================================] 0000/1618 VUs  15m0s  620.00 iters/s
```