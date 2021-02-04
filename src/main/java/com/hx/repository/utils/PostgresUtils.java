package com.hx.repository.utils;

import com.alibaba.fastjson.JSON;
import com.hx.log.file.FileUtils;
import com.hx.log.util.Constants;
import com.hx.log.util.Tools;
import com.hx.repository.domain.BaseEntity;
import com.hx.repository.model.ClassInfo;
import com.hx.repository.model.FieldInfo;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.hx.repository.consts.SqlConstants.COLUMN_ID;
import static com.hx.repository.utils.TypeCastUtils.*;

/**
 * SqliteUtils
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-24 16:13
 */
public final class PostgresUtils {

    /** 默认的缩进 */
    public static int IDENT = 4;
    /** 默认的缩进的数量 */
    public static int IDENT_TIMES_INITIAL = 0;

    // disable constructor
    private PostgresUtils() {
        Tools.assert0("can't instantiate !");
    }

    /**
     * 获取给定的 clazz 的建表语句
     *
     * @param clazz clazz
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-24 16:14
     */
    public static <T> List<String> generateTableSchema(Class<T> clazz, String filePath) {
        try {
            List<String> lines = new ArrayList<>();
            if (FileUtils.exists(filePath)) {
                lines = Tools.getContentWithList(filePath);
            }

            generateSchemaAndSave(lines, clazz,
                                  PostgresUtils::generateCreateTableSignature,
                                  PostgresUtils::generateCreateTableSchema);

            return lines;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 获取给定的 clazz 的建表语句
     *
     * @param clazz clazz
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-24 16:50
     */
    public static <T> String generateCreateTableSchema(Class<T> clazz, Map<String, String> key2Line) {
        ClassInfo classInfo = ClassInfoUtils.getClassInfo(clazz);
        List<FieldInfo> allFieldList = classInfo.allFieldInfo();

        StringBuilder sb = new StringBuilder();
        String createTableSignature = generateCreateTableSignature(clazz);
        int identTimes = IDENT_TIMES_INITIAL;
        sb.append(ident(identTimes)).append(String.format("%s ( \n", createTableSignature));
        for (FieldInfo fieldInfo : allFieldList) {
            String columnName = fieldInfo.getColumnName();
            String dataType = dataType(fieldInfo, key2Line);
            String notNull = notNull(fieldInfo);
            String defaultValue = defaultValue(fieldInfo);

            String fieldDeclareTemplate = "%s %s %s %s,\n";
            String fieldDeclare = String.format(fieldDeclareTemplate, columnName, dataType, notNull, defaultValue);
            sb.append(ident(identTimes + 1)).append(fieldDeclare);
        }

        if (BaseEntity.class.isAssignableFrom(clazz)) {
            sb.append(ident(identTimes + 1)).append(String.format("PRIMARY KEY (%s),\n", COLUMN_ID));
        }
        Tools.removeLastSep(sb, ",\n");
        sb.append("\n");
        sb.append(ident(identTimes)).append(");\n");
        return sb.toString();
    }

    /**
     * 生成给定的方法, 并且保存给定的方法到 lines
     *
     * @param lines lines
     * @param clazz clazz
     * @return java.util.List<java.lang.String>
     * @author Jerry.X.He
     * @date 2021-01-21 17:01
     */
    public static void generateSchemaAndSave(
            List<String> lines, Class clazz,
            Function<Class, String> methodLocatorFunc,
            BiFunction<Class, Map<String, String>, String> methodGeneratorFunc) {
        String methodLocator = methodLocatorFunc.apply(clazz);
        int methodStart = locateMethodStart(lines, methodLocator, "(");
        int methodEnd = locateMethodEnd(lines, methodStart, "(", ")");

        // 采集 key -> line
        Map<String, String> key2Line = new HashMap<>();
        if (methodStart >= 0) {
            for (int i = methodStart; i < methodEnd; i++) {
                String line = lines.get(i);
                if (line.contains("DEFAULT")) {
                    String[] splits = line.trim().split("\\s+");
                    key2Line.put(splits[0], line);
                }
            }
        }

        String methodCode = methodGeneratorFunc.apply(clazz, key2Line);
        saveMethodToLines(lines, methodCode, methodStart, methodEnd);
    }

    /**
     * 生成创表语句的 声明
     *
     * @param clazz clazz
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-24 16:52
     */
    public static String generateCreateTableSignature(Class clazz) {
        ClassInfo classInfo = ClassInfoUtils.getClassInfo(clazz);
        String tableName = classInfo.getTableName();
        return String.format("CREATE TABLE %s ", tableName);
    }

    // ----------------------------------------- 其他公用 -----------------------------------------

    /**
     * 生成缩进的字符串
     *
     * @param times times
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-22 13:48
     */
    public static String ident(int times) {
        int identWithSpace = times * IDENT;
        StringBuilder result = new StringBuilder(identWithSpace);
        for (int i = 0; i < identWithSpace; i++) {
            result.append(" ");
        }
        return result.toString();
    }

    /**
     * 获取给定的字段的 数据类型
     *
     * @param fieldInfo fieldInfo
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-24 16:24
     */
    public static String dataType(FieldInfo fieldInfo, Map<String, String> key2Line) {
        Field field = fieldInfo.getField();
        Class fieldType = field.getType();
        String columnName = fieldInfo.getColumnName();
        if (key2Line.containsKey(columnName)) {
            return parseDataType(key2Line.get(columnName));
        }

        if (ClassUtils.isBooleanClass(fieldType)) {
            return "BOOL";
        } else if (ClassUtils.isIntegerClass(fieldType)) {
            return "INT8";
        } else if (ClassUtils.isLongClass(fieldType)) {
            return "BIGINT";
        } else if (ClassUtils.isBigIntegerClass(fieldType)) {
            return "BIGINT";
        } else if (ClassUtils.isFloatClass(fieldType)) {
            return "NUMERIC(20, 2)";
        } else if (ClassUtils.isBigDecimalClass(fieldType)) {
            return "NUMERIC(20, 2)";
        } else if (ClassUtils.isStringClass(fieldType)) {
            int maxLength = fieldInfo.getMaxLength();
            return String.format("VARCHAR(%s)", maxLength);
        }
        throw new RuntimeException(" unknown field :  " + JSON.toJSONString(fieldInfo));
    }

    /**
     * 获取给定的 类型声明中的 字段的类型
     *
     * @param fieldDeclare fieldDeclare
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-24 17:01
     */
    public static String parseDataType(String fieldDeclare) {
        String[] splits = fieldDeclare.trim().split("\\s+");
        return splits[1];
    }

    /**
     * 获取给定的字段的 是否可以为空
     *
     * @param fieldInfo fieldInfo
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-24 16:40
     */
    public static String notNull(FieldInfo fieldInfo) {
        if (fieldInfo.isNullable()) {
            return Constants.EMPTY_STR;
        }
        return "NOT NULL";
    }

    /**
     * 获取给定的字段的 默认值
     *
     * @param fieldInfo fieldInfo
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-24 16:41
     */
    public static String defaultValue(FieldInfo fieldInfo) {
        Object defaultValue = fieldInfo.getDefaultValue();
        Field field = fieldInfo.getField();
        Class fieldType = field.getType();
        if (!fieldInfo.isNullable() && (defaultValue == null)) {
            return "";
        }
        if (defaultValue == null) {
            return "DEFAULT NULL";
        }

        if (ClassUtils.isBooleanClass(fieldType)) {
            return "DEFAULT " + String.valueOf(defaultValue).toUpperCase();
        } else if (ClassUtils.isIntegerClass(fieldType)) {
            return "DEFAULT " + defaultValue;
        } else if (ClassUtils.isLongClass(fieldType)) {
            return "DEFAULT " + defaultValue;
        } else if (ClassUtils.isBigIntegerClass(fieldType)) {
            return "DEFAULT " + defaultValue;
        } else if (ClassUtils.isFloatClass(fieldType)) {
            return "DEFAULT " + defaultValue;
        } else if (ClassUtils.isBigDecimalClass(fieldType)) {
            return "DEFAULT " + defaultValue;
        } else if (ClassUtils.isStringClass(fieldType)) {
            return "DEFAULT '" + defaultValue + "'";
        }
        throw new RuntimeException(" unknown field :  " + JSON.toJSONString(fieldInfo));
    }

}
