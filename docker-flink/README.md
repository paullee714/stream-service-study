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




# Docker를 사용한 Apache Flink와 Flink Job 올리기(2) - Flink Job Example

Flink 작업을 위해서 [Apache Flink 책](https://g.co/kgs/SmtDyc)인 <Stream Processing with Apache Flink: Fundamentals, Implementation, and Operation of Streaming Applications> 를 참고했으며 예제코드를 다운받아 작성했습니다

사실상 코드는 책에서 제공 해 주는 코드를 실행하기 때문에, 주의 해 주어야 할 것은 Maven 설정을 잘 해주는 것과 Java 환경설정, 그리고 JAR 파일을 잘 말아주는 것 밖에는 할 것이 없었습니다.

Flink에 조금 더 익숙 해 진다면 직접 Flink Job을 만들어 보고싶은 생각이…

# 개발환경

- macOS
- Java 15
- Intellij
- Maven

# Flink Job Code

### 소스코드 다운받기

소스코드 출처 - [링크](https://github.com/streaming-with-flink/examples-java)

`git clone` [`https://github.com/streaming-with-flink/examples-java`](https://github.com/streaming-with-flink/examples-java)

### 소스코드 Open하기 - Intellij

- Intellij → File → Open → [examples-java 다운경로]
- maven 파일 선택하기

### 소스코드

- java File

```java
/*
 * Copyright 2015 Fabian Hueske / Vasia Kalavri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.streamingwithflink.chapter1;

import io.github.streamingwithflink.util.SensorReading;
import io.github.streamingwithflink.util.SensorSource;
import io.github.streamingwithflink.util.SensorTimeAssigner;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class AverageSensorReadings {

    /**
     * main() defines and executes the DataStream program.
     *
     * @param args program arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        // set up the streaming execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // use event time for the application
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        // configure watermark interval
        env.getConfig().setAutoWatermarkInterval(1000L);

        // ingest sensor stream
        DataStream<SensorReading> sensorData = env
            // SensorSource generates random temperature readings
            .addSource(new SensorSource())
            // assign timestamps and watermarks which are required for event time
            .assignTimestampsAndWatermarks(new SensorTimeAssigner());

        DataStream<SensorReading> avgTemp = sensorData
            // convert Fahrenheit to Celsius using and inlined map function
            .map( r -> new SensorReading(r.id, r.timestamp, (r.temperature - 32) * (5.0 / 9.0)))
            // organize stream by sensor
            .keyBy(r -> r.id)
            // group readings in 1 second windows
            .timeWindow(Time.seconds(1))
            // compute average temperature using a user-defined function
            .apply(new TemperatureAverager());

        // print result stream to standard out
        avgTemp.print();

        // execute application
        env.execute("Compute average sensor temperature");
    }

    /**
     *  User-defined WindowFunction to compute the average temperature of SensorReadings
     */
    public static class TemperatureAverager implements WindowFunction<SensorReading, SensorReading, String, TimeWindow> {

        /**
         * apply() is invoked once for each window.
         *
         * @param sensorId the key (sensorId) of the window
         * @param window meta data for the window
         * @param input an iterable over the collected sensor readings that were assigned to the window
         * @param out a collector to emit results from the function
         */
        @Override
        public void apply(String sensorId, TimeWindow window, Iterable<SensorReading> input, Collector<SensorReading> out) {

            // compute the average temperature
            int cnt = 0;
            double sum = 0.0;
            for (SensorReading r : input) {
                cnt++;
                sum += r.temperature;
            }
            double avgTemp = sum / cnt;

            // emit a SensorReading with the average temperature
            out.collect(new SensorReading(sensorId, window.getEnd(), avgTemp));
        }
    }
}
```

- pom.xml

```xml
<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>io.github.streamingwithflink</groupId>
	<artifactId>examples-java</artifactId>
	<version>1.0</version>
	<packaging>jar</packaging>

	<name>Java Examples for Stream Processing with Apache Flink</name>
	<url>http://streaming-with-flink.github.io/examples</url>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<flink.version>1.7.1</flink.version>
		<java.version>1.8</java.version>
		<scala.binary.version>2.12</scala.binary.version>
		<maven.compiler.source>${java.version}</maven.compiler.source>
		<maven.compiler.target>${java.version}</maven.compiler.target>
	</properties>

	<dependencies>
		<!-- Apache Flink dependencies -->
		<!-- These dependencies are provided, because they should not be packaged into the JAR file. -->
		<dependency>
			<groupId>org.apache.flink</groupId>
			<artifactId>flink-java</artifactId>
			<version>${flink.version}</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>org.apache.flink</groupId>
			<artifactId>flink-streaming-java_${scala.binary.version}</artifactId>
			<version>${flink.version}</version>
			<scope>provided</scope>
		</dependency>

		<!-- runtime-web dependency is need to start web UI from IDE -->
                <dependency>
                        <groupId>org.apache.flink</groupId>
                        <artifactId>flink-runtime-web_${scala.binary.version}</artifactId>
                        <version>${flink.version}</version>
                        <scope>provided</scope>
                </dependency>

		<!-- queryable-state dependencies are needed for respective examples -->
                <dependency>
                        <groupId>org.apache.flink</groupId>
                        <artifactId>flink-queryable-state-runtime_${scala.binary.version}</artifactId>
                        <version>${flink.version}</version>
                </dependency>
                <dependency>
                        <groupId>org.apache.flink</groupId>
                        <artifactId>flink-queryable-state-client-java_${scala.binary.version}</artifactId>
                        <version>${flink.version}</version>
                </dependency>

		<!--
                Derby is used for a sink connector example.
                Example only works in local mode, i.e, it is not possible to submit it to a running cluster.
                The dependency is set to provided to reduce the size of the JAR file.
                -->
                <dependency>
                        <groupId>org.apache.derby</groupId>
                        <artifactId>derby</artifactId>
                        <version>10.13.1.1</version>
                        <scope>provided</scope>
                </dependency>

                <!-- Logging -->
                <dependency>
                        <groupId>org.slf4j</groupId>
                        <artifactId>slf4j-log4j12</artifactId>
                        <version>1.7.25</version>
                        <scope>runtime</scope>
                </dependency>
                <dependency>
                        <groupId>log4j</groupId>
                        <artifactId>log4j</artifactId>
                        <version>1.2.17</version>
                        <scope>runtime</scope>
                </dependency>
	</dependencies>

	<build>
		<plugins>
			<!-- Java Compiler -->
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<version>3.1</version>
				<configuration>
					<source>${java.version}</source>
					<target>${java.version}</target>
				</configuration>
			</plugin>

			<!-- We use the maven-shade plugin to create a fat jar that contains all necessary dependencies. -->
			<!-- Change the value of <mainClass>...</mainClass> if your program entry point changes. -->
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-shade-plugin</artifactId>
				<version>3.0.0</version>
				<executions>
					<!-- Run shade goal on package phase -->
					<execution>
						<phase>package</phase>
						<goals>
							<goal>shade</goal>
						</goals>
						<configuration>
							<artifactSet>
								<excludes>
									<exclude>org.apache.flink:force-shading</exclude>
									<exlude>org.apache.flink:flink-shaded-netty</exlude>
                                                                        <exlude>org.apache.flink:flink-shaded-guava</exlude>
									<exclude>com.google.code.findbugs:jsr305</exclude>
									<exclude>org.slf4j:*</exclude>
									<exclude>log4j:*</exclude>
								</excludes>
							</artifactSet>
							<filters>
								<filter>
									<!-- Do not copy the signatures in the META-INF folder.
									Otherwise, this might cause SecurityExceptions when using the JAR. -->
									<artifact>*:*</artifact>
									<excludes>
										<exclude>META-INF/*.SF</exclude>
										<exclude>META-INF/*.DSA</exclude>
										<exclude>META-INF/*.RSA</exclude>
									</excludes>
								</filter>
							</filters>
							<transformers>
								<transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
									<mainClass>io.github.streamingwithflink.StreamingJob</mainClass>
								</transformer>
							</transformers>
						</configuration>
					</execution>
				</executions>
			</plugin>
		</plugins>

		<pluginManagement>
			<plugins>

				<!-- This improves the out-of-the-box experience in Eclipse by resolving some warnings. -->
				<plugin>
					<groupId>org.eclipse.m2e</groupId>
					<artifactId>lifecycle-mapping</artifactId>
					<version>1.0.0</version>
					<configuration>
						<lifecycleMappingMetadata>
							<pluginExecutions>
								<pluginExecution>
									<pluginExecutionFilter>
										<groupId>org.apache.maven.plugins</groupId>
										<artifactId>maven-shade-plugin</artifactId>
										<versionRange>[3.0.0,)</versionRange>
										<goals>
											<goal>shade</goal>
										</goals>
									</pluginExecutionFilter>
									<action>
										<ignore/>
									</action>
								</pluginExecution>
								<pluginExecution>
									<pluginExecutionFilter>
										<groupId>org.apache.maven.plugins</groupId>
										<artifactId>maven-compiler-plugin</artifactId>
										<versionRange>[3.1,)</versionRange>
										<goals>
											<goal>testCompile</goal>
											<goal>compile</goal>
										</goals>
									</pluginExecutionFilter>
									<action>
										<ignore/>
									</action>
								</pluginExecution>
							</pluginExecutions>
						</lifecycleMappingMetadata>
					</configuration>
				</plugin>
			</plugins>
		</pluginManagement>
	</build>

	<!-- This profile helps to make things run out of the box in IntelliJ -->
	<!-- Its adds Flink's core classes to the runtime class path. -->
	<!-- Otherwise they are missing in IntelliJ, because the dependency is 'provided' -->
	<profiles>
		<profile>
			<id>add-dependencies-for-IDEA</id>

			<activation>
				<property>
					<name>idea.version</name>
				</property>
			</activation>

			<dependencies>
				<dependency>
					<groupId>org.apache.flink</groupId>
					<artifactId>flink-java</artifactId>
					<version>${flink.version}</version>
					<scope>compile</scope>
				</dependency>
				<dependency>
					<groupId>org.apache.flink</groupId>
					<artifactId>flink-streaming-java_${scala.binary.version}</artifactId>
					<version>${flink.version}</version>
					<scope>compile</scope>
				</dependency>
				<dependency>
                                        <groupId>org.apache.flink</groupId>
                                        <artifactId>flink-runtime-web_${scala.binary.version}</artifactId>
                                        <version>${flink.version}</version>
                                        <scope>compile</scope>
                                </dependency>
                                <dependency>
                                        <groupId>org.apache.derby</groupId>
                                        <artifactId>derby</artifactId>
                                        <version>10.13.1.1</version>
                                        <scope>compile</scope>
                                </dependency>
			</dependencies>
		</profile>
	</profiles>

</project>
```

### JAR 파일 만들기

프로젝트 우클릭 → 모듈설정 → 아티팩트 설정 → 추가 → JAR file → 종속성 포함 모듈 설정

위의 명령대로 실행하면 아래의 화면이 나옵니다

![Image.png](https://res.craft.do/user/full/e7bc7144-9bd1-660d-9874-b30e85492b64/doc/EDDE7445-26FF-495C-AB4A-2F9CFA0C1ACC/C4819D0D-0BC0-48F8-B0C7-8BFF108A6BE0_2/Image.png)

메인 클래스 오른쪽에 폴더를 누르면 우리가 실행 할 Main 파일이 자동으로 등록 되어 있습니다

설정이 완료되면 ‘확인'을 눌러주고 팝업창을 닫아주세요

이제 JAR 파일을 말아주어야 합니다. 설정한 아티팩트대로 JAR가 빌드 됩니다. 아티팩트를 설정 했기 때문에 빌드 메뉴의 아티팩트 빌드가 활성화 되어있습니다.

`빌드 → 아티팩트 빌드`

빌드가 완료된다면 우리가 지정한 경로에 JAR 파일이 생성됩니다 (특별한 설정을 하지 않았기 때문에 out/artifacts/examples_jar 폴더 아래에 생성 됩니다.

# Flink Job 등록하기

이제 만들어 준 Flink Job을 Docker로 설정 한 Flink 서버에 넣어 주어야 합니다

왼쪽 메뉴에 가장 아래에 있는 “Submit new Job"을 눌러주세요

![Image.png](https://res.craft.do/user/full/e7bc7144-9bd1-660d-9874-b30e85492b64/doc/EDDE7445-26FF-495C-AB4A-2F9CFA0C1ACC/B7DF8F43-8DBF-4B96-8ED6-B984415810CB_2/Image.png)

Add New 를 누르면, 우리가 만든 JAR 파일을 하나의 Job으로 등록 할 수 있습니다.

Add New를 누르고, JAR 파일을 선택해서 Upload하면 아래의 화면처럼 JOB이 등록 됩니다.

![Image.png](https://res.craft.do/user/full/e7bc7144-9bd1-660d-9874-b30e85492b64/doc/EDDE7445-26FF-495C-AB4A-2F9CFA0C1ACC/31A0C03E-CA18-4E4B-8772-9290D17C29CF_2/Image.png)

이제 실행 할 수 있도록 해당하는 job을 눌러주고 실행을 위해 submit을 눌러주세요

![Image.png](https://res.craft.do/user/full/e7bc7144-9bd1-660d-9874-b30e85492b64/doc/EDDE7445-26FF-495C-AB4A-2F9CFA0C1ACC/B5175C82-6F1D-41D2-A03A-0B00C7D211F6_2/Image.png)

Submit이 완료되면 아래의 화면처럼 태스크가 실행 되는 것을 확인 할 수 있습니다

![Image.png](https://res.craft.do/user/full/e7bc7144-9bd1-660d-9874-b30e85492b64/doc/EDDE7445-26FF-495C-AB4A-2F9CFA0C1ACC/7204241C-B144-4E50-BFB7-4B1CCDE284E3_2/Image.png)

OverView 메뉴에서도 우리가 실행 한 작업들이 잘 보입니다

![Image.png](https://res.craft.do/user/full/e7bc7144-9bd1-660d-9874-b30e85492b64/doc/EDDE7445-26FF-495C-AB4A-2F9CFA0C1ACC/B89F65C9-DA94-4F9E-ADF6-4BE4BAD5CC96_2/Image.png)

## 아직 완료하지 못한 것

docker가 아닌 로컬에서 Flink를 실행하고 job을 올리면 잘 보이는 Flink log가 Docker 상에서는 보이지 않습니다...

![Image.png](https://res.craft.do/user/full/e7bc7144-9bd1-660d-9874-b30e85492b64/doc/EDDE7445-26FF-495C-AB4A-2F9CFA0C1ACC/A407EB6E-2270-4410-A82C-628ADA141888_2/Image.png)

`"Internal server error.","<Exception on server side:org.apache.flink.util.FlinkException: The file STDOUT is not available on the TaskExecutor…… "`

요런 에러가 뜨는데 해결 해야 할 Task로 남겨두고 공부 해 봐야겠습니다..

