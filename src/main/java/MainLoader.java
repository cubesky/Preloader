import preloader.Preloader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;

public class MainLoader {
    private static Preloader loaderInstance = null;
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<URL> urlList = new ArrayList<>();
        Properties prop = new Properties();
        Class<?> mainClazz = null;
        HashMap<String,byte[]> byteClassLoaded = null;
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
                loaderInstance = (Preloader) loaderClazz.newInstance();
                byteClassLoaded = loaderInstance.load();
            }
            if (mainJarAttr.containsKey("Class-Path")) {
                Arrays.stream(mainJarAttr.getValue(" ").split(" ")).forEach(attr -> {
                    try {
                        urlList.add(new File(attr).toURI().toURL());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                });
            }
            urlList.add(new File(mainJar).toURI().toURL());
            URL[] url = new URL[urlList.size()];
            urlList.toArray(url);
            ClassLoader mainJarClassLoader;
            if (byteClassLoaded == null) {
                mainJarClassLoader = new URLClassLoader(
                        url, ClassLoader.getSystemClassLoader());
            } else {
                mainJarClassLoader = new ByteClassLoader(url, ClassLoader.getSystemClassLoader(), byteClassLoaded);
            }
            mainClazz = Class.forName(mainJarAttr.getValue("Main-Class"), true, mainJarClassLoader);
            if (Boolean.parseBoolean(prop.getProperty("useTrappedSecurity", "true"))) {
                ExitSecurityManager.forbidSystemExitCall();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Some file not found.");
            System.exit(1);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            System.err.println("Can not invoke jar.");
            System.exit(1);
        }
        if (Boolean.parseBoolean(prop.getProperty("periodCheck","true"))) checkThread();
        mainClazz.getMethod("main", String[].class).invoke(null, (Object) args);
    }

    private static final ScheduledExecutorService schedular = Executors.newScheduledThreadPool(1, r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
    });

    private static void checkThread() {
        schedular.scheduleAtFixedRate(() -> {
            if(!loaderInstance.check()) {
                System.err.println("System halt.");
                Runtime.getRuntime().halt(0);
            }
        }, 1,1, TimeUnit.HOURS);
    }
}
