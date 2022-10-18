package net.yury.MinuteStockService.config;

import com.google.inject.Singleton;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.Duration;

@Singleton
public class JedisPoolClient {
    public final JedisPool pool;

    public JedisPoolClient() {
        String redisHost = "10.10.16.50";
        int redisPort = 6001;
        String redisPassword = "root";
        GenericObjectPoolConfig<Jedis> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(0);
        poolConfig.setMaxWait(Duration.ofMillis(100));
        this.pool = new JedisPool(poolConfig, redisHost, redisPort, 10000, redisPassword, 0);
    }
}
