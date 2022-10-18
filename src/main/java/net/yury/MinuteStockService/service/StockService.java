package net.yury.MinuteStockService.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.lettuce.core.internal.LettuceClassUtils;
import net.yury.MinuteStockService.config.HttpClient;
import net.yury.MinuteStockService.config.JedisPoolClient;
import net.yury.MinuteStockService.config.KafkaClientConsumer;
import net.yury.MinuteStockService.config.LettuceClient;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class StockService {
    @Inject
    private LettuceClient lettuceClient;

    public final static String URL = "http://localhost:8080/stockserver/api/stock/v1/get?stockCode=%s&marketId=%s";

    public Map<String, Object> stockMinute(String stockCode, String marketID) {
        if (stockCode == null || marketID == null) return null;
        String key1 = KafkaService.REDIS_KEY_PREFIX + KafkaService.REDIS_KEY_DELIMITER
                + stockCode + KafkaService.REDIS_KEY_DELIMITER
                + marketID;
        Set<String> dates = lettuceClient.connection.sync().smembers(key1);
        if (dates == null) return null;
        List<String> dateList = new ArrayList<>(dates);
        dateList.sort(String::compareTo);
        List<Double> list = new ArrayList<>();
        for (String date : dateList) {
            String key2 = key1 + KafkaService.REDIS_KEY_DELIMITER + date;
            Map<String, String> map = lettuceClient.connection.sync().hgetall(key2);
            map.entrySet().stream().sorted(Map.Entry.comparingByKey())
                    .forEach(item -> list.add(Double.valueOf(item.getValue())));
        }
        Map<String, Object> res = new HashMap<>();
        res.put("stockName", getStockName(stockCode, marketID));
        res.put("list", list);
        return res;
    }

    public String getStockName(String stockCode, String marketID) {
        String url = String.format(URL, stockCode, marketID);
        String s = null;
        try {
            s = HttpClient.get(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }
}
