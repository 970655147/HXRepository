package com.hx.repository.utils;

import com.hx.log.util.Tools;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

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
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
