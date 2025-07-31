
# imperative vs reactive

- 동일한 부하환경에 imperative 와 reactive 의 지표를 비교한다.
- 3 core로 cpu을 더 준다고 하여 딱히 달라지는 것은 없다. reactive가 성능이 더 안 좋다. 이유는 mongoTemplateRouter 때문인가!?
- 2 core, 2 Gi JFR 덤프 떠서 분석해볼 것.


| app                                           | pod resource | virtual thread | iter/s | req/s | avg (ms) | p95 (ms) | p99 (ms) | cpu | memory |
|:----------------------------------------------|:-------------|:---------------|:-------|:------|:---------|:---------|:---------|:----|:-------|
| jenie-spring-helloworld-armeria-grpc          | 2 core, 2 Gi | -              | 520    | 1560  | 14.93    | 79.5     | 92.03    | 2   | 1350   |
| jenie-spring-helloworld-armeria-grpc          | 2 core, 2 Gi | true           | 520    | -     | 14.31    | 78.25    | 90.73    | 2   | 1350   |
| jenie-spring-helloworld-armeria-grpc-reactive | 2 core, 2 Gi | -              | 520    | 1560  | 27.11    | 89.32    | 108.27   | 2   | 1350   |
| jenie-spring-helloworld-armeria-grpc          | 3 core, 3 Gi | -              | 990    | 3000  | 296.9    | 527.06   | 622.82   |     | 1880   |
| jenie-spring-helloworld-armeria-grpc          | 3 core, 3 Gi | true           | -      | -     | -        | -        | -        |     |        |
| jenie-spring-helloworld-armeria-grpc          | 3 core, 3 Gi | -              | 880    | 2620  | 46.43    | 103.98   | 128.62   | 3   | -      |
| jenie-spring-helloworld-armeria-grpc-reactive | 3 core, 3 Gi | -              | 880    | 2620  | 141      | 265      | 305      | 3   | 1910   |
| jenie-spring-helloworld-armeria-grpc          | 3 core, 3 Gi | -              | 800    | 2420  | 13.58    | 59.99    | 75.6     | 3   | 1900   |
| jenie-spring-helloworld-armeria-grpc-reactive | 3 core, 3 Gi | -              | 800    | 2400  | 39.08    | 89.63    | 108.7    | 3   | 1830   |

## imperative, 2 Core, 2 Gi,  -Xms1700M -Xmx1700M
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
              * rampingStress: Up to 520.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-5000, gracefulStop: 30s)


     ✓ status is OK

   ✓ checks...............: 100.00% 1169304 out of 1169304
     data_received........: 4.2 GB  4.7 MB/s
     data_sent............: 209 MB  232 kB/s
     dropped_iterations...: 231     0.256648/s
   ✓ grpc_req_duration....: avg=14.93ms min=740.89µs med=5.66ms  max=1.7s  p(90)=69.21ms  p(95)=79.5ms   p(99)=92.03ms  count=1169304
     grpc_reqs............: 1169304 1299.13337/s
     grpc_success_reqs....: 1169304 1299.13337/s
     iteration_duration...: avg=73.26ms min=4.39ms   med=68.35ms max=1.74s p(90)=140.99ms p(95)=168.18ms p(99)=220.46ms count=389768
     iterations...........: 389768  433.044457/s
     vus..................: 23      min=0                  max=100
     vus_max..............: 183     min=100                max=183


running (15m00.1s), 0000/0183 VUs, 389768 complete and 0 interrupted iterations
rampingStress ✓ [======================================] 0000/0183 VUs  15m0s  520.00 iters/s
```

## imperative, 2 Core, 2 Gi,  -Xms1700M -Xmx1700M, Virtual Thread

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
              * rampingStress: Up to 520.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-5000, gracefulStop: 30s)


     ✓ status is OK

   ✓ checks...............: 100.00% 1169928 out of 1169928
     data_received........: 4.3 GB  4.7 MB/s
     data_sent............: 209 MB  232 kB/s
     dropped_iterations...: 23      0.025555/s
   ✓ grpc_req_duration....: avg=14.31ms min=789.83µs med=5.69ms max=202.07ms p(90)=67.44ms  p(95)=78.25ms  p(99)=90.73ms  count=1169928
     grpc_reqs............: 1169928 1299.876429/s
     grpc_success_reqs....: 1169928 1299.876429/s
     iteration_duration...: avg=70.67ms min=4.25ms   med=67.1ms max=476.21ms p(90)=134.18ms p(95)=162.92ms p(99)=201.67ms count=389976
     iterations...........: 389976  433.292143/s
     vus..................: 35      min=0                  max=97
     vus_max..............: 108     min=100                max=108


running (15m00.0s), 0000/0108 VUs, 389976 complete and 0 interrupted iterations
rampingStress ✓ [======================================] 0000/0108 VUs  15m0s  520.00 iters/s

```

## reactive, 2 Core, 2 Gi,  -Xms1700M -Xmx1700M

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

## imperative, 3 Core, 3 Gi, -Xms2500M -Xmx2500M

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
              * rampingStress: Up to 990.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-5000, gracefulStop: 30s)


     ✓ status is OK

   ✓ checks...............: 100.00% 2205504 out of 2205504
     data_received........: 8.0 GB  8.9 MB/s
     data_sent............: 393 MB  437 kB/s
     dropped_iterations...: 7331    8.136084/s
   ✓ grpc_req_duration....: avg=296.9ms  min=772.75µs med=324.85ms max=1.72s p(90)=499.14ms p(95)=527.06ms p(99)=622.84ms count=2205504
     grpc_reqs............: 2205504 2447.710585/s
     grpc_success_reqs....: 2205504 2447.710585/s
     iteration_duration...: avg=956.27ms min=4.18ms   med=1.12s    max=3.71s p(90)=1.5s     p(95)=1.59s    p(99)=1.78s    count=735168
     iterations...........: 735168  815.903528/s
     vus..................: 59      min=0                  max=1783
     vus_max..............: 1799    min=100                max=1799


