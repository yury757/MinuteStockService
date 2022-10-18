package net.yury.MinuteStockService.config;

import com.google.common.collect.ImmutableList;
import com.google.inject.Singleton;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Properties;

@Singleton
public class KafkaClientConsumer {
    public final KafkaConsumer<String, String> consumer;
    public static final int MAX_POLL_RECORDS_CONFIG = 200;

    public KafkaClientConsumer() {
        Properties props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.141.141:9092"); // 10.10.16.50:9092
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "test");
        props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, String.valueOf(MAX_POLL_RECORDS_CONFIG));
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> con = new KafkaConsumer<>(props);
        con.subscribe(ImmutableList.of("t1"));
        System.out.println("kafka consumer start");
        this.consumer = con;
    }
}
