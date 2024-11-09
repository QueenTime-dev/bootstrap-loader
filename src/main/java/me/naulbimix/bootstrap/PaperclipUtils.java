package me.naulbimix.bootstrap;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarInputStream;

public class PaperclipUtils {
    public static String getMainClass(Path kernelJar) {
        try {
            try (InputStream is = new BufferedInputStream(Files.newInputStream(kernelJar))) {
                try (JarInputStream js = new JarInputStream(is)) {
                    return js.getManifest().getMainAttributes().getValue("Main-Class");
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from kernel jar");
            e.printStackTrace();
            System.exit(1);
            throw new InternalError();
        }
    }

    public static Method getMainMethod(Path kernelJar, String mainClass) {
        PaperclipAgent.addToClassPath(kernelJar);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("libraries"), "*.jar")) {
            for(Path jarFile : stream)
                PaperclipAgent.addToClassPath(jarFile);
        } catch (IOException e) {
            System.err.println("Error while adding files from libraries");
            e.printStackTrace();
            System.exit(1);
            throw new RuntimeException();
        }

        try {
            return Class.forName(mainClass, true, ClassLoader.getSystemClassLoader()).getMethod("main", String[].class);
        } catch (NoSuchMethodException|ClassNotFoundException e) {
            System.err.println("Failed to find main method in kernel jar");
            e.printStackTrace();
            System.exit(1);
            throw new InternalError();
        }
    }
}
