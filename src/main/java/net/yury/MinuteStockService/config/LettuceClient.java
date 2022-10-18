package net.yury.MinuteStockService.config;

import com.google.inject.Singleton;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;

@Singleton
public class LettuceClient {
    public final StatefulRedisConnection<String, String> connection;

    public LettuceClient() {
        RedisClient redisClient = RedisClient.create("redis://root@10.10.16.50:6001");
        connection = redisClient.connect();
    }
}
