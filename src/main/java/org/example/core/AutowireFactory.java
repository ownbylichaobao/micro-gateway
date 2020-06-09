package org.example.core;

import org.example.annotions.SimpleAutowired;
import org.example.annotions.SimpleComponent;
import org.example.entity.BeanDefinition;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 依赖注入工厂类
 *
 * @author lcb
 * @date 2020/5/13
 */
public class AutowireFactory {
    /**
     * bean依赖关系记录
     */
    private static final HashMap<String, Set<BeanDefinition>> supplyAndDemandMap = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(AutowireFactory.class);
    private static final Set<BeanDefinition> beanDefinitionCacheMap = new HashSet<>();

    public void init(String packageName) throws Exception {
        LOGGER.info("开始自动注入流程。。。");
        getSupplyAndDemandMap(packageName);
        loadBean();
    }

    /**
     * 记录bean的供需关系，即优先级关系
     * @param packageName
     */
    public void getSupplyAndDemandMap(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(SimpleComponent.class);
        for (Class<?> clazz : classSet) {
            Field[] fields = clazz.getDeclaredFields();
            Set<BeanDefinition> set = new HashSet<>();
            SimpleComponent simpleComponent = clazz.getAnnotation(SimpleComponent.class);
            String demandBeanName = "".endsWith(simpleComponent.name()) ? clazz.getName() : simpleComponent.name();
            LOGGER.info("扫描到需自动注入的类，className->{},beanName->{}", clazz.getName(), demandBeanName);
            beanDefinitionCacheMap.add(BeanDefinition.of(demandBeanName, clazz.getName()));
            for (Field field : fields) {
                if (field.isAnnotationPresent(SimpleAutowired.class)) {
                    SimpleAutowired simpleAutowired = field.getAnnotation(SimpleAutowired.class);
                    Type type = field.getGenericType();
                    String clazzName = type.toString().split(" ")[1];
                    String beanName = "".equals(simpleAutowired.name()) ?
                            type.toString().split(" ")[1] : simpleAutowired.name();
                    BeanDefinition beanDefinition = new BeanDefinition();
                    beanDefinition.setClassName(clazzName);
                    beanDefinition.setBeanName(beanName);
                    set.add(beanDefinition);
                    LOGGER.info("------- 加载前提依赖 className->{},beanName->{}", clazzName, beanName);
                }
            }
            supplyAndDemandMap.put(demandBeanName, set);
        }
    }

    /**
     * 循环生成bean
     * @throws Exception
     */
    private void loadBean() throws Exception {
        for (BeanDefinition beanDefinition : beanDefinitionCacheMap) {
            loadBeanCore(beanDefinition);
        }
    }

    /**
     * 递归保证依赖优先级
     *
     * @param beanDefinition beanDefinition
     * @throws Exception e
     */
    private void loadBeanCore(BeanDefinition beanDefinition) throws Exception {
        if (!supplyAndDemandMap.containsKey(beanDefinition.getBeanName()) || supplyAndDemandMap.get(beanDefinition.getBeanName()).size() < 1) {
            registerBean(beanDefinition);
            return;
        }
        Set<BeanDefinition> beanDefinitions = supplyAndDemandMap.get(beanDefinition.getBeanName());
        for (BeanDefinition bd : beanDefinitions) {
            if (BeanFactory.getInstance().getBean(bd.getBeanName()) == null) {
                loadBeanCore(beanDefinition);
            }
        }
        registerBean(beanDefinition);
    }

    /**
     * 向BeanFactory即IOC容器中注册bean
     * @param beanDefinition
     * @throws Exception
     */
    private void registerBean(BeanDefinition beanDefinition) throws Exception {
        Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(beanDefinition.getClassName());
        Object bean = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        Map<String, Object> fieldMap = new HashMap<>();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(SimpleAutowired.class)) {
                SimpleAutowired simpleAutowired = field.getAnnotation(SimpleAutowired.class);
                Type type = field.getGenericType();
                String beanName = "".equals(simpleAutowired.name()) ?
                        type.toString().split(" ")[1] : simpleAutowired.name();
                Object fieldBean = BeanFactory.getInstance().getBean(beanName);
                field.set(bean, fieldBean);
                fieldMap.put(field.getName(), fieldBean);
            } else {
                fieldMap.put(field.getName(), field.get(bean));
            }
        }
        BeanFactory.getInstance().registry(beanDefinition.getBeanName(), beanDefinition.getClassName(), bean, fieldMap);
        LOGGER.info("bean ->{} 加载成功", beanDefinition.getBeanName());
    }
}
