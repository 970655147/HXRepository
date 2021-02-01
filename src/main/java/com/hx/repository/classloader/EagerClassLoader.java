package com.hx.repository.classloader;

import com.hx.log.util.Tools;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * EagerClassLoader
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-02-01 14:09
 */
public class EagerClassLoader extends ClassLoader {

    /** path */
    private String[] pathes;
    /** classNames 列表 */
    private Map<String, String> className2Path;
    /** classNames -> java.lang.Class */
    private Map<String, Class> className2Class;

    // EagerClassLoader
    public EagerClassLoader(String path, ClassLoader parent) {
        this(new String[]{path}, parent);
    }

    public EagerClassLoader(String[] pathes, ClassLoader parent) {
        super(parent);
        this.pathes = pathes;
        this.className2Path = new HashMap<>();
        this.className2Class = new HashMap<>();
        init();
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (className2Class.containsKey(name)) {
            return className2Class.get(name);
        }

        String path = className2Path.get(name);
        boolean pathOrJar = isPathOrJar(path);

        if (path == null) {
            return getParent().loadClass(name);
        }
        if (pathOrJar) {
            return loadWithFile(new File(path), name);
        } else {
            return loadWithJar(new File(path), name);
        }
    }

    /**
     * eager 加载所有的类
     *
     * @return void
     * @author Jerry.X.He
     * @date 2021-02-01 14:13
     */
    public void init() {
        for (String path : pathes) {
            boolean pathOrJar = isPathOrJar(path);
            List<String> classes = new ArrayList<>();
            File parent = new File(path);

            if (pathOrJar) {
                initWithFile(parent, classes);
            } else {
                initWithJar(parent, classes);
            }
        }
    }

    // ----------------------------------------- 辅助方法 -----------------------------------------

    /**
     * 判断当前路径是 path 还是 jar
     *
     * @param path path
     * @return boolean
     * @author Jerry.X.He
     * @date 2021-02-01 16:13
     */
    private boolean isPathOrJar(String path) {
        if (path == null) {
            return true;
        }

        return !path.endsWith(".jar");
    }

    /**
     * 根据 文件 加载所有的 classes
     *
     * @param parent  parent
     * @param classes classes
     * @return void
     * @author Jerry.X.He
     * @date 2021-02-01 15:34
     */
    private List<Class> initWithFile(File parent, List<String> classes) {
        LinkedList packageList = new LinkedList();
        listAllClassesInFile(parent, classes, packageList);
        for (String className : classes) {
            className2Path.put(className, parent.getAbsolutePath());
        }

        List<Class> result = new ArrayList<>();
        for (String className : classes) {
            if (className2Class.containsKey(className)) {
                continue;
            }

            try {
                try {
                    Class clazz = this.getParent().loadClass(className);
                    if (clazz != null) {
                        result.add(clazz);
                        className2Class.put(className, clazz);
                        continue;
                    }
                } catch (Throwable e) {
                    // ignore
                }

                String path = className.replace('.', '/').concat(Tools.CLASS);
                File classFile = new File(parent, path);
                byte[] bytes = allBytes(classFile);
                Class clazz = defineClass(className, bytes, 0, bytes.length);
                result.add(clazz);
                className2Class.put(className, clazz);
            } catch (Throwable e) {
                // ignore
            }
        }
        return result;
    }

