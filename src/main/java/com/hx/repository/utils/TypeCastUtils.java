package com.hx.repository.utils;

import com.alibaba.fastjson.JSONObject;
import com.hx.log.file.FileUtils;
import com.hx.log.util.Constants;
import com.hx.log.util.Tools;
import com.hx.repository.model.ClassInfo;
import com.hx.repository.model.FieldInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * TypeCastUtils
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-19 15:15
 */
public final class TypeCastUtils {

    /**
     * 在给定的文件中生成 给定的 clazz 的 converter
     *
     * @param clazz clazz
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-20 21:03
     */
    public static <T> List<String> generateJsonTypeCaster(Class<T> clazz, String filePath) {
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
     * 生成给定的类型的 toJson 的相关代码
     *
     * @param clazz clazz
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-20 19:51
     */
    public static <T> String generateToJson(Class<T> clazz) {
        StringBuilder sb = new StringBuilder();
        ClassInfo classInfo = ClassInfoUtils.getClassInfo(clazz);
        List<FieldInfo> fieldInfoList = classInfo.allFieldInfo();

        String methodSignature = generateToJsonSignature(clazz);
        sb.append(String.format("%s {\n", methodSignature));
        sb.append("JSONObject result = new JSONObject();\n");
        for (FieldInfo fieldInfo : fieldInfoList) {
            String putFieldTemplate = "result.%s(\"%s\", entity.%s());\n";
            String fieldName = fieldInfo.getFieldName();
            String fieldSetterMethod = wrapJsonSetterMethod(fieldInfo);
            String fieldGetterMethod = wrapGetterMethod(fieldInfo);
            sb.append(String.format(putFieldTemplate, fieldSetterMethod, fieldName, fieldGetterMethod));
        }
        sb.append("return result;\n");
        sb.append("}\n");
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
        StringBuilder sb = new StringBuilder();
        ClassInfo classInfo = ClassInfoUtils.getClassInfo(clazz);
        List<FieldInfo> fieldInfoList = classInfo.allFieldInfo();
        String className = clazz.getSimpleName();

        String methodSignature = generateFromJsonSignature(clazz);
        sb.append(String.format("%s {\n", methodSignature));
        sb.append(String.format("%s result = new %s();\n", className, className));
        for (FieldInfo fieldInfo : fieldInfoList) {
            String putFieldTemplate = "result.%s(%sjson.%s(\"%s\"));\n";
            String fieldName = fieldInfo.getFieldName();
            String fieldSetterMethod = wrapSetterMethod(fieldInfo);
            String jsonGetterMethod = wrapJsonGetterMethod(fieldInfo);
            String objectForceCast = wrapObjectForceCast(fieldInfo);
            sb.append(String.format(putFieldTemplate, fieldSetterMethod, objectForceCast, jsonGetterMethod, fieldName));
        }
        sb.append("return result;\n");
        sb.append("}\n");
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
        return String.format("public JSONObject %s(%s entity)", methodName, className);
    }

    public static String generateFromJsonSignature(Class clazz) {
        String className = clazz.getSimpleName();
        String methodName = generateFromJsonName(className);
        return String.format("public %s %s(JSONObject json)", className, methodName);
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
            boolean isWrapped = ClassUtils.isPrimitiveWrapperClass(fieldType);
            Class primitiveClazz = fieldType;
            if (isWrapped) {
                primitiveClazz = ClassUtils.wrapperClass2Primitive(fieldType);
            }
            candidates.add("get" + Tools.upperCaseFirstChar(primitiveClazz.getSimpleName()));
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
            lines.remove(i);
        }
    }


}
