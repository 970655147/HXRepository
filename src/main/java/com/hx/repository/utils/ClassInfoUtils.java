package com.hx.repository.utils;

import com.hx.log.util.Tools;
import com.hx.repository.model.ClassInfo;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

import static com.hx.repository.utils.FieldInfoUtils.parseFieldInfoListFromClass;

/**
 * ClassInfoUtils
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-17 10:43
 */
public final class ClassInfoUtils {

    // disable constructor
    private ClassInfoUtils() {
        Tools.assert0("can't instantiate !");
    }

    /** 全局的 classInfoManager */
    private static final Map<Class, ClassInfo> GLOBAL = new HashMap<>();

    /**
     * 获取给定的 clazz 对应的 ClassInfo
     *
     * @param clazz clazz
     * @return dcamsclient.repository.base.ClassInfo
     * @author Jerry.X.He
     * @date 2021-01-17 10:47
     */
    public static <T> ClassInfo getClassInfo(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }

        ClassInfo result = GLOBAL.get(clazz);
        if (result == null) {
            synchronized (GLOBAL) {
                result = GLOBAL.get(clazz);
                if (result == null) {
                    result = parseClassInfoFromClass(clazz);
                    GLOBAL.put(clazz, result);
                }
            }
        }

        return result;
    }

    /**
     * 清理所有的 classInfo
     * 预留
     *
     * @return void
     * @author Jerry.X.He
     * @date 2021-01-17 10:53
     */
    private static void clear() {
        synchronized (GLOBAL) {
            GLOBAL.clear();
        }
    }

    /**
     * 从给定的代码文件中解析出 ClassInfo
     *
     * @param filePath filePath
     * @return com.hx.repository.model.ClassInfo
     * @author Jerry.X.He
     * @date 2021-01-28 10:19
     */
    public static ClassInfo parseClassInfoFromSourceFile(String filePath, String classpath) {
        Class clazz = ClassUtils.compileTheJava(filePath, classpath);
        return ClassInfoUtils.getClassInfo(clazz);
    }

    /**
     * 获取当前 Repository 对应的实体解析之后的 ClassInfo
     *
     * @return dcamsclient.repository.base.ClassInfo
     * @author Jerry.X.He
     * @date 2020-11-19 09:55
     */
    public static <T> ClassInfo parseClassInfoFromClass(Class<T> clazz) {
        if (clazz == Object.class) {
            return null;
        }

        Table tableAnno = clazz.getDeclaredAnnotation(Table.class);
        String tableName = Tools.camel2UnderLine(clazz.getSimpleName()).toUpperCase();
        if (tableAnno != null && StringUtils.isNotBlank(tableAnno.name())) {
            tableName = tableAnno.name();
        }

        ClassInfo result = new ClassInfo<>();
        result.setClazz(clazz);
        result.setTableName(tableName);
        result.setFields(parseFieldInfoListFromClass(clazz));
        result.setSuperClassInfo(parseClassInfoFromClass(clazz.getSuperclass()));
        return result;
    }

}
