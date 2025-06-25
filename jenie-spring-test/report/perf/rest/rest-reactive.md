# Performance test result

- For the test environment, refer to [this document](../perf.md).
- Measure the performance of the jenie-spring-helloworld-rest-reactive server in k8s environment.
- Used [CaffeineMongoTemplateRouter](../../../../jenie-spring-data-mongodb/src/main/java/org/jenie/spring/data/mongodb/operation/CaffeineMongoTemplateRouter.java)

## Thresholds

- error rate < 0.01
- p(95) < 1000 ms

## Summary

- This analysis compares a Spring WebFlux application on a 1-core versus a 2-core setup, focusing on Reactor Netty thread tuning. 
- Scaling from 1 to 2 cores and increasing the ioWorkerCount from 5 to 20 more than doubled the throughput from 210 to 480 iter/sec. 
- This configuration change also dramatically improved latency, slashing the p(95) response time by over 56% from 526ms to 229ms. 
- The significant increase in worker threads was key to better handling the I/O-bound workload by enabling greater parallelism. 
- The results highlight that for reactive applications, tuning the ioWorkerCount is critical to fully leverage hardware resources and achieve optimal performance.

| pod resource | virtual thread | iter/s | tps  | cpu | memory  |
|:-------------|:---------------|:-------|:-----|:----|:--------|
| 1 core, 1 Gi | -              | 210    | 630  | 1   | 530 Mi  |
| 2 core, 2 Gi | -              | 480    | 1420 | 2   | 1.34 Gi |

## Webflux with virtual thread
- Although not included in the test report, the results showed that enabling virtual threads in WebFlux actually led to performance degradation.
- This is likely because the improper combination of WebFlux's non-blocking model and virtual threads created overhead, or because thread pinning caused by constructs like `synchronized` blocks prevented the advantages of virtual threads from being fully utilized.


## 1 core, 1 Gi, -Xms700M -Xmx700M, -Dreactor.netty.ioSelectCount=2 -Dreactor.netty.ioWorkerCount=5
```

         /\      Grafana   /‾‾/
    /\  /  \     |\  __   /  /
   /  \/    \    | |/ /  /   ‾‾\
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/

     execution: local
        script: perf-stress-rest-reactive.js
        output: Prometheus remote write (http://192.168.0.14:9090/api/v1/write)

     scenarios: (100.00%) 1 scenario, 2000 max VUs, 15m30s max duration (incl. graceful stop):
              * rampingStress: Up to 210.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-2000, gracefulStop: 30s)


     ✓ status

     checks.........................: 100.00% 472053 out of 472053
     custom_http_req_duration.......: avg=193.046607 min=1.130817 med=167.179344 max=1668.294567 p(90)=392.597907 p(95)=526.760937 p(99)=840.853005 count=472053
     data_received..................: 3.1 GB  3.5 MB/s
     data_sent......................: 63 MB   70 kB/s
     dropped_iterations.............: 149     0.165531/s
     http_req_blocked...............: avg=10.08µs    min=501ns    med=8.01µs     max=10.14ms     p(90)=14.52µs    p(95)=16.82µs    p(99)=30.25µs    count=472053
     http_req_connecting............: avg=864ns      min=0s       med=0s         max=10.05ms     p(90)=0s         p(95)=0s         p(99)=0s         count=472053
   ✓ http_req_duration..............: avg=193.04ms   min=1.13ms   med=167.17ms   max=1.66s       p(90)=392.59ms   p(95)=526.76ms   p(99)=840.85ms   count=472053
       { expected_response:true }...: avg=193.04ms   min=1.13ms   med=167.17ms   max=1.66s       p(90)=392.59ms   p(95)=526.76ms   p(99)=840.85ms   count=472053
   ✓ http_req_failed................: 0.00%   0 out of 472053
     http_req_receiving.............: avg=157.9µs    min=4.05µs   med=79.23µs    max=94.57ms     p(90)=359.7µs    p(95)=619.53µs   p(99)=1.24ms     count=472053
     http_req_sending...............: avg=23.31µs    min=1.84µs   med=20.95µs    max=16.17ms     p(90)=37.51µs    p(95)=42.33µs    p(99)=62.62µs    count=472053
     http_req_tls_handshaking.......: avg=0s         min=0s       med=0s         max=0s          p(90)=0s         p(95)=0s         p(99)=0s         count=472053
     http_req_waiting...............: avg=192.86ms   min=1.11ms   med=167ms      max=1.66s       p(90)=392.36ms   p(95)=526.54ms   p(99)=840.73ms   count=472053
     http_reqs......................: 472053  524.425147/s
     iteration_duration.............: avg=246.99ms   min=1.99ms   med=234.27ms   max=1.66s       p(90)=469.37ms   p(95)=598.55ms   p(99)=907.03ms   count=157351
     iterations.....................: 157351  174.808382/s
     rest_reqs......................: 472053  524.425147/s
     rest_success_reqs..............: 472053  524.425147/s
     vus............................: 55      min=0                max=235
     vus_max........................: 249     min=100              max=249


running (15m00.1s), 0000/0249 VUs, 157351 complete and 0 interrupted iterations
rampingStress ✓ [======================================] 0000/0249 VUs  15m0s  210.00 iters/s
```

