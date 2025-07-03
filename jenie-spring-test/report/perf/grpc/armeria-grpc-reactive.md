# Performance test result - jenie-spring-helloworld-armeria-grpc-reactive

- For the test environment, refer to [this document](../perf.md).
- Measure the performance of the jenie-spring-helloworld-armeria-grpc-reactive server in k8s environment.
- Used [ReactiveCaffeineMongoTemplateRouter](../../../../jenie-spring-data-mongodb/src/main/java/org/jenie/spring/data/mongodb/operation/ReactiveCaffeineMongoTemplateRouter.java)

## Thresholds

- error rate < 0
- p(95) < 1000 ms

## Summary
- The application demonstrated excellent scalability, with throughput nearly doubling when CPU and memory resources were doubled.
- The 1-core environment was clearly CPU-bound, which resulted in high response latency under load.
- Adding a second core resolved this bottleneck, improving the p(95) response time by over 7x (from 699ms to 90ms).


| pod resource | virtual thread | iter/s | req/s | avg    | p95    | p99    | cpu | memory  |
|:-------------|:---------------|:-------|:------|:-------|:-------|:-------|:----|:--------|
| 1 core, 1 Gi | -              | 290    | 860   | 255 ms | 698 ms | 883 ms | 1   | 537 Mi  |
| 2 core, 2 Gi | -              | 560    | 1690  | 26 ms  | 90 ms  | 109 ms | 2   | 1360 Mi |

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
helloworld@thinkhard:~/gitrepo/loops/k6/jenie$ k6 run -o experimental-prometheus-rw perf-stress-grpc-armeria-reactive.js

         /\      Grafana   /‾‾/
    /\  /  \     |\  __   /  /
   /  \/    \    | |/ /  /   ‾‾\
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/

     execution: local
        script: perf-stress-grpc-armeria-reactive.js
        output: Prometheus remote write (http://192.168.0.14:9090/api/v1/write)

     scenarios: (100.00%) 1 scenario, 5000 max VUs, 15m30s max duration (incl. graceful stop):
              * rampingStress: Up to 560.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-5000, gracefulStop: 30s)


     ✓ status is OK

   ✓ checks...............: 100.00% 1257987 out of 1257987
     data_received........: 4.6 GB  5.1 MB/s
     data_sent............: 224 MB  249 kB/s
     dropped_iterations...: 670     0.744355/s
   ✓ grpc_req_duration....: avg=26.4ms   min=758.58µs med=8.95ms   max=1.59s p(90)=83.53ms  p(95)=90.24ms  p(99)=109.2ms  count=1257987
     grpc_reqs............: 1257987 1397.59623/s
     grpc_success_reqs....: 1257987 1397.59623/s
     iteration_duration...: avg=114.75ms min=3.62ms   med=110.18ms max=2.44s p(90)=192.32ms p(95)=229.67ms p(99)=382.47ms count=419329
     iterations...........: 419329  465.86541/s
     vus..................: 55      min=0                  max=300
     vus_max..............: 328     min=100                max=328


running (15m00.1s), 0000/0328 VUs, 419329 complete and 0 interrupted iterations
rampingStress ✓ [======================================] 0000/0328 VUs  15m0s  560.00 iters/s
```