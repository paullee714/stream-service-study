# Apache Flink와 Flink Job 올리기 - Apache Flink Binary

공식 문서를 참고하여 진행했습니다 - [링크](https://ci.apache.org/projects/flink/flink-docs-release-1.13/docs/try-flink/local_installation/#step-2-start-a-cluster)

로컬 서버에 Flink를 올려 사용 해 보겠습니다

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

# Apache Flink 설치

### 다운로드 링크

[**https://mirror.navercorp.com/apache/flink/flink-1.13.2/flink-1.13.2-bin-scala_2.11.tgz**](https://mirror.navercorp.com/apache/flink/flink-1.13.2/flink-1.13.2-bin-scala_2.11.tgz)

### 압축 풀기

다운받은 파일 이름이 다르기 때문에 버전에 맞는 tgz파일을 압축 해제 해 주세요

```shell
$ tar -xzf flink-1.13.*****.tgz
$ cd flink-1.13
```

### 실행 환경 설정 - java

Flink는 Java 8 혹은 Java 11 에서 실행해야 한다고 Flink공식 홈페이지에서 소개하고있습니다

```shell
$ java -version
```

위의 명령어로 java 버전을 확인 해 주시고 JAVA_HOME 설정을 해 주세요

### flink 파일 경로

- 실행 파일 경로

```shell
├── bin
│   ├── bash-java-utils.jar
│   ├── config.sh
│   ├── find-flink-home.sh
│   ├── flink
│   ├── flink-console.sh
│   ├── flink-daemon.sh
│   ├── historyserver.sh
│   ├── jobmanager.sh
│   ├── kubernetes-jobmanager.sh
│   ├── kubernetes-session.sh
│   ├── kubernetes-taskmanager.sh
│   ├── mesos-appmaster-job.sh
│   ├── mesos-appmaster.sh
│   ├── mesos-jobmanager.sh
│   ├── mesos-taskmanager.sh
│   ├── pyflink-shell.sh
│   ├── sql-client.sh
│   ├── standalone-job.sh
│   ├── start-cluster.sh
│   ├── start-scala-shell.sh
│   ├── start-zookeeper-quorum.sh
│   ├── stop-cluster.sh
│   ├── stop-zookeeper-quorum.sh
│   ├── taskmanager.sh
│   ├── yarn-session.sh
│   └── zookeeper.sh
```

- 설정 파일 경로

```shell
├── conf
│   ├── flink-conf.yaml
│   ├── log4j-cli.properties
│   ├── log4j-console.properties
│   ├── log4j-session.properties
│   ├── log4j.properties
│   ├── logback-console.xml
│   ├── logback-session.xml
│   ├── logback.xml
│   ├── masters
│   ├── workers
│   └── zoo.cfg
```

### flink 환경설정

config/flink-conf.yaml

```other
taskmanager.numberOfTaskSlots: 4
```

- flink config폴더 안의 fllink-conf.yaml 파일은 설정값들을 가지고 있습니다
- config/flink-conf.yaml 파일 안의 taskmanager가 가질 수 있는 taskslot을 4개로 수정 해 주세요

### flink 실행

- 1개의 JobManager와 다수개의 TaskManager로 구성을 해야 합니다
- bin폴더 내의 `start-cluster.sh` 파일은 JobManager를 실행 해 주는 역할을 합니다
- 1개의 JobManager를 두고 Master Node 역할을 하도록 실행 해 주고, `taskmanager.sh` 로 태스크 매니저를 실행 하도록 하겠습니다

```shell
// flink폴더 내에서 실행합니다
// JobManager (Master Node 실행)
$ ./bin/start-cluster.sh

Starting cluster.
Starting standalonesession daemon on host.
Starting taskexecutor daemon on host.

// TaskManager (Slave Node 실행)
$ ./bin/taskmanager.sh start
```

### flink 종료

```shell
// TaskManager (Slave Node 종료)
$ ./bin/taskmanager.sh stop

// JobManager (Master Node 종료)
$ ./bin/stop-cluster.sh
```

### 실행 화면

Flink 서버를 실행하고, localhost:8081 로 접속 하면 Dashboard를 볼 수 있습니다.

Flink application을 어서 등록 해 보도록 하겠습니다~!

![Image.png](https://res.craft.do/user/full/e7bc7144-9bd1-660d-9874-b30e85492b64/doc/838CCBC6-AE7D-45DC-B557-7D1DE80B5807/790717EF-8C9B-4E6D-A135-463E4F49C562_2/Image.png)

