package com.hx.repository.utils;

import com.hx.common.util.AssertUtils;
import com.hx.log.util.Tools;
import com.hx.repository.classloader.EagerClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static com.hx.log.log.LogPatternUtils.formatLogInfoWithIdx;

/**
 * ClassInfoUtils
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-17 10:43
 */
public final class ClassUtils {

    /** 原始类型的 class */
    private static List<Class> PRIMITIVE_CLASSES = new ArrayList<>();
    /** 原始类型包装类型的 class */
    private static List<Class> PRIMITIVE_WRAPPER_CLASSES = new ArrayList<>();
    /** 字符串类型的 class */
    private static List<Class> STRING_CLASSES = new ArrayList<>();

    static {
        PRIMITIVE_CLASSES.add(boolean.class);
        PRIMITIVE_CLASSES.add(byte.class);
        PRIMITIVE_CLASSES.add(short.class);
        PRIMITIVE_CLASSES.add(char.class);
        PRIMITIVE_CLASSES.add(int.class);
        PRIMITIVE_CLASSES.add(long.class);
        PRIMITIVE_CLASSES.add(float.class);
        PRIMITIVE_CLASSES.add(double.class);

        PRIMITIVE_WRAPPER_CLASSES.add(Boolean.class);
        PRIMITIVE_WRAPPER_CLASSES.add(Byte.class);
        PRIMITIVE_WRAPPER_CLASSES.add(Short.class);
        PRIMITIVE_WRAPPER_CLASSES.add(Character.class);
        PRIMITIVE_WRAPPER_CLASSES.add(Integer.class);
        PRIMITIVE_WRAPPER_CLASSES.add(Long.class);
        PRIMITIVE_WRAPPER_CLASSES.add(Float.class);
        PRIMITIVE_WRAPPER_CLASSES.add(Double.class);

        STRING_CLASSES.add(String.class);
        STRING_CLASSES.add(StringBuilder.class);
        STRING_CLASSES.add(StringBuffer.class);
    }

    // disable constructor
    private ClassUtils() {
        Tools.assert0("can't instantiate !");
    }

    /**
     * 判断给定的 clazz 是否是原始类型
     *
     * @param clazz clazz
     * @return boolean
     * @author Jerry.X.He
     * @date 2021-01-20 20:23
     */
    public static boolean isPrimitiveClass(Class clazz) {
        return clazz.isPrimitive();
    }

    /**
     * 判断给定的 clazz 是否是原始类型的包装类
     *
     * @param clazz clazz
     * @return boolean
     * @author Jerry.X.He
     * @date 2021-01-20 20:23
     */
    public static boolean isPrimitiveWrapperClass(Class clazz) {
        return PRIMITIVE_WRAPPER_CLASSES.contains(clazz);
    }

    /**
     * 判断给定的 clazz 是否是 原始类型 或者 原始类型的包装类
     *
     * @param clazz clazz
     * @return boolean
     * @author Jerry.X.He
     * @date 2021-01-20 20:24
     */
    public static boolean isPrimitiveOrWrapperClass(Class clazz) {
        return isPrimitiveClass(clazz) || isPrimitiveWrapperClass(clazz);
    }

    /**
     * primitiveClass2Wrapper
     *
     * @param clazz clazz
     * @return java.lang.Class
     * @author Jerry.X.He
     * @date 2021-01-20 20:30
     */
    public static Class primitiveClass2Wrapper(Class clazz) {
        int idx = PRIMITIVE_CLASSES.indexOf(clazz);
        if (idx < 0) {
            return null;
        }
        return PRIMITIVE_WRAPPER_CLASSES.get(idx);
    }

    /**
     * wrapperClass2Primitive
     *
     * @param clazz clazz
     * @return java.lang.Class
     * @author Jerry.X.He
     * @date 2021-01-20 20:30
     */
    public static Class wrapperClass2Primitive(Class clazz) {
        int idx = PRIMITIVE_WRAPPER_CLASSES.indexOf(clazz);
        if (idx < 0) {
            return null;
        }
        return PRIMITIVE_CLASSES.get(idx);
    }

