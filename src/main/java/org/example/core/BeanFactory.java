package org.example.core;

import org.example.annotions.SimpleAutowired;
import org.example.annotions.SimpleBean;
import org.example.annotions.SimpleConfiguration;
import org.example.entity.BeanDefinition;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * bean生成、缓存工厂类
 * @author lcb
 * @date 2020/5/13
 */
public class BeanFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanFactory.class);
    private BeanFactory() {

    }

    /**
     * 内部静态类保证全局唯一
     */
    private static class BeanFactorySingle {
        private static BeanFactory beanFactory = new BeanFactory();
    }

    /**
     * beanCacheMap缓存所有的bean对象
     */
    private static Map<String, BeanDefinition> beanCacheMap = new HashMap<>();

    /**
     * bean的初始化，扫描所有带有@SimpleConfiguration的类，
     * 并利用反射调用其中被@SimpleBean注解标记的方法完成bean的生成。
     * @param packageName 要扫描的包名
     * @throws Exception
     */
    public void init(String packageName) throws Exception {
        LOGGER.info("开始加载bean,basePackage->{}",packageName);
        if (beanCacheMap.size() == 0) {
            Reflections reflections = new Reflections(packageName);
            Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(SimpleConfiguration.class);
            for (Class<?> clazz : classSet) {
                LOGGER.info("加载配置类 ->{}",clazz.getName());
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(SimpleBean.class)) {
                        Type type = method.getGenericReturnType();

                        SimpleBean simpleBean = method.getAnnotation(SimpleBean.class);
                        String className = type.toString().split(" ")[1];
                        LOGGER.info("--------生成Bean 类名->{}",className);
                        Object o = clazz.newInstance();
                        Object bean = method.invoke(o);
                        String beanName = "".equals(simpleBean.name()) ? className : simpleBean.name();
                        registry(beanName, className, bean, null);
                    }
                }
            }
        }
        new AutowireFactory().init(packageName);
    }
    public static BeanFactory getInstance(){
        return BeanFactorySingle.beanFactory;
    }

    /**
     * 提供给框架一个根据beanName取对应的bean的方法，
     * 如果beanCacheMap中不存在则返回错误。
     * 若bean未初始化则进行相应的初始化过程（此种情况在我们本次设计的运行中不会出现）
     * @param beanName bean名称
     * @return
     * @throws Exception
     */
    public Object getBean(String beanName) throws Exception {
        BeanDefinition beanDefinition = beanCacheMap.get(beanName);
        if (beanDefinition == null) {
            throw new Exception("No beans found which name is " + beanName);
        }

        Object bean = beanDefinition.getBean();
        if (bean == null) {
            LOGGER.info("bean beanName->{} not init,now exec the doCreate method",beanName);
            bean = doCreate(beanDefinition);
        }
        return bean;
    }

    private Object doCreate(BeanDefinition beanDefinition) throws Exception {
        Object bean = ClassLoader.getSystemClassLoader().loadClass(beanDefinition.getClassName()).newInstance();
        return addProperty(bean, beanDefinition);
    }

    /**
     * 属性值注入过程
     * @param bean 被注入的bean对象
     * @param beanDefinition
     * @return
     * @throws Exception
     */
    private Object addProperty(Object bean, BeanDefinition beanDefinition) throws Exception {
        Map<String, Object> map = beanDefinition.getPropertyMap();
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field declaredField : fields) {
            declaredField.setAccessible(true);
            declaredField.set(bean, map.get(declaredField.getName()));
        }
        return bean;
    }

    /**
     * bean注册，即 将生成的bean放入beanCacheMap中
     * @param beanName
     * @param className
     * @param bean
     * @param propertyMap
     * @throws Exception
     */
    void registry(String beanName, String className, Object bean, Map<String, Object> propertyMap) throws Exception {
        if (beanCacheMap.containsKey(beanName)) {
            throw new Exception("重复注册bean beanName->" + beanName);
        }
        beanCacheMap.put(beanName, new BeanDefinition(beanName,className, bean, propertyMap));
    }
}
