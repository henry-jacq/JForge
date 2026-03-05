package jdi.container;

import jdi.annotations.*;
import jdi.scanner.ClassScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public class ApplicationContext {

    private BeanFactory beanFactory = new BeanFactory();

    public ApplicationContext(String basePackage) throws Exception {
        List<Class<?>> classes = ClassScanner.scan(basePackage);

        // Register all component classes first
        for (Class<?> clazz : classes) {
            if (isComponent(clazz)) {
                beanFactory.registerBeanDefinition(clazz);
                
                // Also register by interfaces it implements
                for (Class<?> interfaceClass : clazz.getInterfaces()) {
                    beanFactory.registerInterfaceImplementation(interfaceClass, clazz);
                }
            }
        }

        // Create all beans (this will handle dependencies properly)
        for (Class<?> clazz : classes) {
            if (isComponent(clazz)) {
                getOrCreateBean(clazz);
            }
        }

        performFieldInjection();
    }

    private boolean isComponent(Class<?> clazz) {
        return clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Service.class);
    }

    private void performFieldInjection() throws Exception {
        for (Object bean: beanFactory.getAllBeans()) {
            Field[] fields = bean.getClass().getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(Inject.class)) {
                    Object dependency = resolveDependency(field.getType());
                    if (dependency != null) {
                        field.setAccessible(true);
                        field.set(bean, dependency);
                    }
                }
            }
        }
    }

    public <T> T getBean(Class<T> clazz) {
        Object bean = resolveDependency(clazz);
        return clazz.cast(bean);
    }
    
    private Object resolveDependency(Class<?> clazz) {
        // First try to get bean directly
        Object bean = beanFactory.getBean(clazz);
        if (bean != null) {
            return bean;
        }
        
        // If it's an interface, try to find an implementation
        if (clazz.isInterface()) {
            Class<?> implementation = beanFactory.getImplementation(clazz);
            if (implementation != null) {
                return beanFactory.getBean(implementation);
            }
        }
        
        return null;
    }

    private Object createBean(Class<?> clazz) throws Exception {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        // Look for @Inject annotated constructor first
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                Class<?>[] paramTypes = constructor.getParameterTypes();
                Object[] dependencies = new Object[paramTypes.length];

                for (int i = 0; i < paramTypes.length; i++) {
                    dependencies[i] = resolveDependency(paramTypes[i]);
                    if (dependencies[i] == null) {
                        dependencies[i] = getOrCreateBean(paramTypes[i]);
                    }
                }

                return constructor.newInstance(dependencies);
            }
        }

        // If no @Inject constructor, try default constructor
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            // If no default constructor, use the first constructor and try to inject
            Constructor<?> constructor = constructors[0];
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] dependencies = new Object[paramTypes.length];

            for (int i = 0; i < paramTypes.length; i++) {
                dependencies[i] = resolveDependency(paramTypes[i]);
                if (dependencies[i] == null) {
                    dependencies[i] = getOrCreateBean(paramTypes[i]);
                }
            }

            return constructor.newInstance(dependencies);
        }
    }

    private Object getOrCreateBean(Class<?> clazz) throws Exception {
        // Check if bean already exists
        Object existing = beanFactory.getBean(clazz);
        if (existing != null) {
            return existing;
        }
        
        // If it's an interface, try to find an implementation
        if (clazz.isInterface()) {
            Class<?> implementation = beanFactory.getImplementation(clazz);
            if (implementation != null) {
                return getOrCreateBean(implementation);
            }
            throw new RuntimeException("No implementation found for interface: " + clazz.getName());
        }
        
        // Check if it's a registered component
        if (!beanFactory.hasBeanDefinition(clazz)) {
            throw new RuntimeException("No bean definition found for class: " + clazz.getName());
        }
        
        // Create the bean
        Object instance = createBean(clazz);
        beanFactory.registerBean(clazz, instance);
        return instance;
    }
}
