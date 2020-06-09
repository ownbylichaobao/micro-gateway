package org.example.annotions;

import java.lang.annotation.*;

/**
 * 模仿Spring @Autowired
 * @author lcb
 * @date 2020/5/13
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SimpleAutowired {
    String name() default "";
}
