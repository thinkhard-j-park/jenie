# Performance test result - jenie-spring-helloworld-armeria-grpc

- For the test environment, refer to [this document](../perf.md).
- Measure the performance of the jenie-spring-helloworld-armeria-grpc server in k8s environment.
- Used [CaffeineMongoTemplateRouter](../../../../jenie-spring-data-mongodb/src/main/java/org/jenie/spring/data/mongodb/operation/CaffeineMongoTemplateRouter.java)

## Thresholds

- error rate < 0
- p(95) < 1000 ms

## Summary

- The performance analysis of the armeria-grpc server reveals that the effectiveness of virtual threads is highly dependent on available CPU resources.
- In a 2-core pod, enabling virtual threads significantly boosted throughput from 590 to 630 iter/s, demonstrating a clear benefit. 
- However, in a 1-core environment, enabling virtual threads for the gRPC server did not increase throughput, slightly reducing it instead. This occurs because gRPC's Protobuf serialization is CPU-intensive, making the application CPU-bound under tight resource constraints. Virtual threads primarily optimize I/O-bound workloads, so they offer no advantage when the main performance bottleneck is CPU computation.

| pod resource | virtual thread | iter/s | req/s | avg    | p95    | p99    | cpu | memory  |
|:-------------|:---------------|:-------|:------|:-------|:-------|:-------|:----|:--------|
| 1 core, 1 Gi | -              | 290    | 880   | 140 ms | 393 ms | 527 ms | 1   | 467 Mi  |
| 1 core, 1 Gi | enabled        | 270    | 800   | 244 ms | 891 ms | 1120 ms | 1   | 537 Mi  |
| 2 core, 2 Gi | -              | 590    | 1780  | 100 ms | 209 ms | 286 ms | 2   | 1340 Mi |
| 2 core, 2 Gi | enabled        | 630    | 1910  | 181 ms | 405 ms | 521 ms | 2   | 1360 Mi |

## 1 core, 1 Gi, -Xms700M -Xmx700M, -Dcom.linecorp.armeria.numCommonWorkers=2 -Dcom.linecorp.armeria.numCommonBlockingTaskThreads=10
```
         /\      Grafana   /‾‾/
    /\  /  \     |\  __   /  /
   /  \/    \    | |/ /  /   ‾‾\
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/

     execution: local
        script: perf-stress-grpc-armeria.js
        output: Prometheus remote write (http://192.168.0.14:9090/api/v1/write)

     scenarios: (100.00%) 1 scenario, 5000 max VUs, 15m30s max duration (incl. graceful stop):
              * rampingStress: Up to 290.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-5000, gracefulStop: 30s)


     ✓ status is OK

   ✓ checks...............: 100.00% 650721 out of 650721
     data_received........: 2.4 GB  2.6 MB/s
     data_sent............: 116 MB  129 kB/s
     dropped_iterations...: 592     0.657637/s
   ✓ grpc_req_duration....: avg=140.86ms min=824.18µs med=103.34ms max=1.19s p(90)=306.93ms p(95)=393.77ms p(99)=527.81ms count=650721
     grpc_reqs............: 650721  722.868555/s
     grpc_success_reqs....: 650721  722.868555/s
     iteration_duration...: avg=470.98ms min=4.59ms   med=377.17ms max=2.78s p(90)=1.02s    p(95)=1.18s    p(99)=1.78s    count=216907
     iterations...........: 216907  240.956185/s
     vus..................: 62      min=0                max=385
     vus_max..............: 407     min=100              max=407


running (15m00.2s), 0000/0407 VUs, 216907 complete and 0 interrupted iterations
rampingStress ✓ [======================================] 0000/0407 VUs  15m0s  290.00 iters/s
```

