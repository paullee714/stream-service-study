# Docker를 사용한 Apache Flink와 Flink Job 올리기(1) - Docker Setting

이번 글의 내용은 [Apache Flink 책](https://g.co/kgs/SmtDyc)인 <Stream Processing with Apache Flink: Fundamentals, Implementation, and Operation of Streaming Applications> 를 참고했으며 직접 개발환경을 세팅 해 보고 작성하였습니다.

Docker를 기반으로 작업 하려고 합니다. Docker를 다룰 줄 알거나 설치가 되어 있다는 가정 하에 작업을 해 보도록 하겠습니다.

# Apache Flink

플링크는 스파크 다음 세대의 빅데이터 분석 프레임워크로서 짧은 지연시간 내에 스트림 데이터를 처리하면서도 강력한 상태 관리가 필요한 경우에 최적의 선택이 될 수 있습니다

이번에는 설치나 세팅이 비교적 간편한 Docker를 사용해서 Apache Flink를 실행 해 보도록 하겠습니다

# Apache Flink 가장 기본적인 친구들

![Image](https://res.craft.do/user/full/e7bc7144-9bd1-660d-9874-b30e85492b64/doc/ABB230F5-800B-4E43-AFD9-36E4E42600BE/378DD088-8F18-413A-A18D-0D9382B650AA_2/Image)

- Master Process

   → Job Manager 라고도 불립니다

   → Flink의 마스터 노드 입니다. Flink내의 Task들을 실행하는 Worker들을 관리하는 노드입니다

   → Task를 스케쥴링하여 실행 시점을 관리하며 모니터링을 합니다

   → 문제가 생겼을 경우 Recover를 담당합니다

- Worker Process

   → Task Manager 라고도 불립니다

   → Master Process에게 할당 받은 Task들을 처리합니다

   → 작성 된 Job App (JAR파일) 실행을 담당합니다

# Apache Flink Docker에서 띄우기

Docker에서 Apache Flink를 띄워보겠습니다. 위에서 설명 된 것 처럼 1개의 Master Process를 띄우고, Worker Process는 입맛에 맞게 띄워보도록 하겠습니다! (올릴 job이 많으면... worker를 늘리면 되겠네용)

저는 Mac을 사용합니다.  Intel과 M1 모두 다 명령어를 정리 해 보았습니다. Window이신분들은 Intel 따라서 하시면 될 것 같고, Linux는 amd64를 사용하시면 될 것 같습니다!

### Intel Mac

- Master Process (Job Manager)

```shell
docker run -d --name flink-jobmanager -e JOB_MANAGER_RPC_ADDRESS=jobmanager -p 8081:8081 flink jobmanager
```

- Worker Process (Task Manager)

```other
# task manager 1
docker run -d --name flink-taskmanager-1 --link flink-jobmanager:jobmanager -e JOB_MANAGER_RPC_ADDRESS=jobmanager flink taskmanager

# task manager 2
docker run -d --name flink-taskmanager-2 --link flink-jobmanager:jobmanager -e JOB_MANAGER_RPC_ADDRESS=jobmanager flink taskmanager
```

### M1 Mac

- Master Process (Job Manager)

```shell
docker run -d --name flink-jobmanager -e JOB_MANAGER_RPC_ADDRESS=jobmanager -p 8081:8081 arm64v8/flink jobmanager
```

- Worker Process (Task Manager)

```other
# task manager 1
docker run -d --name flink-taskmanager-1 --link flink-jobmanager:jobmanager -e JOB_MANAGER_RPC_ADDRESS=jobmanager arm64v8/flink taskmanager

# task manager 2
docker run -d --name flink-taskmanager-2 --link flink-jobmanager:jobmanager -e JOB_MANAGER_RPC_ADDRESS=jobmanager arm64v8/flink taskmanager
```

### Linux

- Master Process (Job Manager)

```shell
docker run -d --name flink-jobmanager -e JOB_MANAGER_RPC_ADDRESS=jobmanager -p 8081:8081 amd64/flink jobmanager
```

- Worker Process (Task Manager)

```other
# task manager 1
docker run -d --name flink-taskmanager-1 --link flink-jobmanager:jobmanager -e JOB_MANAGER_RPC_ADDRESS=jobmanager amd64/flink taskmanager

# task manager 2
docker run -d --name flink-taskmanager-2 --link flink-jobmanager:jobmanager -e JOB_MANAGER_RPC_ADDRESS=jobmanager amd64/flink taskmanager
```

# 결과화면

저는 task manager를 3개까지 올렸습니다. 중간에 있는 flink-taskmanager를 1~3 까지 생성했습니다

저는 m1 Mac을 사용하는데, docker의 Flink 중 m1 mac과 호환되는 Flink는 버전이 좀 낮았습니다. 현재기준 (2021.08.18) 최신 버전은 Flink-1.8.0 버전입니다. Docker가 아닌 Flink 공식 홈페이지에서 파일을 받으면, Flink-1.13.0 버전이라서 화면 구성이 조금 다르네요!

![Image.png](https://res.craft.do/user/full/e7bc7144-9bd1-660d-9874-b30e85492b64/doc/ABB230F5-800B-4E43-AFD9-36E4E42600BE/7F7E6CA9-1D86-44E0-837A-EF1C8B7A4E76_2/Image.png)

![Image.png](https://res.craft.do/user/full/e7bc7144-9bd1-660d-9874-b30e85492b64/doc/ABB230F5-800B-4E43-AFD9-36E4E42600BE/B40782A4-255F-4C99-99FA-93D883B141B5_2/Image.png)


