package com.hx.repository.utils;

import com.hx.log.util.Tools;
import com.hx.repository.model.ClassInfo;
import com.hx.repository.model.FieldInfo;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 获取当前 Repository 对应的实体解析之后的 ClassInfo
     *
     * @return dcamsclient.repository.base.ClassInfo<T>
     * @author Jerry.X.He
     * @date 2020-11-19 09:55
     */
    private static <T> ClassInfo parseClassInfoFromClass(Class<T> clazz) {
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

    /**
     * 从 class 中解析 字段信息
     *
     * @return java.util.List<com.hx.test08.Test20CodeGenerator.FieldInfo>
     * @author Jerry.X.He
     * @date 2020-11-19 09:55
     */
    private static <T> List<FieldInfo> parseFieldInfoListFromClass(Class<T> clazz) {
        List<FieldInfo> result = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            FieldInfo fieldInfo = new FieldInfo();
            Column columnAnno = field.getDeclaredAnnotation(Column.class);
            String columnName = Tools.camel2UnderLine(field.getName()).toUpperCase();
            if (columnAnno != null && StringUtils.isNotBlank(columnAnno.name())) {
                columnName = columnAnno.name();
            }
            boolean nullable = true;
            if (columnAnno != null) {
                nullable = columnAnno.nullable();
            }

            fieldInfo.setFieldName(field.getName());
            fieldInfo.setColumnName(columnName);
            fieldInfo.setCommentInfo(field.getName());
            fieldInfo.setNullable(nullable);
            fieldInfo.setField(field);
            result.add(fieldInfo);
        }

        return result;
    }


}