## 1 core, 1 Gi, -Xms700M -Xmx700M, virtual thread
```
         /\      Grafana   /‾‾/
    /\  /  \     |\  __   /  /
   /  \/    \    | |/ /  /   ‾‾\
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/

     execution: local
        script: perf-stress-grpc-armeria.js
        output: Prometheus remote write (http://192.168.0.14:9090/api/v1/write)

     scenarios: (100.00%) 1 scenario, 5000 max VUs, 15m30s max duration (incl. graceful stop):
              * rampingStress: Up to 270.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-5000, gracefulStop: 30s)


     ✓ status is OK

   ✓ checks...............: 100.00% 604053 out of 604053
     data_received........: 2.2 GB  2.4 MB/s
     data_sent............: 108 MB  120 kB/s
     dropped_iterations...: 1149    1.274313/s
   ✓ grpc_req_duration....: avg=244.15ms min=967.85µs med=187.39ms max=1.61s p(90)=609.26ms p(95)=891.13ms p(99)=1.12s count=604053
     grpc_reqs............: 604053  669.932796/s
     grpc_success_reqs....: 604053  669.932796/s
     iteration_duration...: avg=871.29ms min=4.77ms   med=573.81ms max=4.81s p(90)=2.22s    p(95)=3.07s    p(99)=3.99s count=201351
     iterations...........: 201351  223.310932/s
     vus..................: 305     min=0                max=730
     vus_max..............: 748     min=100              max=748


running (15m01.7s), 0000/0748 VUs, 201351 complete and 0 interrupted iterations
rampingStress ✓ [======================================] 0000/0748 VUs  15m0s  270.00 iters/s
```

## 2 core, 2 Gi, -Xms1700M -Xmx1700M -Dcom.linecorp.armeria.numCommonWorkers=4 -Dcom.linecorp.armeria.numCommonBlockingTaskThreads=20
```

         /\      Grafana   /‾‾/
    /\  /  \     |\  __   /  /
   /  \/    \    | |/ /  /   ‾‾\
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/

     execution: local
        script: perf-stress-grpc-armeria.js
        output: Prometheus remote write (http://192.168.0.14:9090/api/v1/write)

     scenarios: (100.00%) 1 scenario, 5000 max VUs, 15m30s max duration (incl. graceful stop):
              * rampingStress: Up to 590.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-5000, gracefulStop: 30s)


     ✓ status is OK

   ✓ checks...............: 100.00% 1323936 out of 1323936
     data_received........: 4.8 GB  5.4 MB/s
     data_sent............: 236 MB  262 kB/s
     dropped_iterations...: 1188    1.319377/s
   ✓ grpc_req_duration....: avg=100.68ms min=782.31µs med=100.71ms max=570.49ms p(90)=198.93ms p(95)=209.05ms p(99)=286.01ms count=1323936
     grpc_reqs............: 1323936 1470.346228/s
     grpc_success_reqs....: 1323936 1470.346228/s
     iteration_duration...: avg=344.09ms min=3.74ms   med=345.23ms max=1.49s    p(90)=619.14ms p(95)=688.45ms p(99)=795.42ms count=441312
     iterations...........: 441312  490.115409/s
     vus..................: 336     min=0                  max=465
     vus_max..............: 495     min=100                max=495


running (15m00.4s), 0000/0495 VUs, 441312 complete and 0 interrupted iterations
rampingStress ✓ [======================================] 0000/0495 VUs  15m0s  590.00 iters/s
```

## 2 core, 2 Gi, -Xms1700M -Xmx1700M, virtual thread
```
         /\      Grafana   /‾‾/
    /\  /  \     |\  __   /  /
   /  \/    \    | |/ /  /   ‾‾\
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/

     execution: local
        script: perf-stress-grpc-armeria.js
        output: Prometheus remote write (http://192.168.0.14:9090/api/v1/write)

     scenarios: (100.00%) 1 scenario, 5000 max VUs, 15m30s max duration (incl. graceful stop):
              * rampingStress: Up to 630.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-5000, gracefulStop: 30s)


     ✓ status is OK

   ✓ checks...............: 100.00% 1410057 out of 1410057
     data_received........: 5.1 GB  5.7 MB/s
     data_sent............: 252 MB  279 kB/s
     dropped_iterations...: 2480    2.753381/s
   ✓ grpc_req_duration....: avg=181.92ms min=713.29µs med=188.43ms max=990.44ms p(90)=331.23ms p(95)=405.24ms p(99)=521.02ms count=1410057
     grpc_reqs............: 1410057 1565.493399/s
     grpc_success_reqs....: 1410057 1565.493399/s
     iteration_duration...: avg=646.27ms min=4.33ms   med=639.75ms max=2.61s    p(90)=1.14s    p(95)=1.34s    p(99)=1.74s    count=470019
     iterations...........: 470019  521.831133/s
     vus..................: 630     min=0                  max=837
     vus_max..............: 859     min=100                max=859


running (15m00.7s), 0000/0859 VUs, 470019 complete and 0 interrupted iterations
rampingStress ✓ [======================================] 0000/0859 VUs  15m0s  630.00 iters/s
```