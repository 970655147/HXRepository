package com.hx.repository.utils;

import com.alibaba.fastjson.JSONObject;
import com.hx.log.file.FileUtils;
import com.hx.log.util.Constants;
import com.hx.log.util.Tools;
import com.hx.repository.model.ClassInfo;
import com.hx.repository.model.FieldInfo;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * TypeCastUtils
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-19 15:15
 */
public final class TypeCastUtils {

    // disable constructor
    private TypeCastUtils() {
        Tools.assert0("can't instantiate !");
    }

    /** 默认的缩进 */
    public static int IDENT = 4;
    /** 默认的缩进的数量 */
    public static int IDENT_TIMES_INITIAL = 1;
    /** 作者 */
    public static String AUTHOR = "Jerry.X.He";
    /** 日期格式 */
    public static String DATE_PATTERN = "yyyy-MM-dd HH:mm";

    /**
     * 在给定的文件中生成 给定的 clazz 的 converter
     *
     * @param clazz clazz
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-20 21:03
     */
    public static <T> List<String> generateJsonCaster(Class<T> clazz, String filePath) {
        try {
            List<String> lines = new ArrayList<>();
            if (FileUtils.exists(filePath)) {
                lines = Tools.getContentWithList(filePath);
            }

            generateMethodAndSave(lines, clazz,
                                  TypeCastUtils::generateToJsonSignature,
                                  TypeCastUtils::generateToJson);
            generateMethodAndSave(lines, clazz,
                                  TypeCastUtils::generateFromJsonSignature,
                                  TypeCastUtils::generateFromJson);

            return lines;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 在给定的文件中生成 类型转换类型 的 converter
     *
     * @param sourceClazz sourceClazz
     * @param targetClazz targetClazz
     * @param filePath    filePath
     * @return java.util.List<java.lang.String>
     * @author Jerry.X.He
     * @date 2021-01-22 15:13
     */
    public static <S, T> List<String> generateToTypeCaster(Class<S> sourceClazz, Class<T> targetClazz,
                                                           String filePath) {
        try {
            List<String> lines = new ArrayList<>();
            if (FileUtils.exists(filePath)) {
                lines = Tools.getContentWithList(filePath);
            }

            generateMethodAndSave(lines, sourceClazz, targetClazz,
                                  TypeCastUtils::generateToTypeSignature,
                                  TypeCastUtils::generateToType);
            generateMethodAndSave(lines, targetClazz, sourceClazz,
                                  TypeCastUtils::generateToTypeSignature,
                                  TypeCastUtils::generateToType);

            return lines;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 将给定的 map 转换为 json
     *
     * @param map map
     * @return com.alibaba.fastjson.JSONObject
     * @author Jerry.X.He
     * @date 2021-02-02 11:08
     */
    public static JSONObject castMap2Json(Map<String, Object> map) {
        JSONObject result = new JSONObject();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * 将给定的 map 转换为 json
     *
     * @param map map
     * @return com.alibaba.fastjson.JSONObject
     * @author Jerry.X.He
     * @date 2021-02-02 11:08
     */
    public static JSONObject castMap2JsonWithCamel(Map<String, Object> map) {
        JSONObject result = new JSONObject();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
            result.put(Tools.underLine2Camel(entry.getKey().toLowerCase()), entry.getValue());
        }
        return result;
    }

    /**
     * 将给定的 json 转换为 map
     *
     * @param json json
     * @return com.alibaba.fastjson.JSONObject
     * @author Jerry.X.He
     * @date 2021-02-02 11:08
     */
    public static Map<String, Object> castJson2Map(JSONObject json) {
        Map<String, Object> result = new HashMap<>();
        for (String key : json.keySet()) {
            result.put(key, json.get(key));
        }
        return result;
    }

    /**
     * 将给定的 json 转换为 map
     *
     * @param json json
     * @return com.alibaba.fastjson.JSONObject
     * @author Jerry.X.He
     * @date 2021-02-02 11:08
     */
    public static Map<String, Object> castJson2MapWithCamel(JSONObject json) {
        Map<String, Object> result = new HashMap<>();
        for (String key : json.keySet()) {
            result.put(key, json.get(key));
            result.put(Tools.underLine2Camel(key.toLowerCase()), json.get(key));
        }
        return result;
    }

    // ----------------------------------------- toJson/fromJson/toType 方法 -----------------------------------------

    /**
     * 生成给定的类型的 toJson 的相关代码
     *
     * @param clazz clazz
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-20 19:51
     */
    public static <T> String generateToJson(Class<T> clazz) {
        ClassInfo classInfo = ClassInfoUtils.getClassInfo(clazz);
        List<FieldInfo> fieldInfoList = classInfo.allFieldInfo();

        String methodSignature = generateToJsonSignature(clazz);
        int identTimes = IDENT_TIMES_INITIAL;
        StringBuilder sb = new StringBuilder();
        sb.append(generateToJsonDoc(clazz));
        sb.append(ident(identTimes)).append(String.format("%s {\n", methodSignature));
        sb.append(ident(identTimes + 1)).append("JSONObject result = new JSONObject();\n");
        for (FieldInfo fieldInfo : fieldInfoList) {
            String fieldName = fieldInfo.getFieldName();
            String fieldSetterMethod = wrapJsonSetterMethod(fieldInfo);
            String fieldGetterMethod = wrapGetterMethod(fieldInfo);

            String putFieldTemplate = "result.%s(\"%s\", entity.%s());\n";
            sb.append(ident(identTimes + 1))
              .append(String.format(putFieldTemplate, fieldSetterMethod, fieldName, fieldGetterMethod));
        }
        sb.append(ident(identTimes + 1)).append("return result;\n");
        sb.append(ident(identTimes)).append("}\n");
        return sb.toString();
    }

    /**
     * 生成给定的类型的 fromJson 的相关代码
     *
     * @param clazz clazz
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-20 20:11
     */
    public static <T> String generateFromJson(Class<T> clazz) {
        ClassInfo classInfo = ClassInfoUtils.getClassInfo(clazz);
        List<FieldInfo> fieldInfoList = classInfo.allFieldInfo();
        String className = clazz.getSimpleName();

        String methodSignature = generateFromJsonSignature(clazz);
        int identTimes = IDENT_TIMES_INITIAL;
        StringBuilder sb = new StringBuilder();
        sb.append(generateFromJsonDoc(clazz));
        sb.append(ident(identTimes)).append(String.format("%s {\n", methodSignature));
        sb.append(ident(identTimes + 1)).append(String.format("%s result = new %s();\n", className, className));
        for (FieldInfo fieldInfo : fieldInfoList) {
            String fieldName = fieldInfo.getFieldName();
            String fieldSetterMethod = wrapSetterMethod(fieldInfo);
            String fieldGetterMethod = wrapJsonGetterMethod(fieldInfo);
            String objectForceCast = wrapObjectForceCast(fieldInfo);

            String putFieldTemplate = "result.%s(%sjson.%s(\"%s\"));\n";
            Object[] putFieldArgs = new Object[]{fieldSetterMethod, objectForceCast, fieldGetterMethod, fieldName};
            sb.append(ident(identTimes + 1))
              .append(String.format(putFieldTemplate, putFieldArgs));
        }
        sb.append(ident(identTimes + 1)).append("return result;\n");
        sb.append(ident(identTimes)).append("}\n");
        return sb.toString();
    }

    /**
     * 生成 sourceClazz -> targetClazz 的类型转换
     *
     * @param sourceClazz sourceClazz
     * @param targetClazz targetClazz
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-22 14:16
     */
    public static <S, T> String generateToType(Class<S> sourceClazz, Class<T> targetClazz) {
        ClassInfo sourceClassInfo = ClassInfoUtils.getClassInfo(sourceClazz);
        List<FieldInfo> sourceFieldList = sourceClassInfo.allFieldInfo();
        ClassInfo targetClassInfo = ClassInfoUtils.getClassInfo(targetClazz);
        List<FieldInfo> targetFieldList = targetClassInfo.allFieldInfo();
        List<FieldInfo> jointFieldList = jointFieldInfo(sourceFieldList, targetFieldList);
        String targetTypeName = targetClazz.getSimpleName();

        String methodSignature = generateToTypeSignature(sourceClazz, targetClazz);
        int identTimes = IDENT_TIMES_INITIAL;
        StringBuilder sb = new StringBuilder();
        sb.append(generateToTypeDoc(sourceClazz, targetClazz));
        sb.append(ident(identTimes)).append(String.format("%s {\n", methodSignature));
        sb.append(ident(identTimes + 1))
          .append(String.format("%s result = new %s();\n", targetTypeName, targetTypeName));
        for (FieldInfo fieldInfo : jointFieldList) {
            String fieldName = fieldInfo.getFieldName();
            String fieldSetterMethod = wrapSetterMethod(FieldInfoUtils.lookUpByFieldName(targetFieldList, fieldName));
            String fieldGetterMethod = wrapGetterMethod(FieldInfoUtils.lookUpByFieldName(sourceFieldList, fieldName));
            String objectForceCast = wrapObjectForceCast(fieldInfo);

            String putFieldTemplate = "result.%s(%sentity.%s());\n";
            Object[] putFieldArgs = new Object[]{fieldSetterMethod, objectForceCast, fieldGetterMethod};
            sb.append(ident(identTimes + 1))
              .append(String.format(putFieldTemplate, putFieldArgs));
        }
        sb.append(ident(identTimes + 1)).append("return result;\n");
        sb.append(ident(identTimes)).append("}\n");
        return sb.toString();
    }

    // ----------------------------------------- 辅助方法 -----------------------------------------

    // ----------------------------------------- toJson 相关 -----------------------------------------

    /**
     * 生成 toJson 的 javadoc
     *
     * @param clazz clazz
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-22 13:55
     */
    public static <T> String generateToJsonDoc(Class<T> clazz) {
        String className = clazz.getSimpleName();
        String toJsonMethodName = generateToJsonName(className);

        StringBuilder sb = new StringBuilder();
        int identTimes = IDENT_TIMES_INITIAL;
        sb.append(ident(identTimes)).append("/**").append("\n");
        sb.append(ident(identTimes)).append(" * ").append(toJsonMethodName).append("\n");
        sb.append(ident(identTimes)).append(" * ").append("\n");
        sb.append(ident(identTimes)).append(" * @param entity  entity").append("\n");
        sb.append(ident(identTimes)).append(" * @return com.alibaba.fastjson.JSONObject").append("\n");
        sb.append(ident(identTimes)).append(" * @author ").append(AUTHOR).append("\n");
        sb.append(ident(identTimes)).append(" * @date ")
          .append(DateFormatUtils.format(new Date(), DATE_PATTERN)).append("\n");
        sb.append(ident(identTimes)).append(" */").append("\n");
        return sb.toString();
    }

    /**
     * 生成 toJson 的 javadoc
     *
     * @param clazz clazz
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-22 13:55
     */
    public static <T> String generateFromJsonDoc(Class<T> clazz) {
        String className = clazz.getSimpleName();
        String toJsonMethodName = generateFromJsonName(className);

        StringBuilder sb = new StringBuilder();
        int identTimes = IDENT_TIMES_INITIAL;
        sb.append(ident(identTimes)).append("/**").append("\n");
        sb.append(ident(identTimes)).append(" * ").append(toJsonMethodName).append("\n");
        sb.append(ident(identTimes)).append(" * ").append("\n");
        sb.append(ident(identTimes)).append(" * @param json  json").append("\n");
        sb.append(ident(identTimes)).append(" * @return ").append(clazz.getName()).append("\n");
        sb.append(ident(identTimes)).append(" * @author ").append(AUTHOR).append("\n");
        sb.append(ident(identTimes)).append(" * @date ")
          .append(DateFormatUtils.format(new Date(), DATE_PATTERN)).append("\n");
        sb.append(ident(identTimes)).append(" */").append("\n");
        return sb.toString();
    }

    /**
     * 生成实体转换 json 的方法声明
     *
     * @param clazz clazz
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-21 16:40
     */
    public static String generateToJsonSignature(Class clazz) {
        String className = clazz.getSimpleName();
        String methodName = generateToJsonName(className);
        return String.format("public static JSONObject %s(%s entity)", methodName, className);
    }

    public static String generateFromJsonSignature(Class clazz) {
        String className = clazz.getSimpleName();
        String methodName = generateFromJsonName(className);
        return String.format("public static %s %s(JSONObject json)", className, methodName);
    }

    /**
     * 生成实体转换 json 的方法名称
     *
     * @param className className
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-21 16:27
     */
    public static String generateToJsonName(String className) {
        return String.format("cast%sToJson", className);
    }

    public static String generateFromJsonName(String className) {
        return String.format("castJsonTo%s", className);
    }

    // ----------------------------------------- toType 相关 -----------------------------------------

    /**
     * 计算 来源字段列表 和 目标字段列表 中相同的字段列表
     *
     * @param sourceFieldList sourceFieldList
     * @param targetFieldList targetFieldList
     * @return java.util.List<com.hx.repository.model.FieldInfo>
     * @author Jerry.X.He
     * @date 2021-01-22 14:27
     */
    public static List<FieldInfo> jointFieldInfo(List<FieldInfo> sourceFieldList, List<FieldInfo> targetFieldList) {
        List<FieldInfo> result = new ArrayList<>();
        outerLoop:
        for (FieldInfo sourceField : sourceFieldList) {
            for (FieldInfo targetField : targetFieldList) {
                if (Objects.equals(sourceField.getFieldName(), targetField.getFieldName())) {
                    result.add(sourceField);
                    continue outerLoop;
                }
            }
        }
        return result;
    }

    /**
     * 生成 来源类型 到 目标类型的转换方法
     *
     * @param sourceClazz sourceClazz
     * @param targetClazz targetClazz
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-22 14:31
     */
    public static <S, T> String generateToTypeSignature(Class<S> sourceClazz, Class<T> targetClazz) {
        String sourceTypeName = sourceClazz.getSimpleName();
        String targetTypeName = targetClazz.getSimpleName();
        String methodName = generateToTypeName(sourceClazz, targetClazz);
        return String.format("public static %s %s(%s entity)", targetTypeName, methodName, sourceTypeName);
    }

    /**
     * 生成 来源类型 到 目标类型的转换方法方法形成
     *
     * @param sourceClazz sourceClazz
     * @param targetClazz targetClazz
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-22 14:32
     */
    public static <S, T> String generateToTypeName(Class<S> sourceClazz, Class<T> targetClazz) {
        return String.format("cast%sTo%s", sourceClazz.getSimpleName(), targetClazz.getSimpleName());
    }

    /**
     * 生成 toJson 的 javadoc
     *
     * @param sourceClazz sourceClazz
     * @param targetClazz targetClazz
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-22 13:55
     */
    public static <S, T> String generateToTypeDoc(Class<S> sourceClazz, Class<T> targetClazz) {
        String targetTypeName = targetClazz.getSimpleName();
        String toTypeMethodName = generateToTypeName(sourceClazz, targetClazz);

        StringBuilder sb = new StringBuilder();
        int identTimes = IDENT_TIMES_INITIAL;
        sb.append(ident(identTimes)).append("/**").append("\n");
        sb.append(ident(identTimes)).append(" * ").append(toTypeMethodName).append("\n");
        sb.append(ident(identTimes)).append(" * ").append("\n");
        sb.append(ident(identTimes)).append(" * @param entity  entity").append("\n");
        sb.append(ident(identTimes)).append(" * @return ").append(targetTypeName).append("\n");
        sb.append(ident(identTimes)).append(" * @author ").append(AUTHOR).append("\n");
        sb.append(ident(identTimes)).append(" * @date ")
          .append(DateFormatUtils.format(new Date(), DATE_PATTERN)).append("\n");
        sb.append(ident(identTimes)).append(" */").append("\n");
        return sb.toString();
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
     * 获取给定的字段的 getter 的名称
     *
     * @param fieldInfo fieldInfo
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-20 20:08
     */
    public static String wrapGetterMethod(FieldInfo fieldInfo) {
        String fieldName = fieldInfo.getFieldName();
        Class dclaredClazz = fieldInfo.getField().getDeclaringClass();
        List<String> candidates = new ArrayList<>();
        for (String prefix : Constants.BEAN_GETTER_PREFIXES) {
            candidates.add(prefix + Tools.upperCaseFirstChar(fieldName));
        }

        Method method = lookForSetterGetterMethod(dclaredClazz, candidates);
        if (method != null) {
            return method.getName();
        }
        return null;
    }

    /**
     * 获取给定的字段的 setter 的名称
     *
     * @param fieldInfo fieldInfo
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-20 20:14
     */
    public static String wrapSetterMethod(FieldInfo fieldInfo) {
        String fieldName = fieldInfo.getFieldName();
        Class dclaredClazz = fieldInfo.getField().getDeclaringClass();
        List<String> candidates = new ArrayList<>();
        for (String prefix : Constants.BEAN_SETTER_PREFIXES) {
            candidates.add(prefix + Tools.upperCaseFirstChar(fieldName));
        }

        Method method = lookForSetterGetterMethod(dclaredClazz, candidates);
        if (method != null) {
            return method.getName();
        }
        return null;
    }

    /**
     * 获取 json api 中 get + 类型的 方法
     *
     * @param fieldInfo fieldInfo
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-20 20:18
     */
    public static String wrapJsonGetterMethod(FieldInfo fieldInfo) {
        Class fieldType = fieldInfo.getField().getType();
        List<String> candidates = new ArrayList<>();
        boolean isPrimitiveOrWrapped = ClassUtils.isPrimitiveOrWrapperClass(fieldType);
        if (isPrimitiveOrWrapped) {
            boolean isPrimitive = ClassUtils.isPrimitiveClass(fieldType);
            Class wrapperClazz = fieldType;
            if (isPrimitive) {
                wrapperClazz = ClassUtils.primitiveClass2Wrapper(fieldType);
            }
            candidates.add("get" + Tools.upperCaseFirstChar(wrapperClazz.getSimpleName()));
        }
        if (ClassUtils.isStringClass(fieldType)) {
            candidates.add("getString");
        }
        candidates.add("get");

        Method method = lookForSetterGetterMethod(JSONObject.class, candidates);
        if (method != null) {
            return method.getName();
        }
        return null;
    }

    /**
     * 封装除了 基础数据类型 + String 之外的其他类型的 强转
     *
     * @param fieldInfo fieldInfo
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-21 17:56
     */
    public static String wrapObjectForceCast(FieldInfo fieldInfo) {
        Class fieldType = fieldInfo.getField().getType();
        boolean isPrimitiveOrWrapped = ClassUtils.isPrimitiveOrWrapperClass(fieldType);
        if (isPrimitiveOrWrapped) {
            return Constants.EMPTY_STR;
        }
        if (ClassUtils.isStringClass(fieldType)) {
            return Constants.EMPTY_STR;
        }

        return String.format("(%s)", fieldType.getSimpleName());
    }

    /**
     * 获取 json api 中 set + 类型的 方法
     *
     * @param fieldInfo fieldInfo
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-20 20:18
     */
    public static String wrapJsonSetterMethod(FieldInfo fieldInfo) {
        List<String> candidates = new ArrayList<>();
        candidates.add("put");

        Method method = lookForSetterGetterMethod(JSONObject.class, candidates);
        if (method != null) {
            return method.getName();
        }
        return null;
    }

    /**
     * 获取 clazz 中 candidates 中存在的方法
     *
     * @param clazz      clazz
     * @param candidates candidates
     * @return java.lang.reflect.Method
     * @author Jerry.X.He
     * @date 2021-01-21 17:31
     */
    public static Method lookForSetterGetterMethod(Class clazz, List<String> candidates) {
        if (clazz == null) {
            return null;
        }

        for (String methodName : candidates) {
            try {
                List<Method> methods = lookForMethodByName(clazz, methodName);
                if (methods.size() == 0) {
                    return null;
                }
                return methods.get(0);
            } catch (Exception e) {
                // ignore
            }
        }

        return lookForSetterGetterMethod(clazz.getSuperclass(), candidates);
    }

    /**
     * 查询给定的方法名 对应的方法列表
     *
     * @param clazz      clazz
     * @param methodName methodName
     * @return java.util.List<java.lang.reflect.Method>
     * @author Jerry.X.He
     * @date 2021-01-21 17:44
     */
    public static List<Method> lookForMethodByName(Class clazz, String methodName) {
        List<Method> result = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                result.add(method);
            }
        }
        return result;
    }

    /**
     * 定位到给定的关键字的索引, 对应于方法的开始
     *
     * @param lines   lines
     * @param locator locator
     * @return int
     * @author Jerry.X.He
     * @date 2021-01-21 16:44
     */
    public static int locateMethodStart(List<String> lines, String locator, String lBracket) {
        for (int i = 0, len = lines.size(); i < len; i++) {
            String line = lines.get(i);
            if (line.contains(locator) && line.contains(lBracket)) {
                return i;
            }
        }
        return -1;
    }

    public static int locateMethodStart(List<String> lines, String locator) {
        return locateMethodStart(lines, locator, "{");
    }

    /**
     * 定位到方法的结束
     *
     * @param lines    lines
     * @param start    start
     * @param lBracket lBracket
     * @param rBracket rBracket
     * @return int
     * @author Jerry.X.He
     * @date 2021-01-21 16:44
     */
    public static int locateMethodEnd(List<String> lines, int start, String lBracket, String rBracket) {
        if (start < 0) {
            return -1;
        }

        int ident = 0;
        for (int i = start, len = lines.size(); i < len; i++) {
            String line = lines.get(i);
            if (line.contains(lBracket)) {
                ident++;
            }
            if (line.contains(rBracket)) {
                ident--;
            }

            if (ident == 0) {
                return i;
            }
        }
        return -1;
    }

    public static int locateMethodEnd(List<String> lines, int start) {
        return locateMethodEnd(lines, start, "{", "}");
    }

    /**
     * 查询给定的 lines 中 locator 的索引
     *
     * @param lines   lines
     * @param locator locator
     * @return int
     * @author Jerry.X.He
     * @date 2021-01-21 17:08
     */
    public static int locateFor(List<String> lines, String locator) {
        for (int i = 0, len = lines.size(); i < len; i++) {
            if (lines.get(i).contains(locator)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 查询给定的 lines 中 locator 的索引, 倒序查询
     *
     * @param lines   lines
     * @param locator locator
     * @return int
     * @author Jerry.X.He
     * @date 2021-01-21 17:09
     */
    public static int locateForReverse(List<String> lines, String locator) {
        for (int i = lines.size() - 1; i >= 0; i--) {
            if (lines.get(i).contains(locator)) {
                return i;
            }
        }
        return -1;
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
    public static void generateMethodAndSave(
            List<String> lines, Class clazz,
            Function<Class, String> methodLocatorFunc,
            Function<Class, String> methodGeneratorFunc) {
        String methodLocator = methodLocatorFunc.apply(clazz);
        int methodStart = locateMethodStart(lines, methodLocator);
        int methodEnd = locateMethodEnd(lines, methodStart);
        String methodCode = methodGeneratorFunc.apply(clazz);
        saveMethodToLines(lines, methodCode, methodStart, methodEnd);
    }

    /**
     * 生成给定的方法, 并且保存给定的方法到 lines
     *
     * @param lines               lines
     * @param sourceClazz         sourceClazz
     * @param targetClazz         targetClazz
     * @param methodLocatorFunc   methodLocatorFunc
     * @param methodGeneratorFunc methodGeneratorFunc
     * @return void
     * @author Jerry.X.He
     * @date 2021-01-22 15:18
     */
    public static <S, T> void generateMethodAndSave(
            List<String> lines,
            Class<S> sourceClazz,
            Class<T> targetClazz,
            BiFunction<Class, Class, String> methodLocatorFunc,
            BiFunction<Class, Class, String> methodGeneratorFunc) {
        String methodLocator = methodLocatorFunc.apply(sourceClazz, targetClazz);
        int methodStart = locateMethodStart(lines, methodLocator);
        int methodEnd = locateMethodEnd(lines, methodStart);
        String methodCode = methodGeneratorFunc.apply(sourceClazz, targetClazz);
        saveMethodToLines(lines, methodCode, methodStart, methodEnd);
    }

    /**
     * 将方法的代码 保存到 lines 里面, 如果已经有了 则更新, 如果没有 则新增
     *
     * @param lines       lines
     * @param methodCode  methodCode
     * @param methodStart methodStart
     * @param methodEnd   methodEnd
     * @return java.util.List<java.lang.String>
     * @author Jerry.X.He
     * @date 2021-01-21 16:57
     */
    public static void saveMethodToLines(List<String> lines, String methodCode,
                                         int methodStart, int methodEnd) {
        // 新增
        if (methodStart < 0) {
            int idxOfLastRBracket = locateForReverse(lines, "}");
            int insertIdx = lines.size();
            if (idxOfLastRBracket >= 0) {
                insertIdx = idxOfLastRBracket;
            }
            lines.add(insertIdx, methodCode);
            return;
        }

        // 更新
        lines.set(methodStart, methodCode);
        for (int i = methodStart + 1; i <= methodEnd; i++) {
            lines.remove(methodStart + 1);
        }
    }


}
