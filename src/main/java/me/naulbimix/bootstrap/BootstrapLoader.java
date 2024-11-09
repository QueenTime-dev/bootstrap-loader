package me.naulbimix.bootstrap;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BootstrapLoader {
    public static void main(String[] args) {
        final Path kernelJar = Paths.get("kernel.jar");
        final String main = PaperclipUtils.getMainClass(kernelJar);
        final Method mainMethod = PaperclipUtils.getMainMethod(kernelJar, main);
        try {
            mainMethod.invoke(null, new Object[] { args });
        } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
            System.err.println("Error while running kernel jar");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
