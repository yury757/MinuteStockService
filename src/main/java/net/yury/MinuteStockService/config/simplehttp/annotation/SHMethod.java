package net.yury.MinuteStockService.config.simplehttp.annotation;

public @interface SHMethod {
    public String name() default "GET";
}