## 2 core, 2 Gi, -Xms1700M -Xmx1700M, -Dreactor.netty.ioSelectCount=5 -Dreactor.netty.ioWorkerCount=20
```

         /\      Grafana   /‾‾/
    /\  /  \     |\  __   /  /
   /  \/    \    | |/ /  /   ‾‾\
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/

     execution: local
        script: perf-stress-rest-reactive.js
        output: Prometheus remote write (http://192.168.0.14:9090/api/v1/write)

     scenarios: (100.00%) 1 scenario, 2000 max VUs, 15m30s max duration (incl. graceful stop):
              * rampingStress: Up to 480.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-2000, gracefulStop: 30s)


     ✓ status

     checks.........................: 100.00% 1079361 out of 1079361
     custom_http_req_duration.......: avg=75.920424 min=0.984541 med=60.652342 max=1045.104657 p(90)=154.843779 p(95)=229.305639 p(99)=424.192324 count=1079361
     data_received..................: 7.2 GB  7.9 MB/s
     data_sent......................: 145 MB  161 kB/s
     dropped_iterations.............: 212     0.235542/s
     http_req_blocked...............: avg=8.75µs    min=465ns    med=6.73µs    max=9.1ms       p(90)=14.13µs    p(95)=16.35µs    p(99)=30.34µs    count=1079361
     http_req_connecting............: avg=417ns     min=0s       med=0s        max=8.98ms      p(90)=0s         p(95)=0s         p(99)=0s         count=1079361
   ✓ http_req_duration..............: avg=75.92ms   min=984.54µs med=60.65ms   max=1.04s       p(90)=154.84ms   p(95)=229.3ms    p(99)=424.19ms   count=1079361
       { expected_response:true }...: avg=75.92ms   min=984.54µs med=60.65ms   max=1.04s       p(90)=154.84ms   p(95)=229.3ms    p(99)=424.19ms   count=1079361
   ✓ http_req_failed................: 0.00%   0 out of 1079361
     http_req_receiving.............: avg=143.82µs  min=3.94µs   med=77.31µs   max=18.78ms     p(90)=347.71µs   p(95)=564.12µs   p(99)=914.72µs   count=1079361
     http_req_sending...............: avg=21.22µs   min=1.78µs   med=18.07µs   max=26.37ms     p(90)=36.12µs    p(95)=40.71µs    p(99)=64.02µs    count=1079361
     http_req_tls_handshaking.......: avg=0s        min=0s       med=0s        max=0s          p(90)=0s         p(95)=0s         p(99)=0s         count=1079361
     http_req_waiting...............: avg=75.75ms   min=933.5µs  med=60.49ms   max=1.04s       p(90)=154.63ms   p(95)=229.12ms   p(99)=423.97ms   count=1079361
     http_reqs......................: 1079361 1199.221877/s
     iteration_duration.............: avg=92.66ms   min=1.56ms   med=78.83ms   max=1.04s       p(90)=175.47ms   p(95)=254.62ms   p(99)=453.23ms   count=359787
     iterations.....................: 359787  399.740626/s
     rest_reqs......................: 1079361 1199.221877/s
     rest_success_reqs..............: 1079361 1199.221877/s
     vus............................: 29      min=0                  max=268
     vus_max........................: 303     min=100                max=303


running (15m00.1s), 0000/0303 VUs, 359787 complete and 0 interrupted iterations
rampingStress ✓ [======================================] 0000/0303 VUs  15m0s  480.00 iters/s
```