    /**
     * 根据 jar 加载所有的 classes
     *
     * @param parent  parent
     * @param classes classes
     * @return void
     * @author Jerry.X.He
     * @date 2021-02-01 15:34
     */
    private List<Class> initWithJar(File parent, List<String> classes) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(parent);
            listAllClassesInJar(zipFile, classes);
            for (String className : classes) {
                className2Path.put(className, parent.getAbsolutePath());
            }
        } catch (Throwable e) {
            return Collections.emptyList();
        }

        List<Class> result = new ArrayList<>();
        for (String className : classes) {
            if (className2Class.containsKey(className)) {
                continue;
            }

            try {
                try {
                    Class clazz = this.getParent().loadClass(className);
                    if (clazz != null) {
                        result.add(clazz);
                        className2Class.put(className, clazz);
                        continue;
                    }
                } catch (Throwable e) {
                    // ignore
                }

                String path = className.replace('.', '/').concat(Tools.CLASS);
                ZipEntry zipEntry = zipFile.getEntry(path);
                byte[] bytes = allBytes(zipFile.getInputStream(zipEntry));
                Class clazz = defineClass(className, bytes, 0, bytes.length);
                result.add(clazz);
                className2Class.put(className, clazz);
            } catch (Throwable e) {
                // ignore
            }
        }
        return result;
    }

    /**
     * 冲给定的文件中 加载给定的 class
     *
     * @param parent    parent
     * @param className className
     * @return java.lang.Class
     * @author Jerry.X.He
     * @date 2021-02-01 16:19
     */
    private Class loadWithFile(File parent, String className) {
        try {
            try {
                Class clazz = this.getParent().loadClass(className);
                if (clazz != null) {
                    return clazz;
                }
            } catch (Throwable e) {
                // ignore
            }

            String path = className.replace('.', '/').concat(Tools.CLASS);
            File classFile = new File(parent, path);
            byte[] bytes = allBytes(classFile);
            return defineClass(className, bytes, 0, bytes.length);
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 从给定的 jar 中加载给定的 class
     *
     * @param parent    parent
     * @param className className
     * @return java.lang.Class
     * @author Jerry.X.He
     * @date 2021-02-01 16:22
     */
    private Class loadWithJar(File parent, String className) {
        try {
            ZipFile zipFile = new ZipFile(parent);
            try {
                Class clazz = this.getParent().loadClass(className);
                if (clazz != null) {
                    return clazz;
                }
            } catch (Throwable e) {
                // ignore
            }

            String path = className.replace('.', '/').concat(Tools.CLASS);
            ZipEntry zipEntry = zipFile.getEntry(path);
            byte[] bytes = allBytes(zipFile.getInputStream(zipEntry));
            return defineClass(className, bytes, 0, bytes.length);
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 加载给定的 file 下面的所有的 class
     *
     * @param file file
     * @return java.util.List<java.lang.String>
     * @author Jerry.X.He
     * @date 2021-02-01 14:19
     */
    private void listAllClassesInFile(File file, List<String> classes, LinkedList<String> packageList) {
        if (file == null) {
            return;
        }
        if (!file.exists()) {
            return;
        }

        String packageName = StringUtils.join(packageList, ".");
        for (File childFile : file.listFiles()) {
            if (childFile.isDirectory()) {
                packageList.addLast(childFile.getName());
                listAllClassesInFile(childFile, classes, packageList);
                packageList.removeLast();
                continue;
            }

            if (childFile.getName().endsWith(Tools.CLASS)) {
                String clazzFileName = childFile.getName();
                String trimmedClassName = clazzFileName.substring(0, clazzFileName.length() - Tools.CLASS.length());
                classes.add(packageName + "." + trimmedClassName);
            }
        }
    }

    /**
     * 加载给定的 jar 下面的所有的 class
     *
     * @param zipFile zipFile
     * @param classes classes
     * @return java.util.List<java.lang.String>
     * @author Jerry.X.He
     * @date 2021-02-01 15:15
     */
    private void listAllClassesInJar(ZipFile zipFile, List<String> classes) {
        try {
            Enumeration<? extends ZipEntry> ite = zipFile.entries();
            while (ite.hasMoreElements()) {
                ZipEntry entry = ite.nextElement();
                if (entry.getName().endsWith(Tools.CLASS)) {
                    String clazzFileName = entry.getName();
                    String trimmedClassName = clazzFileName.substring(0, clazzFileName.length() - Tools.CLASS.length());
                    classes.add(trimmedClassName.replaceAll("/", "."));
                }
            }
        } catch (Throwable e) {
            // ignore
        }
    }

    /**
     * 获取给定的文件的所有的 字节数据
     *
     * @param is is
     * @return byte[]
     * @author Jerry.X.He
     * @date 2021-02-01 15:08
     */
    private byte[] allBytes(InputStream is) throws Throwable {
        try (BufferedInputStream bis = new BufferedInputStream(is)) {
            byte[] buffer = new byte[2048];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int readLen = 0;
            while ((readLen = bis.read(buffer, 0, buffer.length)) > 0) {
                baos.write(buffer, 0, readLen);
            }
            return baos.toByteArray();
        }
    }

    private byte[] allBytes(File file) throws Throwable {
        return allBytes(new FileInputStream(file));
    }


}
