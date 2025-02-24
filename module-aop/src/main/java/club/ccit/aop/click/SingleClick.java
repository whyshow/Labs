package club.ccit.aop.click;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author swzhang3
 * name: PreventDoubleClick
 * date: 2024/6/19 12:49
 * description: 防连点
 **/
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SingleClick {
    /**
     * 设置延迟时间
     *
     * @return 默认3000ms
     */
    long value() default 3000;
}
