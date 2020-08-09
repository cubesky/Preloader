import preloader.Preloader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;

public class MainLoader {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Properties prop = new Properties();
        Class<?> mainClazz = null;
        try {
            prop.load(Files.newInputStream(new File("loader.properties").toPath()));
            String mainJar = prop.getProperty("jar");
            if (mainJar == null) {
                System.err.println("Properties not define: jar");
                System.exit(1);
            }
            String loaderJar = prop.getProperty("loader", "");
            JarInputStream mainJarStream = new JarInputStream(Files.newInputStream(new File(mainJar).toPath()));
            Attributes mainJarAttr = mainJarStream.getManifest().getMainAttributes();
            if (!loaderJar.isEmpty()) {
                UnloadableClassLoader.InsideClassLoader sideloadClassLoader = new UnloadableClassLoader.InsideClassLoader();
                UnloadableClassLoader loaderClassLoader = new UnloadableClassLoader(
                        new URL[]{new File(loaderJar).toURI().toURL()}, sideloadClassLoader);
                Class<?> loaderClazz = Class.forName("loader.Preloader", true, loaderClassLoader);
                Preloader loaderInstance = (Preloader) loaderClazz.newInstance();
                new ByteClassLoader(new URL[]{}, ClassLoader.getSystemClassLoader(), loaderInstance.load());
                loaderClazz = null;
                loaderClassLoader = null;
                loaderInstance = null;
                sideloadClassLoader = null;
                System.gc();
            }
            if (mainJarAttr.containsKey("Class-Path")) {
                new URLClassLoader((URL[]) Arrays.stream(mainJarAttr.getValue("Class-Path").split(" ")).map(a -> {
                    try {
                        return new File(a).toURI().toURL();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).toArray(), ClassLoader.getSystemClassLoader());
            }
            URLClassLoader mainJarClassLoader = new URLClassLoader(
                    new URL[]{new File(mainJar).toURI().toURL()}, ClassLoader.getSystemClassLoader());
            mainClazz = Class.forName(mainJarAttr.getValue("Main-Class"), true, mainJarClassLoader);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Some file not found.");
            System.exit(1);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            System.err.println("Can not invoke jar.");
            System.exit(1);
        }
        mainClazz.getMethod("main", String[].class).invoke(null, (Object) args);
    }
}
