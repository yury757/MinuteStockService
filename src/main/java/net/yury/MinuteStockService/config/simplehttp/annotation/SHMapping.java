package net.yury.MinuteStockService.config.simplehttp.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SHMapping {
    public String path();
}
