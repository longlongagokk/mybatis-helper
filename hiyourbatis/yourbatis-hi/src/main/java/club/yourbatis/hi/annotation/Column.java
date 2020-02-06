package club.yourbatis.hi.annotation;

import java.lang.annotation.*;

/**
 * 实体类对应表名
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {
    String value();
}
