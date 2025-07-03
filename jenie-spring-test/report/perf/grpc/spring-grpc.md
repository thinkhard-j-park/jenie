# Performance test result - jenie-spring-helloworld-grpc

- For the test environment, refer to [this document](../perf.md).
- Measure the performance of the jenie-spring-helloworld-grpc server in k8s environment.
- Used [CaffeineMongoTemplateRouter](../../../../jenie-spring-data-mongodb/src/main/java/org/jenie/spring/data/mongodb/operation/CaffeineMongoTemplateRouter.java)

## Thresholds

- error rate < 0
- p(95) < 1000 ms

## Summary

- The jenie-spring-helloworld-grpc server showed no throughput improvement with virtual threads because it was CPU-bound, not I/O-bound. 
- The CPU-intensive nature of gRPC's Protobuf serialization saturated the available cores, negating any benefits from non-blocking I/O. 
- Consequently, enabling virtual threads only resulted in slightly increased latency and memory usage due to overhead.

| pod resource | virtual thread | iter/s | req/s | avg    | p95    | p99      | cpu | memory  |
|:-------------|:---------------|:-------|:------|:-------|:-------|:---------|:----|:--------|
| 1 core, 1 Gi | -              | 290    | 850   | 236 ms | 515 ms | 700 ms   | 1   | 440 Mi  |
| 1 core, 1 Gi | enabled        | 290    | 850   | 358 ms | 757 ms | 884 ms   | 1   | 458 Mi  |
| 2 core, 2 Gi | -              | 670    | 1970  | 150 ms | 387 ms | 515 ms   | 2   | 1370 Mi |
| 2 core, 2 Gi | enabled        | 670    | 1950  | 164 ms | 397 ms | 529 ms   | 2   | 1400 Mi |

## 1 core, 1 Gi, -Xms700M -Xmx700M
```
         /\      Grafana   /‾‾/
    /\  /  \     |\  __   /  /
   /  \/    \    | |/ /  /   ‾‾\
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/

     execution: local
        script: perf-stress-grpc.js
        output: Prometheus remote write (http://192.168.0.14:9090/api/v1/write)

     scenarios: (100.00%) 1 scenario, 5000 max VUs, 15m30s max duration (incl. graceful stop):
              * rampingStress: Up to 290.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-5000, gracefulStop: 30s)


     ✓ status is OK

   ✓ checks...............: 100.00% 648984 out of 648984
     data_received........: 2.3 GB  2.6 MB/s
     data_sent............: 119 MB  133 kB/s
     dropped_iterations...: 1171    1.300115/s
   ✓ grpc_req_duration....: avg=236.03ms min=752.38µs med=213.32ms max=1.4s  p(90)=407.37ms p(95)=515.14ms p(99)=700.98ms count=648984
     grpc_reqs............: 648984  720.541226/s
     grpc_success_reqs....: 648984  720.541226/s
     iteration_duration...: avg=800.62ms min=4ms      med=821.82ms max=2.81s p(90)=1.33s    p(95)=1.78s    p(99)=2.28s    count=216328
     iterations...........: 216328  240.180409/s
     vus..................: 281     min=0                max=673
     vus_max..............: 700     min=100              max=700


running (15m00.7s), 0000/0700 VUs, 216328 complete and 0 interrupted iterations
rampingStress ✓ [======================================] 0000/0700 VUs  15m0s  290.00 iters/s
```

