# Kafka로 메시지/ 이벤트 처리하기

![Image](https://res.craft.do/user/full/e7bc7144-9bd1-660d-9874-b30e85492b64/doc/F1EE68BA-35C8-4A19-952B-60554F5AD7DA/2E5FC9B2-AB60-470A-926A-AB64229C7CD3_2/Image)

이번 글은 카프카, [데이터 플랫폼의 최강자](http://www.yes24.com/Product/Goods/59789254) 라는 책을 참고하여 공부하였으며, 간단한 실행 및 예제 프로그래밍은 책보다 공식[홈페이지에 있는 문서 를 참고](https://kafka.apache.org/quickstart#quickstart_kafkaconnect)하였습니다.

# Apache Kafka

Apache Kafka는 실시간으로 기록 스트림을 게시, 구독, 저장 및 처리할 수 있는 분산 데이터 스트리밍 플랫폼입니다. 이는 여러 소스에서 데이터 스트림을 처리하고 여러 사용자에게 전달하도록 설계되었습니다. 간단히 말해, A지점에서 B지점까지 이동하는 것뿐만 아니라 A지점에서 Z지점을 비롯해 필요한 모든 곳에서 대규모 데이터를 동시에 이동할 수 있습니다.  → reference : [RedHat,  Apache Kafka 소개](https://www.redhat.com/ko/topics/integration/what-is-apache-kafka)

## Kafka를 사용 할 때 짚고 넘어 갈 간단한 개념

- ZooKeeper

   Apache Zookeeper는 분산 코디네이션 서비스를 제공하는 오픈 소스입니다

   어플리케이션에서 스케쥴링 및 작업 조율을 직접 하지 않고 zookeeper가 조율을 도와줍니다. 안정성 확보를 위해서 클러스터로 구축이 되며 클러스터는 보통 홀수개로 구축합니다.

- Topic

   데이터의 '주제' 라고 생각하면 쉽게 이해 할 수 있습니다. 데이터의 주제 / 이벤트를 Topic으로 생성하고 해당하는 Topic에 데이터를 보내고 읽을 수 있습니다.

   예를 들어 Topic을 temperature로 설정하고, temperature Topic에 관련된 데이터를 보내고 읽을 수 있습니다.

- Producer

   데이터를 제공하는 쪽 입니다. "떠드는 쪽" 이라고 해도 괜찮을 것 같습니다. 데이터를 생성하고, 이벤트를 생성하는 쪽 입니다. 설정 된 Topic에 데이터를 제공합니다.

- Consumer

   데이터를 소비하는 쪽 입니다. 즉, 데이터를 필요로 하는 쪽 입니다. 데이터나 이벤트의 발생을 보고 분석하거나 저장하는 쪽으로 이어주는 역할을 할 수 도 있습니다.

## Kafka 설치 및 실행하기

실행시키고 확인 해야 할 서비스가 많습니다!! 때문에 터미널을 좀 많이 켜야겠네요 하핳…

### 설치 (Download)

[https://www.apache.org/dyn/closer.cgi?path=/kafka/2.8.0/kafka_2.13-2.8.0.tgz](https://www.apache.org/dyn/closer.cgi?path=/kafka/2.8.0/kafka_2.13-2.8.0.tgz)

위의 페이지에서 Kafka를 받고, 적절한 위치로 이동시켜주세요.

### Unzip

```shell
$ tar -xzf kafka_2.13-2.8.0.tgz
$ cd kafka_2.13-2.8.0
```

### Kafka 실행을 위한 Zookeeper, Broker 실행하기

```shell
// kafka_2.13-2.8.0 폴더 내의 위치에서 실행하면 됩니다.
// zookeeper
$ bin/zookeeper-server-start.sh config/zookeeper.properties

// broker service
$ bin/kafka-server-start.sh config/server.properties
```

### 메시지 발송을 위한 Topic 생성하기

```shell
// Kafka myevent Topic 생성
$ bin/kafka-topics.sh --create --topic myevent --bootstrap-server localhost:9092

// myevent Topic 구독 현황 확인하기
$ bin/kafka-topics.sh --describe --topic myevent --bootstrap-server localhost:9092
```

### Topic에 메시지 발송하기 (command line interface에서)

```shell
$ bin/kafka-console-producer.sh --topic myevent --bootstrap-server localhost:9092
>
```

**kafka-console-producer.sh** 를 실행하면, 메시지를 계속 생성하고 보내는 역할을 합니다. 때문에 콘솔창은 input이 가능한 형태로 계속 열려있습니다.

### Topic에 발송 한 메시지 읽어오기(command line interface에서)

```shell
$ bin/kafka-console-consumer.sh --topic myevent --from-beginning --bootstrap-server localhost:9092
```

위의 kafka-console-producer.sh 에서 작성 한 데이터가 출력됩니다

## Kafka Topic을 발행하고 producer와 consumer 프로그램 만들기 - Python

새로운 Topic을 생성 해 주세요. blog-post 라는 Topic을 생성 하겠습니다

```shell
$ bin/kafka-topics.sh --create --topic blog-post --bootstrap-server localhost:9092
```

### 개발환경 구축하기

- 가상환경 설정하기

```shell
$ virtualenv venv
$ soruce venv/bin/activate
```

- kafka module 설치하기

```shell
(venv) $ pip3 install kafka-python
```

### producer.py

10000개의 데이터를 Queue에 보내는데 얼마나 걸리는지 측정 해 보기 위해 time을 사용했습니다

```python
import time
from kafka import KafkaProducer

# producer 객체 생성
# acks 0 -> 빠른 전송우선, acks 1 -> 데이터 정확성 우선
producer = KafkaProducer(acks=0, compression_type='gzip',bootstrap_servers=['localhost:9092'])


start = time.time()

for i in range(10000):
	producer.send('blog-post',b'Kafka Blog Post Event Message')
	producer.flush() #queue에 있는 데이터를 보냄

end = time.time() - start
print(end)
```

### consumer.py

```python
from kafka import KafkaConsumer, consumer

# consumer 객체 생성
consumer = KafkaConsumer(
    'blog-post',
    bootstrap_servers=['127.0.0.1:9092'],
    auto_offset_reset='earliest', 
    enable_auto_commit=True,
    consumer_timeout_ms=1000
)


while True:
    for message in consumer:
        print(message.topic, message.partition, message.offset, message.key, message.value)
```

### 실행

- 시작하기 전, Topic을 생성했기 때문에 producer.py와 consumer.py만 실행 해 주면 됩니다
- producer.py는 데이터를 발생시키는 친구이고, 10000개의 데이터를 'blog-post’ Topic으로 전송합니다
- consumer.py는 데이터를 읽어오는 친구이고, 무한루프를 만들었기 때문에 데이터를 받는대로 출력 해 줍니다

```shell
> python producer.py 
3.8476390838623047
```

```shell
> python consumer.py 
...
blog-post 0 100 None b'Kafka Blog Post Event Message'
blog-post 0 101 None b'Kafka Blog Post Event Message'
blog-post 0 102 None b'Kafka Blog Post Event Message'
blog-post 0 103 None b'Kafka Blog Post Event Message'
...
```
