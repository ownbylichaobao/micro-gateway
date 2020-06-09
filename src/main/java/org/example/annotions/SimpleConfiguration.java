package org.example.annotions;

import java.lang.annotation.*;

/**
 * 模仿Spring @Configuration
 * @author lcb
 * @date 2020/5/13
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SimpleConfiguration {
    String[] value() default {};
}
