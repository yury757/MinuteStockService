package net.yury.MinuteStockService;

import net.yury.MinuteStockService.config.GuiceInjection;
import net.yury.MinuteStockService.config.simplehttp.SimpleHttpServer;
import net.yury.MinuteStockService.service.KafkaService;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        KafkaService kafkaService = GuiceInjection.injector.getInstance(KafkaService.class);
        kafkaService.run();
        SimpleHttpServer.start();
    }
}
