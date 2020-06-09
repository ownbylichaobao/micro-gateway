package org.example.annotions;

import java.lang.annotation.*;

/**
 * 模仿Spring @Bean
 * @author lcb
 * @date 2020/5/13
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SimpleBean {
    String name() default "";
}
