package net.yury.MinuteStockService.config;

import com.fasterxml.jackson.databind.ObjectMapper;

public enum JsonUtil {
    INSTANCE;

    public ObjectMapper mapper;
    private JsonUtil() {
        this.mapper = new ObjectMapper();
    }
}
