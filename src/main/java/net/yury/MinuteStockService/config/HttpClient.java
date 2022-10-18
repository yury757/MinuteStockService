package net.yury.MinuteStockService.config;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class HttpClient {
    public static final OkHttpClient CLIENT = new OkHttpClient();
    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = CLIENT.newCall(request).execute();
        assert response.body() != null;
        return response.body().string();
    }
}
