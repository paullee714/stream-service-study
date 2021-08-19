import time
from kafka import KafkaProducer

# producer 객체 생성
# acks 0 -> 빠른 전송우선, acks 1 -> 데이터 정확성 우선
producer = KafkaProducer(
    acks=0, compression_type="gzip", bootstrap_servers=["localhost:9092"]
)


start = time.time()

for i in range(10000):
    producer.send("blog-post", b"Kafka Blog Post Event Message")
    producer.flush()  # queue에 있는 데이터를 보냄

end = time.time() - start
print(end)
