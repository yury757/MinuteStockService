package net.yury.MinuteStockService.config.simplehttp;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.yury.MinuteStockService.config.GuiceInjection;
import net.yury.MinuteStockService.config.LettuceClient;
import net.yury.MinuteStockService.config.simplehttp.annotation.SHController;
import net.yury.MinuteStockService.config.simplehttp.annotation.SHMapping;
import net.yury.MinuteStockService.service.StockService;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class Controller implements SHController {
    @Inject
    private StockService stockService;

    // http://localhost:8080/api/stockdata/v1/list?stockCode=300033&marketId=33
    @SHMapping(path = "/api/stockdata/v1/list")
    public Map<String, Object> stockMinute(String content) {
        String[] arr = content.split("&");
        Map<String, Object> map = new HashMap<>();
        String stockCode = null;
        String marketId = null;
        for (String array : arr) {
            String[] inner = array.split("=");
            switch (inner[0]) {
                case "stockCode":
                    stockCode = inner[1];
                    break;
                case "marketId":
                    marketId = inner[1];
                    break;
            }
        }
        if (stockCode == null || marketId == null) {
            throw new RuntimeException("param stockCode or marketId not exists");
        }
        Map<String, Object> res;
        try {
            Map<String, Object> data = this.stockService.stockMinute(stockCode, marketId);
            res = put(null, data);
        }catch (Exception ex) {
            res = put(ex, null);
        }
        return res;
    }

    @SHMapping(path = "/stockserver/api/stock/v1/get")
    public String getName(String content) {
        String[] arr = content.split("&");
        Map<String, Object> map = new HashMap<>();
        String stockCode = null;
        String marketId = null;
        for (String array : arr) {
            String[] inner = array.split("=");
            switch (inner[0]) {
                case "stockCode":
                    stockCode = inner[1];
                    break;
                case "marketId":
                    marketId = inner[1];
                    break;
            }
        }
        if (stockCode == null || marketId == null) {
            throw new RuntimeException("param stockCode or marketId not exists");
        }
        return stockCode + "-" + marketId;
    }

    /**
     * 封装消息
     * @param ex
     * @param data
     * @return
     */
    public Map<String, Object> put(Throwable ex, Object data) {
        Map<String, Object> res = new HashMap<>(3);
        if (ex == null) {
            res.put("code", 0);
            res.put("massage", "成功");
            res.put("data", data);
        }else {
            res.put("code", -1);
            res.put("massage", ex.getMessage());
            res.put("data", null);
        }
        return res;
    }

}
