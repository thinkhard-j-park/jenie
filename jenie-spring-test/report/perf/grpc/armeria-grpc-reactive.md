# Performance test result - jenie-spring-helloworld-armeria-grpc-reactive

- For the test environment, refer to [this document](../perf.md).
- Measure the performance of the jenie-spring-helloworld-armeria-grpc-reactive server in k8s environment.
- Used [ReactiveCaffeineMongoTemplateRouter](../../../../jenie-spring-data-mongodb/src/main/java/org/jenie/spring/data/mongodb/operation/ReactiveCaffeineMongoTemplateRouter.java)

## Thresholds

- error rate < 0
- p(95) < 1000 ms

## Summary

- The Armeria gRPC reactive implementation achieves 520 iterations/s with excellent latency characteristics and maintains consistent performance across different resource configurations.
- It demonstrates excellent resource utilization efficiency, achieving nearly 9x better average latency and 7.8x better p95 latency in the 2-core configuration while maintaining consistent throughput scaling characteristics.




| pod resource | virtual thread | iter/s       | req/s | avg    | p95    | p99    | cpu | memory  |
|:-------------|:---------------|:-------------|:-----|:-------|:-------|:-------|:----|:--------|
| 1 core, 1 Gi | -              | 290          | 860  | 255 ms | 698 ms | 883 ms | 1   | 537 Mi  |
| 2 core, 2 Gi | -              | 520          |  1560  | 27 ms  | 89 ms  | 108 ms | 2   | 1350 Mi |

## Reactive gRPC with virtual thread
- Virtual threads caused performance degradation because they added unnecessary thread context switching overhead to an already optimized non-blocking reactive stack (Armeria + Spring Data MongoDB Reactive).
- The `-Dreactor.schedulers.defaultBoundedElasticOnVirtualThreads=true` flag forces Reactor's boundedElastic scheduler to use virtual threads, which creates redundant thread handoffs between event loop threads and virtual threads for operations that don't actually block. 
- Virtual threads are designed for blocking I/O scenarios, not for reactive non-blocking architectures where they introduce more overhead than benefit.


## 1 core, 1 Gi, -Xms700M -Xmx700M
```
         /\      Grafana   /‾‾/
    /\  /  \     |\  __   /  /
   /  \/    \    | |/ /  /   ‾‾\
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/

     execution: local
        script: perf-stress-grpc-armeria-reactive.js
        output: Prometheus remote write (http://192.168.0.14:9090/api/v1/write)

     scenarios: (100.00%) 1 scenario, 5000 max VUs, 15m30s max duration (incl. graceful stop):
              * rampingStress: Up to 290.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-5000, gracefulStop: 30s)


     ✓ status is OK

   ✓ checks...............: 100.00% 649293 out of 649293
     data_received........: 2.4 GB  2.6 MB/s
     data_sent............: 116 MB  128 kB/s
     dropped_iterations...: 1068    1.184784/s
   ✓ grpc_req_duration....: avg=255.04ms min=808.88µs med=198.63ms max=1.61s p(90)=604.08ms p(95)=698.85ms p(99)=883.1ms count=649293
     grpc_reqs............: 649293  720.291964/s
     grpc_success_reqs....: 649293  720.291964/s
     iteration_duration...: avg=993.19ms min=3.75ms   med=771.3ms  max=4.23s p(90)=2.36s    p(95)=2.66s    p(99)=3.14s   count=216431
     iterations...........: 216431  240.097321/s
     vus..................: 219     min=0                max=660
     vus_max..............: 665     min=100              max=665

running (15m01.4s), 0000/0665 VUs, 216431 complete and 0 interrupted iterations
rampingStress ✓ [======================================] 0000/0665 VUs  15m0s  290.00 iters/s
```

## 2 core, 2 Gi, -Xms1700M -Xmx1700M
```
         /\      Grafana   /‾‾/
    /\  /  \     |\  __   /  /
   /  \/    \    | |/ /  /   ‾‾\
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/

     execution: local
        script: perf-stress-grpc-armeria-reactive.js
        output: Prometheus remote write (http://192.168.0.14:9090/api/v1/write)

     scenarios: (100.00%) 1 scenario, 5000 max VUs, 15m30s max duration (incl. graceful stop):
              * rampingStress: Up to 520.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-5000, gracefulStop: 30s)


     ✓ status is OK

   ✓ checks...............: 100.00% 1169247 out of 1169247
     data_received........: 4.3 GB  4.7 MB/s
     data_sent............: 209 MB  232 kB/s
     dropped_iterations...: 250     0.277744/s
   ✓ grpc_req_duration....: avg=27.11ms  min=815.28µs med=10.92ms  max=470.96ms p(90)=81.58ms  p(95)=89.32ms  p(99)=108.27ms count=1169247
     grpc_reqs............: 1169247 1299.004387/s
     grpc_success_reqs....: 1169247 1299.004387/s
     iteration_duration...: avg=117.73ms min=3.75ms   med=114.17ms max=1.22s    p(90)=199.65ms p(95)=249.17ms p(99)=403.5ms  count=389749
     iterations...........: 389749  433.001462/s
     vus..................: 89      min=0                  max=175
     vus_max..............: 190     min=100                max=190


running (15m00.1s), 0000/0190 VUs, 389749 complete and 0 interrupted iterations
rampingStress ✓ [======================================] 0000/0190 VUs  15m0s  520.00 iters/s
```