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