running (15m01.0s), 0000/1799 VUs, 735168 complete and 0 interrupted iterations
rampingStress ✓ [======================================] 0000/1799 VUs  15m0s  990.00 iters/s
```

## imperative, 3 Core, 3 Gi, 880 iters
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
              * rampingStress: Up to 880.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-5000, gracefulStop: 30s)


     ✓ status is OK

   ✓ checks...............: 100.00% 1978008 out of 1978008
     data_received........: 7.2 GB  8.0 MB/s
     data_sent............: 353 MB  392 kB/s
     dropped_iterations...: 663     0.736463/s
   ✓ grpc_req_duration....: avg=46.43ms  min=720.73µs med=31.56ms  max=611.2ms p(90)=94.12ms  p(95)=103.98ms p(99)=128.62ms count=1978008
     grpc_reqs............: 1978008 2197.179844/s
     grpc_success_reqs....: 1978008 2197.179844/s
     iteration_duration...: avg=182.29ms min=3.92ms   med=200.36ms max=1.32s   p(90)=280.76ms p(95)=298.71ms p(99)=335.77ms count=659336
     iterations...........: 659336  732.393281/s
     vus..................: 212     min=0                  max=236
     vus_max..............: 279     min=100                max=279


running (15m00.2s), 0000/0279 VUs, 659336 complete and 0 interrupted iterations
rampingStress ✓ [======================================] 0000/0279 VUs  15m0s  880.00 iters/s
```
## reactive, 3 Core, 3 Gi  -Xms2500M -Xmx2500M, 880 iters

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
              * rampingStress: Up to 880.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-5000, gracefulStop: 30s)


     ✓ status is OK

   ✓ checks...............: 100.00% 1971972 out of 1971972
     data_received........: 7.2 GB  8.0 MB/s
     data_sent............: 352 MB  391 kB/s
     dropped_iterations...: 2675    2.970376/s
   ✓ grpc_req_duration....: avg=141.8ms  min=820.09µs med=153.75ms max=1.8s  p(90)=228.11ms p(95)=265.14ms p(99)=305.42ms count=1971972
     grpc_reqs............: 1971972 2189.719072/s
     grpc_success_reqs....: 1971972 2189.719072/s
     iteration_duration...: avg=483.91ms min=3.89ms   med=536.56ms max=3.09s p(90)=755.44ms p(95)=805.75ms p(99)=902.72ms count=657324
     iterations...........: 657324  729.906357/s
     vus..................: 719     min=0                  max=766
     vus_max..............: 795     min=100                max=795


running (15m00.6s), 0000/0795 VUs, 657324 complete and 0 interrupted iterations
rampingStress ✓ [======================================] 0000/0795 VUs  15m0s  880.00 iters/s
```

## imperative, 3 Core, 3 Gi, 800 iter/s
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
              * rampingStress: Up to 800.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-5000, gracefulStop: 30s)


     ✓ status is OK

   ✓ checks...............: 100.00% 1799802 out of 1799802
     data_received........: 6.5 GB  7.3 MB/s
     data_sent............: 321 MB  357 kB/s
     dropped_iterations...: 65      0.072218/s
   ✓ grpc_req_duration....: avg=13.58ms min=712.4µs med=7.9ms   max=368.07ms p(90)=29.63ms  p(95)=59.99ms  p(99)=75.6ms   count=1799802
     grpc_reqs............: 1799802 1999.671913/s
     grpc_success_reqs....: 1799802 1999.671913/s
     iteration_duration...: avg=63.5ms  min=4.08ms  med=64.49ms max=393.57ms p(90)=104.89ms p(95)=117.82ms p(99)=159.57ms count=599934
     iterations...........: 599934  666.557304/s
     vus..................: 29      min=0                  max=88
     vus_max..............: 122     min=100                max=122


running (15m00.0s), 0000/0122 VUs, 599934 complete and 0 interrupted iterations
rampingStress ✓ [======================================] 0000/0122 VUs  15m0s  800.00 iters/s
```

## reactive, 3 Core, 3 Gi, 800 iter/s
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
              * rampingStress: Up to 800.00 iterations/s for 15m0s over 2 stages (maxVUs: 100-5000, gracefulStop: 30s)


     ✓ status is OK

✓ checks...............: 100.00% 1798332 out of 1798332
data_received........: 6.5 GB  7.2 MB/s
data_sent............: 321 MB  356 kB/s
dropped_iterations...: 555     0.616547/s
✓ grpc_req_duration....: avg=39.08ms  min=770.71µs med=28.41ms  max=394.08ms p(90)=79.7ms   p(95)=89.63ms  p(99)=108.7ms  count=1798332
grpc_reqs............: 1798332 1997.757445/s
grpc_success_reqs....: 1798332 1997.757445/s
iteration_duration...: avg=158.19ms min=3.59ms   med=171.09ms max=1.31s    p(90)=233.47ms p(95)=261.04ms p(99)=304.91ms count=599444
iterations...........: 599444  665.919148/s
vus..................: 123     min=0                  max=253
vus_max..............: 261     min=100                max=261


running (15m00.2s), 0000/0261 VUs, 599444 complete and 0 interrupted iterations
rampingStress ✓ [======================================] 0000/0261 VUs  15m0s  800.00 iters/s
```