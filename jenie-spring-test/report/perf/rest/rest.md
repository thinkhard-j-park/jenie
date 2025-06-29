# Performance test result - jenie-spring-helloworld-rest

- For the test environment, refer to [this document](../perf.md).
- Measure the performance of the jenie-spring-helloworld-rest server in k8s environment.
- Used [CaffeineMongoTemplateRouter](../../../../jenie-spring-data-mongodb/src/main/java/org/jenie/spring/data/mongodb/operation/CaffeineMongoTemplateRouter.java)

## Thresholds

- error rate < 0
- p(95) < 1000 ms

## Summary
- Enabling Virtual Threads dramatically reduced p(95) response time by over 92% (e.g., from 250ms to 6.8ms), by efficiently handling I/O-bound operations.
- Throughput increases more moderately, around 12-13%, as performance then becomes bound by CPU and database capacity, etc.
- This disparity arises because once the thread-blocking bottleneck is resolved, overall system performance becomes limited by other factors like CPU capacity or the backend database's throughput.


| pod resource | virtual thread | iter/s | req/s | cpu  | memory  |
|:-------------|:---------------|:-------|:------|:-----|:--------|
| 1 core, 1 Gi | -              | 250    | 754   | 1    | 900 Mi  |
| 1 core, 1 Gi | enabled        | 280    | 840   | 0.87 | 612 Mi  |
| 2 core, 2 Gi | -              | 490    | 1460  | 2    | 1400 Mi |
| 2 core, 2 Gi | enabled        | 550    | 1650  | 1.71 | 1910 Mi |

## 1 core, 1 Gi, -Xms700M -Xmx700M

```
         /\      Grafana   /‾‾/
    /\  /  \     |\  __   /  /
   /  \/    \    | |/ /  /   ‾‾\
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/

     execution: local
        script: perf-stress-rest.js
        output: Prometheus remote write (http://192.168.0.14:9090/api/v1/write)

     scenarios: (100.00%) 1 scenario, 2000 max VUs, 12m30s max duration (incl. graceful stop):
              * rampingStress: Up to 250.00 iterations/s for 12m0s over 2 stages (maxVUs: 100-2000, gracefulStop: 30s)


     ✓ status

     checks.........................: 100.00% 494931 out of 494931
     custom_http_req_duration.......: avg=109.445793 min=1.312448 med=102.345829 max=1552.92333 p(90)=196.368399 p(95)=250.785925 p(99)=353.171659 count=494931
     data_received..................: 3.3 GB  4.6 MB/s
     data_sent......................: 66 MB   92 kB/s
     dropped_iterations.............: 22      0.03055/s
     http_req_blocked...............: avg=14.91µs    min=516ns    med=7.62µs     max=11.24ms    p(90)=14.7µs     p(95)=17.6µs     p(99)=320.18µs   count=494931
     http_req_connecting............: avg=5.05µs     min=0s       med=0s         max=11.18ms    p(90)=0s         p(95)=0s         p(99)=275.86µs   count=494931
   ✓ http_req_duration..............: avg=109.44ms   min=1.31ms   med=102.34ms   max=1.55s      p(90)=196.36ms   p(95)=250.78ms   p(99)=353.17ms   count=494931
       { expected_response:true }...: avg=109.44ms   min=1.31ms   med=102.34ms   max=1.55s      p(90)=196.36ms   p(95)=250.78ms   p(99)=353.17ms   count=494931
   ✓ http_req_failed................: 0.00%   0 out of 494931
     http_req_receiving.............: avg=1.18ms     min=4.67µs   med=114.13µs   max=401.75ms   p(90)=566.57µs   p(95)=879.9µs    p(99)=83.52ms    count=494931
     http_req_sending...............: avg=23.16µs    min=2.01µs   med=20.22µs    max=5.81ms     p(90)=37.4µs     p(95)=42.67µs    p(99)=67.97µs    count=494931
     http_req_tls_handshaking.......: avg=0s         min=0s       med=0s         max=0s         p(90)=0s         p(95)=0s         p(99)=0s         count=494931
     http_req_waiting...............: avg=108.23ms   min=1.21ms   med=100.98ms   max=1.55s      p(90)=195.51ms   p(95)=248.86ms   p(99)=350.96ms   count=494931
     http_reqs......................: 494931  687.271072/s
     iteration_duration.............: avg=160.69ms   min=1.94ms   med=155.94ms   max=1.55s      p(90)=269.11ms   p(95)=296.01ms   p(99)=466.6ms    count=164977
     iterations.....................: 164977  229.090357/s
     rest_reqs......................: 494931  687.271072/s
     rest_success_reqs..............: 494931  687.271072/s
     vus............................: 65      min=0                max=104
     vus_max........................: 121     min=100              max=121
```

## 1 core, 1 Gi, -Xms700M -Xmx700M, Virtual Thread

