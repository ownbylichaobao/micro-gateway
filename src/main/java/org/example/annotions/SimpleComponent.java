package org.example.annotions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 模仿Spring @Component
 * @author lcb
 * @date 2020/5/13
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleComponent {
    String name() default "";
}