    /**
     * 判断是否是字符串类型
     *
     * @param clazz clazz
     * @return boolean
     * @author Jerry.X.He
     * @date 2021-01-20 20:26
     */
    public static boolean isStringClass(Class clazz) {
        return STRING_CLASSES.contains(clazz);
    }

    /**
     * 判断 clazz 是否是 布尔
     *
     * @param clazz clazz
     * @return boolean
     * @author Jerry.X.He
     * @date 2021-01-24 16:27
     */
    public static boolean isBooleanClass(Class clazz) {
        return clazz == boolean.class || clazz == Boolean.class;
    }

    /**
     * 判断 clazz 是否是 整形
     *
     * @param clazz clazz
     * @return boolean
     * @author Jerry.X.He
     * @date 2021-01-24 16:27
     */
    public static boolean isIntegerClass(Class clazz) {
        return clazz == byte.class || clazz == Byte.class ||
               clazz == short.class || clazz == Short.class ||
               clazz == char.class || clazz == Character.class ||
               clazz == int.class || clazz == Integer.class;
    }

    /**
     * 判断 clazz 是否是 长整形
     *
     * @param clazz clazz
     * @return boolean
     * @author Jerry.X.He
     * @date 2021-01-24 16:27
     */
    public static boolean isLongClass(Class clazz) {
        return clazz == long.class || clazz == Long.class;
    }

    /**
     * 判断 clazz 是否是 长整形
     *
     * @param clazz clazz
     * @return boolean
     * @author Jerry.X.He
     * @date 2021-01-24 16:29
     */
    public static boolean isFloatClass(Class clazz) {
        return clazz == float.class || clazz == Float.class ||
               clazz == double.class || clazz == Double.class;
    }

    /**
     * 判断 clazz 是否是 BigInteger
     *
     * @param clazz clazz
     * @return boolean
     * @author Jerry.X.He
     * @date 2021-01-24 16:29
     */
    public static boolean isBigIntegerClass(Class clazz) {
        return clazz == BigInteger.class;
    }

    /**
     * 判断 clazz 是否是 BigDecimal
     *
     * @param clazz clazz
     * @return boolean
     * @author Jerry.X.He
     * @date 2021-01-24 16:29
     */
    public static boolean isBigDecimalClass(Class clazz) {
        return clazz == BigDecimal.class;
    }

    /**
     * 判断 clazz 是否是 Number
     *
     * @param clazz clazz
     * @return boolean
     * @author Jerry.X.He
     * @date 2021-02-03 14:01
     */
    public static boolean isNumberClass(Class clazz) {
        return Number.class.isAssignableFrom(clazz);
    }

