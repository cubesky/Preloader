import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

public class UnloadableClassLoader extends URLClassLoader {
    public UnloadableClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public UnloadableClassLoader(URL[] urls) {
        super(urls);
    }

    public UnloadableClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    static class InsideClassLoader extends ClassLoader {

    }
}