```

         /\      Grafana   /‾‾/
    /\  /  \     |\  __   /  /
   /  \/    \    | |/ /  /   ‾‾\
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/

     execution: local
        script: perf-stress-rest.js
        output: Prometheus remote write (http://192.168.0.14:9090/api/v1/write)

     scenarios: (100.00%) 1 scenario, 2000 max VUs, 15m30s max duration (incl. graceful stop):
              * rampingStress: Up to 280.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-2000, gracefulStop: 30s)


     ✓ status

     checks.........................: 100.00% 629901 out of 629901
     custom_http_req_duration.......: avg=5.321044 min=1.209116 med=2.915968 max=758.872855 p(90)=5.995382 p(95)=6.827367 p(99)=61.143704 count=629901
     data_received..................: 4.2 GB  4.7 MB/s
     data_sent......................: 84 MB   94 kB/s
     dropped_iterations.............: 32      0.035555/s
     http_req_blocked...............: avg=15.11µs  min=519ns    med=9.37µs   max=10.57ms    p(90)=15.11µs  p(95)=18.32µs  p(99)=288.19µs  count=629901
     http_req_connecting............: avg=4.43µs   min=0s       med=0s       max=10.47ms    p(90)=0s       p(95)=0s       p(99)=259.52µs  count=629901
   ✓ http_req_duration..............: avg=5.32ms   min=1.2ms    med=2.91ms   max=758.87ms   p(90)=5.99ms   p(95)=6.82ms   p(99)=61.14ms   count=629901
       { expected_response:true }...: avg=5.32ms   min=1.2ms    med=2.91ms   max=758.87ms   p(90)=5.99ms   p(95)=6.82ms   p(99)=61.14ms   count=629901
   ✓ http_req_failed................: 0.00%   0 out of 629901
     http_req_receiving.............: avg=172.83µs min=5.04µs   med=110.33µs max=65.97ms    p(90)=368.86µs p(95)=532.87µs p(99)=673.17µs  count=629901
     http_req_sending...............: avg=25.37µs  min=1.93µs   med=25.21µs  max=13.42ms    p(90)=36.38µs  p(95)=41.85µs  p(99)=75.99µs   count=629901
     http_req_tls_handshaking.......: avg=0s       min=0s       med=0s       max=0s         p(90)=0s       p(95)=0s       p(99)=0s        count=629901
     http_req_waiting...............: avg=5.12ms   min=1.16ms   med=2.74ms   max=758.7ms    p(90)=5.72ms   p(95)=6.47ms   p(99)=60.7ms    count=629901
     http_reqs......................: 629901  699.888112/s
     iteration_duration.............: avg=8.34ms   min=2.16ms   med=5.76ms   max=759.65ms   p(90)=8ms      p(95)=8.98ms   p(99)=87.7ms    count=209967
     iterations.....................: 209967  233.296037/s
     rest_reqs......................: 629901  699.888112/s
     rest_success_reqs..............: 629901  699.888112/s
     vus............................: 4       min=0                max=122
     vus_max........................: 132     min=100              max=132
```

## 2 core, 2 Gi, -Xms1700M -Xmx1700M
```

         /\      Grafana   /‾‾/
    /\  /  \     |\  __   /  /
   /  \/    \    | |/ /  /   ‾‾\
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/

     execution: local
        script: perf-stress-rest.js
        output: Prometheus remote write (http://192.168.0.14:9090/api/v1/write)

     scenarios: (100.00%) 1 scenario, 2000 max VUs, 15m30s max duration (incl. graceful stop):
              * rampingStress: Up to 490.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-2000, gracefulStop: 30s)


     ✓ status

     checks.........................: 100.00% 1102395 out of 1102395
     custom_http_req_duration.......: avg=54.608021 min=1.095143 med=47.28575 max=645.35463 p(90)=114.368407 p(95)=154.913736 p(99)=188.550006 count=1102395
     data_received..................: 7.4 GB  8.2 MB/s
     data_sent......................: 148 MB  164 kB/s
     dropped_iterations.............: 33      0.036665/s
     http_req_blocked...............: avg=14.16µs   min=500ns    med=6.68µs   max=7.19ms    p(90)=14.38µs    p(95)=17.35µs    p(99)=308.61µs   count=1102395
     http_req_connecting............: avg=5µs       min=0s       med=0s       max=6.17ms    p(90)=0s         p(95)=0s         p(99)=259.11µs   count=1102395
   ✓ http_req_duration..............: avg=54.6ms    min=1.09ms   med=47.28ms  max=645.35ms  p(90)=114.36ms   p(95)=154.91ms   p(99)=188.55ms   count=1102395
       { expected_response:true }...: avg=54.6ms    min=1.09ms   med=47.28ms  max=645.35ms  p(90)=114.36ms   p(95)=154.91ms   p(99)=188.55ms   count=1102395
   ✓ http_req_failed................: 0.00%   0 out of 1102395
     http_req_receiving.............: avg=593.99µs  min=4.27µs   med=114.37µs max=196.53ms  p(90)=516.85µs   p(95)=775.82µs   p(99)=1.83ms     count=1102395
     http_req_sending...............: avg=21.71µs   min=1.88µs   med=18.24µs  max=28.45ms   p(90)=36.66µs    p(95)=42.41µs    p(99)=71.55µs    count=1102395
     http_req_tls_handshaking.......: avg=0s        min=0s       med=0s       max=0s        p(90)=0s         p(95)=0s         p(99)=0s         count=1102395
     http_req_waiting...............: avg=53.99ms   min=992.04µs med=46.72ms  max=645.03ms  p(90)=112.08ms   p(95)=153.52ms   p(99)=188.03ms   count=1102395
     http_reqs......................: 1102395 1224.831933/s
     iteration_duration.............: avg=76.74ms   min=1.53ms   med=71.02ms  max=646.47ms  p(90)=164.09ms   p(95)=181.31ms   p(99)=225.42ms   count=367465
     iterations.....................: 367465  408.277311/s
     rest_reqs......................: 1102395 1224.831933/s
     rest_success_reqs..............: 1102395 1224.831933/s
     vus............................: 37      min=0                  max=102
     vus_max........................: 131     min=100                max=131
```

