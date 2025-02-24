package club.ccit.aop.time;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author swzhang3
 * name: ThreadWait
 * date: 2024/6/19 12:52
 * description: 等待一段时间后再执行后续
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
public @interface WaitTime {
    /**
     * 设置延迟时间
     *
     * @return 默认3000ms
     */
    long value() default 3000;
}
