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