## 2 core, 2 Gi, -Xms1700M -Xmx1700M, Virtual Thread
```

         /\      Grafana   /‾‾/
    /\  /  \     |\  __   /  /
   /  \/    \    | |/ /  /   ‾‾\
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/

     execution: local
        script: perf-stress-rest.js
        output: Prometheus remote write (http://192.168.0.14:9090/api/v1/write)

     scenarios: (100.00%) 1 scenario, 2000 max VUs, 15m30s max duration (incl. graceful stop):
              * rampingStress: Up to 550.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-2000, gracefulStop: 30s)


     ✓ status

     checks.........................: 100.00% 1237170 out of 1237170
     custom_http_req_duration.......: avg=3.907236 min=1.022734 med=2.376897 max=889.807199 p(90)=4.851215 p(95)=5.750713 p(99)=28.662004 count=1237170
     data_received..................: 8.3 GB  9.2 MB/s
     data_sent......................: 166 MB  184 kB/s
     dropped_iterations.............: 110     0.122221/s
     http_req_blocked...............: avg=13µs     min=512ns    med=7.12µs   max=15.5ms     p(90)=13.25µs  p(95)=16.28µs  p(99)=254.35µs  count=1237170
     http_req_connecting............: avg=4.24µs   min=0s       med=0s       max=14.9ms     p(90)=0s       p(95)=0s       p(99)=223.91µs  count=1237170
   ✓ http_req_duration..............: avg=3.9ms    min=1.02ms   med=2.37ms   max=889.8ms    p(90)=4.85ms   p(95)=5.75ms   p(99)=28.66ms   count=1237170
       { expected_response:true }...: avg=3.9ms    min=1.02ms   med=2.37ms   max=889.8ms    p(90)=4.85ms   p(95)=5.75ms   p(99)=28.66ms   count=1237170
   ✓ http_req_failed................: 0.00%   0 out of 1237170
     http_req_receiving.............: avg=161.84µs min=4.14µs   med=103.73µs max=58.03ms    p(90)=355.59µs p(95)=519.71µs p(99)=687.82µs  count=1237170
     http_req_sending...............: avg=20.58µs  min=1.84µs   med=18.87µs  max=6.19ms     p(90)=33.4µs   p(95)=39.32µs  p(99)=71.18µs   count=1237170
     http_req_tls_handshaking.......: avg=0s       min=0s       med=0s       max=0s         p(90)=0s       p(95)=0s       p(99)=0s        count=1237170
     http_req_waiting...............: avg=3.72ms   min=962.65µs med=2.21ms   max=889.71ms   p(90)=4.57ms   p(95)=5.4ms    p(99)=28.4ms    count=1237170
     http_reqs......................: 1237170 1374.624893/s
     iteration_duration.............: avg=6.43ms   min=1.51ms   med=4.53ms   max=890.31ms   p(90)=6.83ms   p(95)=8.13ms   p(99)=39.73ms   count=412390
     iterations.....................: 412390  458.208298/s
     rest_reqs......................: 1237170 1374.624893/s
     rest_success_reqs..............: 1237170 1374.624893/s
     vus............................: 3       min=0                  max=175
     vus_max........................: 201     min=100                max=201


running (15m00.0s), 0000/0201 VUs, 412390 complete and 0 interrupted iterations
rampingStress ✓ [======================================] 0000/0201 VUs  15m0s  550.00 iters/s
```