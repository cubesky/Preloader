# PreLoader
Preloader 是一个可以在被配置的 jar 文件加载运行之前注入加载其他类的预加载器。

# 配置文件

Preloader 将会自动加载运行当前目录下的 `loader.properties` 文件作为配置。

jar 条目定义主程序 jar 的位置和文件名。

loader 条目定义加载器 jar 的位置和文件名。

```properties
jar: xxx.jar
loader: xxx.jar
```

# Loader Jar
加载器 jar 必须以 `compileOnly` 方式引入 Preloader

新建 `loader.Preloader` 类继承 `preloader.Preloader` 类并实现其中的 `load` 方法，返回一个 HashMap 以字节方式传递需要加载的类。

这些类将会被加载到系统 ClassLoader 上。