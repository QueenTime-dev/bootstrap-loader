package me.naulbimix.bootstrap;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

public class PaperclipAgent {
    public static void premain(String agentArgs, Instrumentation inst) {}

    static void addToClassPath(Path paperJar) {
        try {
            URL url = paperJar.toUri().toURL();
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();

            if (classLoader instanceof URLClassLoader urlClassLoader) {
                // Используем reflection для добавления JAR'а в существующий URLClassLoader
                Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                method.setAccessible(true);
                method.invoke(urlClassLoader, url);
            } else {
                // Если это не URLClassLoader, создаем новый
                URLClassLoader newClassLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());
                Thread.currentThread().setContextClassLoader(newClassLoader);
            }
        } catch (IOException | ReflectiveOperationException e) {
            System.err.println("Unable to add kernel to System ClassLoader");
            e.printStackTrace();
            System.exit(1);
        }
        /*ClassLoader loader = ClassLoader.getSystemClassLoader();
        if (!(loader instanceof java.net.URLClassLoader))
            throw new RuntimeException("System ClassLoader is not URLClassLoader");
        try {
            Method addURL = getAddMethod(loader);
            if (addURL == null) {
                System.err.println("Unable to find method to add Paper jar to System ClassLoader");
                System.exit(1);
            }
            addURL.setAccessible(true);
            addURL.invoke(loader, paperJar.toUri().toURL());
        } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|java.net.MalformedURLException e) {
            System.err.println("Unable to add kernel to System ClassLoader");
            e.printStackTrace();
            System.exit(1);
        }*/
    }

    private static Method getAddMethod(Object o) {
        Class<?> clazz = o.getClass();
        Method m = null;
        while (m == null) {
            try {
                m = clazz.getDeclaredMethod("addURL", URL.class);
            } catch (NoSuchMethodException ignored) {
                clazz = clazz.getSuperclass();
                if (clazz == null)
                    return null;
            }
        }
        return m;
    }
}
