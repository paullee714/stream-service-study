5587 4901 0247 7224 11/27 697



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


## Kafka Topic을 발행하고 producer와 consumer 프로그램 만들기 - Java

마찬가지로, Topic은 "blog-post”로 이미 생성을 해 놓았으니, Producer, Consumer를 만들어 보도록 하겠습니다

### java maven dependency

```xml
<dependencies>
        <!-- https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients -->
        <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka-clients</artifactId>
            <version>2.8.0</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.apache.kafka/kafka -->
        <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka_2.13</artifactId>
            <version>2.8.0</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.apache.kafka/kafka-streams -->
        <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka-streams</artifactId>
            <version>2.8.0</version>
        </dependency>

        <dependency>
            <groupId> org.apache.cassandra</groupId>
            <artifactId>cassandra-all</artifactId>
            <version>0.8.1</version>

            <exclusions>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-log4j12</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>log4j</groupId>
                    <artifactId>log4j</artifactId>
                </exclusion>
            </exclusions>

        </dependency>

    </dependencies>
```

- 위의 dependency를 maven pom.xml에 등록하고 관련 소스를 받아주세요

### src/main/java/kafka/MyKafkaProducer.java

```java
package kafka;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class MyKafkaProducer {
    public static final String TOPIC = "blog-post";

    public static void main(String[] args) throws IOException, InterruptedException{

        Random random = new Random();
        int message = 0;

        Properties properties = new Properties();
        properties.put("bootstrap.servers","localhost:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("compression.type","gzip");
        properties.put("acks", "1");
        properties.put("block.on.buffer.full", "true");

        Producer<String,String> producer = new KafkaProducer<>(properties);
        while(true){

            message = random.nextInt(1000);
            if(message % 2 == 1){
                producer.send(new ProducerRecord<String,String>(TOPIC,"ODD",String.format("%d - kafka Streaming - key : %s",message,"ODD")));
            }else{
                producer.send(new ProducerRecord<String,String>(TOPIC,"EVEN",String.format("%d - kafka Streaming - key : %s",message,"EVEN")));
            }

            Thread.sleep(500);
        }

    }

}
```

- While문으로 메시지 무한전송 프로그램을 작성했습니다
- Thread.sleep(500)으로 0.5초마다 데이터를 전송하게 작성했습니다
- 랜덤으로 생성되는 수가 짝/홀수임에 따라서 "ODD”, “EVEN” 을 같이 전달하도록 작성했습니다

### 실행

```shell
$ bin/kafka-console-consumer.sh --topic blog-post --bootstrap-server localhost:9092
```

- shell을 실행시키고 우리가 작성한 MyKafkaProducer의 기능을 확인 해 보도록 하겠습니다!
- 위의 shell을 실행시킨 후, MyKafkaProducer를 실행시켜주면 아래와 같은 화면을 확인 할 수 있습니다

![Image.png](https://res.craft.do/user/full/e7bc7144-9bd1-660d-9874-b30e85492b64/doc/F1EE68BA-35C8-4A19-952B-60554F5AD7DA/D11FF410-F47C-4753-BA7A-FE97831533DA_2/Image.png)

### src/main/java/kafka/MyKafkaConsumer.java

shell로 확인 할 수 도 있지만, Consumer를 만들어 직접 읽어오도록 하겠습니다

```java
package kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class MyKafkaConsumer {

    public static final String TOPIC = "blog-post";

    public static void main(String[] args) throws IOException, InterruptedException{
        Properties properties = new Properties();
        properties.put("bootstrap.servers","localhost:9092");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.offset.reset", "latest");
        properties.put("group.id","blog-group");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String,String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(List.of(TOPIC));

        while(true){
            ConsumerRecords<String,String> records = consumer.poll(100);
            for(ConsumerRecord<String,String> record : records){
                System.out.printf("TOPIC : %s, Offset: %d, Key: %s, Value: %s\n",record.topic(),record.offset(),record.key(),record.value());
            }
        }

    }
}
```

### 실행

- consumer 클래스를 실행 한 후 producer 클래스도 실행 해 주어야 합니다

![Image.png](https://res.craft.do/user/full/e7bc7144-9bd1-660d-9874-b30e85492b64/doc/F1EE68BA-35C8-4A19-952B-60554F5AD7DA/8DDC2B1D-F678-4A3A-9004-65C3DB713132_2/Image.png)