## 1 core, 1 Gi, -Xms700M -Xmx700M, virtual thread
```
         /\      Grafana   /‾‾/
    /\  /  \     |\  __   /  /
   /  \/    \    | |/ /  /   ‾‾\
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/

     execution: local
        script: perf-stress-grpc.js
        output: Prometheus remote write (http://192.168.0.14:9090/api/v1/write)

     scenarios: (100.00%) 1 scenario, 5000 max VUs, 15m30s max duration (incl. graceful stop):
              * rampingStress: Up to 290.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-5000, gracefulStop: 30s)


     ✓ status is OK

   ✓ checks...............: 100.00% 648201 out of 648201
     data_received........: 2.3 GB  2.6 MB/s
     data_sent............: 119 MB  132 kB/s
     dropped_iterations...: 1432    1.589638/s
   ✓ grpc_req_duration....: avg=358.51ms min=680.85µs med=370ms max=1.5s p(90)=689.32ms p(95)=757.19ms p(99)=884.01ms count=648201
     grpc_reqs............: 648201  719.556611/s
     grpc_success_reqs....: 648201  719.556611/s
     iteration_duration...: avg=1.22s    min=3.45ms   med=1.19s max=3.5s p(90)=2.3s     p(95)=2.49s    p(99)=2.75s    count=216067
     iterations...........: 216067  239.852204/s
     vus..................: 412     min=0                max=799
     vus_max..............: 841     min=100              max=841


running (15m00.8s), 0000/0841 VUs, 216067 complete and 0 interrupted iterations
rampingStress ✓ [======================================] 0000/0841 VUs  15m0s  290.00 iters/s
```

## 2 core, 2 Gi, -Xms1700M -Xmx1700M 
```
        /\      Grafana   /‾‾/
    /\  /  \     |\  __   /  /
   /  \/    \    | |/ /  /   ‾‾\
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/

     execution: local
        script: perf-stress-grpc.js
        output: Prometheus remote write (http://192.168.0.14:9090/api/v1/write)

     scenarios: (100.00%) 1 scenario, 5000 max VUs, 15m30s max duration (incl. graceful stop):
              * rampingStress: Up to 670.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-5000, gracefulStop: 30s)


     ✓ status is OK

   ✓ checks...............: 100.00% 1498899 out of 1498899
     data_received........: 5.4 GB  6.0 MB/s
     data_sent............: 276 MB  306 kB/s
     dropped_iterations...: 2867    3.184043/s
   ✓ grpc_req_duration....: avg=150.08ms min=678.85µs med=115.63ms max=1.36s p(90)=305.62ms p(95)=387.01ms p(99)=515.67ms count=1498899
     grpc_reqs............: 1498899 1664.652804/s
     grpc_success_reqs....: 1498899 1664.652804/s
     iteration_duration...: avg=514.64ms min=3.8ms    med=458.14ms max=2.72s p(90)=1s       p(95)=1.19s    p(99)=1.64s    count=499633
     iterations...........: 499633  554.884268/s
     vus..................: 416     min=0                  max=890
     vus_max..............: 916     min=100                max=916


running (15m00.4s), 0000/0916 VUs, 499633 complete and 0 interrupted iterations
rampingStress ✓ [======================================] 0000/0916 VUs  15m0s  670.00 iters/s
```

## 2 core, 2 Gi, -Xms1700M -Xmx1700M, virtual thread

```

         /\      Grafana   /‾‾/
    /\  /  \     |\  __   /  /
   /  \/    \    | |/ /  /   ‾‾\
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/

     execution: local
        script: perf-stress-grpc.js
        output: Prometheus remote write (http://192.168.0.14:9090/api/v1/write)

     scenarios: (100.00%) 1 scenario, 5000 max VUs, 15m30s max duration (incl. graceful stop):
              * rampingStress: Up to 670.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-5000, gracefulStop: 30s)


     ✓ status is OK

   ✓ checks...............: 100.00% 1497933 out of 1497933
     data_received........: 5.4 GB  6.0 MB/s
     data_sent............: 276 MB  306 kB/s
     dropped_iterations...: 3188    3.540765/s
   ✓ grpc_req_duration....: avg=164.72ms min=721.24µs med=138.48ms max=1.19s p(90)=314.64ms p(95)=397.97ms p(99)=529.89ms count=1497933
     grpc_reqs............: 1497933 1663.685439/s
     grpc_success_reqs....: 1497933 1663.685439/s
     iteration_duration...: avg=561.67ms min=4.12ms   med=527.99ms max=2.91s p(90)=1.03s    p(95)=1.25s    p(99)=1.66s    count=499311
     iterations...........: 499311  554.561813/s
     vus..................: 312     min=0                  max=925
     vus_max..............: 973     min=100                max=973


running (15m00.4s), 0000/0973 VUs, 499311 complete and 0 interrupted iterations
rampingStress ✓ [======================================] 0000/0973 VUs  15m0s  670.00 iters/s
```