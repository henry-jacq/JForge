package jdi.scanner;

import java.io.File;
import java.util.*;

public class ClassScanner {
    public static List<Class<?>> scan(String packageName) throws Exception {

        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File directory = new File(classLoader.getResource(path).getFile());

        for (File file : directory.listFiles()) {
            if (file.getName().endsWith(".class")) {
                String className = packageName + "." +
                        file.getName().replace(".class", "");
                classes.add(Class.forName(className));
            }
        }

        return classes;
    }
}
