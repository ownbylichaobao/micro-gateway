package org.example.entity;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 这里的注解使用的是lombok，使用后我们可以不用显示
 * 的写出Getter/Setter以及构造方法，代码更加简洁
 * @author lcb
 * @date 2020/5/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
public class BeanDefinition {
    /**
     * 默认为类的全限定名 如com.test.Student
     * 当@SimpleBean注解指定name时，使用@SimpleBean的name属性值作为beanName
     * 全局唯一
     */
    @NonNull
    private String beanName;
    @NonNull
    private String className;
    private Object bean;
    private Map<String ,Object> propertyMap;
}
