package net.yury.MinuteStockService.config.simplehttp;

import net.yury.MinuteStockService.config.GuiceInjection;

public class SimpleHttpServer {

    public static void start() throws InterruptedException {
        final Controller controller = GuiceInjection.injector.getInstance(Controller.class);
        new Thread(() -> {
            try {
                new SHServer(8080, controller).start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
