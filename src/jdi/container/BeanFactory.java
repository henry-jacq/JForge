package jdi.container;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BeanFactory {
    private Map<Class<?>, Object> singletonBeans = new HashMap<>();
    private Set<Class<?>> beanDefinitions = new HashSet<>();
    private Map<Class<?>, Class<?>> interfaceImplementations = new HashMap<>();

    public void registerBeanDefinition(Class<?> clazz) {
        beanDefinitions.add(clazz);
    }

    public void registerBean(Class<?> clazz, Object instance) {
        singletonBeans.put(clazz, instance);
    }

    public void registerInterfaceImplementation(Class<?> interfaceClass, Class<?> implementationClass) {
        interfaceImplementations.put(interfaceClass, implementationClass);
    }

    public Object getBean(Class<?> clazz) {
        return singletonBeans.get(clazz);
    }

    public Class<?> getImplementation(Class<?> interfaceClass) {
        return interfaceImplementations.get(interfaceClass);
    }

    public Collection<Object> getAllBeans() {
        return singletonBeans.values();
    }

    public boolean hasBeanDefinition(Class<?> clazz) {
        return beanDefinitions.contains(clazz);
    }
}