    /**
     * 获取给定的 classloader 已经加载的类 的所有的类
     *
     * @param classLoader classLoader
     * @return java.util.List<java.lang.Class>
     * @author Jerry.X.He
     * @date 2021-01-20 21:06
     */
    public static List<Class> getAllClasses(ClassLoader classLoader) {
        try {
            Field field = ClassLoader.class.getDeclaredField("classes");
            field.setAccessible(true);
            Vector<Class> classes = (Vector<Class>) field.get(classLoader);

            List<Class> result = new ArrayList<>();
            for (Class clazz : classes) {
                result.add(clazz);
            }

            // 递归 父classloader
            if (classLoader.getParent() != null) {
                result.addAll(getAllClasses(classLoader.getParent()));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 获取给定的 classloader 已经加载的类 的所有的 baseClass 的子类
     *
     * @param classLoader classLoader
     * @param baseClazz   baseClazz
     * @return java.util.List<java.lang.Class>
     * @author Jerry.X.He
     * @date 2021-01-20 21:06
     */
    public static List<Class> getSubClasses(ClassLoader classLoader, Class baseClazz) {
        try {
            Field field = ClassLoader.class.getDeclaredField("classes");
            field.setAccessible(true);
            Vector<Class> classes = (Vector<Class>) field.get(classLoader);

            List<Class> result = new ArrayList<>();
            for (Class clazz : classes) {
                if (baseClazz.isAssignableFrom(clazz)) {
                    result.add(clazz);
                }
            }

            // 递归 父classloader
            if (classLoader.getParent() != null) {
                result.addAll(getSubClasses(classLoader.getParent(), baseClazz));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 获取给定的 类路径 或者 jar 的所有的 类
     *
     * @param pathOrJar pathOrJar
     * @return java.util.List<java.lang.Class>
     * @author Jerry.X.He
     * @date 2021-02-01 15:54
     */
    public static List<Class> getAllClasses(String pathOrJar) {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        EagerClassLoader classLoader = new EagerClassLoader(pathOrJar, parent);
        return getAllClasses(classLoader);
    }

    public static List<Class> getSubClasses(String pathOrJar, Class baseClazz) {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        EagerClassLoader classLoader = new EagerClassLoader(pathOrJar, parent);
        return getSubClasses(classLoader, baseClazz);
    }

    /**
     * 获取给定的多个 类路径 或者 jar 的所有的 类
     *
     * @param pathOrJar pathOrJar
     * @return java.util.List<java.lang.Class>
     * @author Jerry.X.He
     * @date 2021-02-01 15:54
     */
    public static List<Class> getAllClasses(String[] pathOrJar) {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        EagerClassLoader classLoader = new EagerClassLoader(pathOrJar, parent);
        return getAllClasses(classLoader);
    }

    public static List<Class> getSubClasses(String[] pathOrJar, Class baseClazz) {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        EagerClassLoader classLoader = new EagerClassLoader(pathOrJar, parent);
        return getSubClasses(classLoader, baseClazz);
    }

    /**
     * 编译已经生成的 .java 文件
     *
     * @param filePath filePath
     * @return void
     * @author Jerry.X.He
     * @date 2021-01-28 10:41
     */
    public static Class compileTheJava(String filePath, String classpath) {
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager sjfm = jc.getStandardFileManager(null, null, null);

        File theJavaFile = new File(filePath);
        try {
            ArrayList<String> options = new ArrayList<>();
            options.add("-classpath");
            options.add(classpath);

            Iterable fileObjects = sjfm.getJavaFileObjects(theJavaFile);
            jc.getTask(null, sjfm, null, options, null, fileObjects).call();
            sjfm.close();
        } catch (Exception e) {
            AssertUtils.assert0(false, formatLogInfoWithIdx(" compile {0} failed ", filePath));
            return null;
        }

        String fileName = theJavaFile.getName();
        String fileNameWithoutSuffix = fileName.substring(0, fileName.lastIndexOf("."));
        File parentFolder = theJavaFile.getParentFile();
        String clazzFileName = fileNameWithoutSuffix + Tools.CLASS;
        File theClassFile = new File(parentFolder, fileNameWithoutSuffix + Tools.CLASS);

        String targetClassPath = getTargetClassPath();
        String classPackage = determinePackageFromFile(filePath);
        File copiedClazzParentDir = new File(targetClassPath, classPackage.replaceAll("\\.", "/"));
        File copiedClazzFile = new File(copiedClazzParentDir, clazzFileName);

        // 将编译好的 class 文件复制到 当前 classpath
        copiedClazzParentDir.mkdirs();
        if (copiedClazzFile.exists()) {
            copiedClazzFile.delete();
        }
        theClassFile.renameTo(copiedClazzFile);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try {
            Class clazz = classLoader.loadClass(classPackage + "." + fileNameWithoutSuffix);
            return clazz;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取当前项目的 target/classes
     *
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-28 11:44
     */
    public static String getTargetClassPath() {
        String classpath = System.getProperty("java.class.path");
        // ":" need compatiable with other platform
        String[] splited = classpath.split(":");
        for (String cp : splited) {
            if (cp.contains("/target/classes")) {
                return cp;
            }
        }
        return System.getProperty("user.dir", "/") + "/target/classes";
    }

    /**
     * 根据文件获取包名
     *
     * @param filePath filePath
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-28 11:52
     */
    public static String determinePackageFromFile(String filePath) {
        List<String> topPackages = Arrays.asList("/com/", "/org/", "/cn/");
        for (String topPackage : topPackages) {
            if (filePath.contains(topPackage)) {
                int idxOfTopPackage = filePath.indexOf(topPackage);
                String clazzRelativePath = filePath.substring(idxOfTopPackage + 1);
                return clazzRelativePath
                        .substring(0, clazzRelativePath.lastIndexOf("/"))
                        .replaceAll("/", ".");
            }
        }
        return null;
    }

}
