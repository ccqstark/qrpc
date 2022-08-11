package io.github.ccqstark.qrpc.common.extension;

import io.github.ccqstark.qrpc.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ccqstark
 * @description SPI机制的服务实现加载器
 * @date 2022/6/15 22:39
 */
@Slf4j
public final class ExtensionLoader<T> {

    /**
     * 配置文件所在目录路径
     */
    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";

    /**
     * 加载器缓存，一个模块对应一个ExtensionLoader
     */
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();

    /**
     * 具体实现类的实例缓存（Class对象作为key，相当于一级缓存）
     */
    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    /**
     * 该加载器对应的模块
     */
    private Class<?> type;

    /**
     * 具体实现类的 Class 对象缓存
     */
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    /**
     * 具体实现类的实例缓存（别称作为key，相当于二级缓存）
     */
    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();

    /**
     * 私有的类构造器
     * @param type 模块的Class
     */
    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    /**
     * 获取指定模块的ExtensionLoader
     * @param type 模块的Class
     */
    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type) {
        // 对应模块为空
        if (type == null) {
            throw new IllegalArgumentException("Extension type should not be null.");
        }
        // type不为接口
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type must be an interface.");
        }
        // 对应接口没有加上@SPI注解
        if (type.getAnnotation(SPI.class) == null) {
            throw new IllegalArgumentException("Extension type must be annotated by @SPI");
        }
        // 先获取缓存，如果拿不到再创建
        ExtensionLoader<S> extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        if (extensionLoader == null) {
            // 这里相当于是双重校验锁，因为putIfAbsent是ConcurrentHashMap的线程安全方法
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<S>(type));
            extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        }
        return extensionLoader;
    }

    /**
     * 获取当前模块的指定具体实现实例
     * @param name 具体实现的别名，在配置文件中为key，value是实现的全限定类名
     * @return
     */
    public T getExtension(String name) {
        // 判空
        if (StringUtil.isBlank(name)) {
            throw new IllegalArgumentException("Extension name should not be null or empty.");
        }
        // 先获取缓存，如果拿不到再创建
        Holder<Object> holder = cachedInstances.get(name);
        if (holder == null) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        // 如果实例不存在，则以单例模式进行创建
        Object instance = holder.get();
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    // 创建实现类实例并放入holder
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    /**
     * 创建具体实现类实例
     * @param name 具体实现的别名，在配置文件中为key，value是实现的全限定类名
     * @return
     */
    private T createExtension(String name) {
        // 通过别名获取实现类的Class对象
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw new RuntimeException("No such extension of name " + name);
        }
        // 从缓存中获取实例
        T instance = (T) EXTENSION_INSTANCES.get(clazz);
        // 若缓存未命中
        if (instance == null) {
            try {
                // 实例化并存入缓存
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return instance;
    }

    /**
     * 获取实现类的Class缓存Map
     * @return 缓存Map
     */
    private Map<String, Class<?>> getExtensionClasses() {
        // 从holder中拿出缓存map，如果map为空则在下一步去目录文件中加载
        Map<String, Class<?>> classes = cachedClasses.get();
        // 双重校验锁
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    classes = new HashMap<>();
                    // 从目录中去加载
                    loadDirectory(classes);
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    /**
     * 加载配置文件夹
     * @param extensionClasses
     */
    private void loadDirectory(Map<String, Class<?>> extensionClasses) {
        // 获取模块对应的配置文件名（META-INF/extensions/模块接口全限定名）
        String fileName = ExtensionLoader.SERVICE_DIRECTORY + type.getName();
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            // 获取配置文件的url列表
            urls = classLoader.getResources(fileName);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    // 加载配置文件
                    loadResource(extensionClasses, classLoader, resourceUrl);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 加载配置文件
     * @param extensionClasses 实现类 Class 缓存
     * @param classLoader 类加载器
     * @param resourceUrl 配置文件的资源url
     */
    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL resourceUrl) {
        // 用资源url读取配置文件的内容
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), StandardCharsets.UTF_8))) {
            String line;
            // 逐行读取
            while ((line = reader.readLine()) != null) {
                // 获取#号下标，#号后面是注释，要忽略掉
                final int ci = line.indexOf('#');
                if (ci >= 0) {
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if (line.length() > 0) {
                    try {
                        // key 和 value 是用等号分隔的
                        final int ei = line.indexOf('=');
                        // key为实现类的别称
                        String name = line.substring(0, ei).trim();
                        // value是具体实现类的全限定名称
                        String clazzName = line.substring(ei + 1).trim();
                        // 本项目的SPI使用键值对，所以他们两者都不能为空
                        if (name.length() > 0 && clazzName.length() > 0) {
                            // 使用类加载器加载出对应的 Class 对象，并放入缓存中
                            Class<?> clazz = classLoader.loadClass(clazzName);
                            extensionClasses.put(name, clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
