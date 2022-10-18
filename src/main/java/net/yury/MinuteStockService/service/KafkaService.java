package net.yury.MinuteStockService.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.yury.MinuteStockService.config.JedisPoolClient;
import net.yury.MinuteStockService.config.JsonUtil;
import net.yury.MinuteStockService.config.KafkaClientConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.KafkaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class KafkaService {
    public static final String NAME_STOCK_CODE = "stockCode";
    public static final String NAME_MARKET_ID = "marketId";
    public static final String NAME_PRICE = "price";
    public static final String NAME_DATE = "date";
    public static final String NAME_TIME = "time";

    public static final String REDIS_KEY_PREFIX = "zouyu2";
    public static final char REDIS_KEY_DELIMITER = '-';

    @Inject
    private KafkaClientConsumer kafkaClientConsumer;
    @Inject
    private JedisPoolClient jedisPoolClient;
    private Thread kafkaThread;
    private static final Logger LOG = LoggerFactory.getLogger(KafkaService.class);

    public void run() {
        if (kafkaThread == null) {
            kafkaThread = new Thread(() -> {
                while (true) {
                    ConsumerRecords<String, String> records = kafkaClientConsumer.consumer.poll(Duration.ofMillis(200));
                    List<String> values = new ArrayList<>(KafkaClientConsumer.MAX_POLL_RECORDS_CONFIG);
                    for (ConsumerRecord<String, String> record : records) {
                        long offset = record.offset();
                        String value = record.value();
                        LOG.info("offset = {}, value = {}", offset, value);
                        values.add(value);
                    }
                    try {
                        consume(values);
                        kafkaClientConsumer.consumer.commitSync();
                    } catch (JedisException | KafkaException ex) {
                        LOG.error("error", ex);
                    } catch (Exception ex) {
                        // 其他异常，说明代码处理有问题，或者有异常数据，打印并通知开发人员
                        LOG.error("consume error, kafka message: " + values);
                    }
                }
            });
        }else {
            LOG.warn("kafka consumer already started");
            return ;
        }
        kafkaThread.start();
        LOG.info("kafka consumer started");
    }

    public void consume(List<String> values) {
        if (values == null || values.isEmpty()) return;
        Jedis jedis = jedisPoolClient.pool.getResource();
        Pipeline pipeline = jedis.pipelined();
        try {
            long time1 = System.currentTimeMillis();
            for (String value : values) {
                ObjectNode node;
                try {
                    node = (ObjectNode)JsonUtil.INSTANCE.mapper.readTree(value);
                } catch (Exception e) {
                    System.out.printf("unsupported message %s%n", value);
                    continue;
                }
                if (!checkMessage(node)) continue;

                String code = node.get(NAME_STOCK_CODE).asText();
                String marketID = node.get(NAME_MARKET_ID).asText();
                String date = node.get(NAME_DATE).asText();
                String time = node.get(NAME_TIME).asText();
                double price = node.get(NAME_PRICE).asDouble();
                String key1 = REDIS_KEY_PREFIX + REDIS_KEY_DELIMITER + code + REDIS_KEY_DELIMITER + marketID;
                String key2 = key1 + REDIS_KEY_DELIMITER + date;
                pipeline.sadd(key1, date);
                pipeline.hset(key2, time, String.valueOf(price));
            }
            long time2 = System.currentTimeMillis();
            LOG.info("deal message, cost time: {}ms", time2 - time1);

            time1 = System.currentTimeMillis();
            pipeline.sync();
            time2 = System.currentTimeMillis();
            LOG.info("write redis, cost time: {}ms", time2 - time1);

            LOG.info("success put {} rows", values.size());
        }finally {
            pipeline.close();
            jedisPoolClient.pool.returnResource(jedis);
        }
    }

    public boolean checkMessage(ObjectNode node) {
        // 校验字段是否存在
        if (!node.has(NAME_STOCK_CODE) || !node.has(NAME_MARKET_ID) || !node.has(NAME_PRICE)
                || !node.has(NAME_DATE) || !node.has(NAME_TIME)
        ) return false;

        // 校验 price 字段是否是double
        JsonNode jsonNode = node.get(NAME_PRICE);
        if (!jsonNode.getNodeType().equals(JsonNodeType.NUMBER)) return false;

        // 校验 date 字段是否是日期格式
        JsonNode jsonNode2 = node.get(NAME_PRICE);
        // todo
        return true;
    }
}
