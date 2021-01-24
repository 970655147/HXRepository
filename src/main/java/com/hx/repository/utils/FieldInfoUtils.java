package com.hx.repository.utils;

import com.hx.log.util.Tools;
import com.hx.repository.domain.BaseEntity;
import com.hx.repository.model.FieldInfo;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * FieldInfoUtils
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-19 15:15
 */
public final class FieldInfoUtils {

    // disable constructor
    private FieldInfoUtils() {
        Tools.assert0("can't instantiate !");
    }

    /** BaseEntity.versionNumber */
    public static Field BASE_ENTITY_VERSION_NUMBER_FIELD;
    /** 上一次创建的对象 */
    public static WeakReference<Object> LAST_CREATED_INSTANCE_REF = new WeakReference<>(null);

    /**
     * 根据 fieldName/columnName 进行查询
     *
     * @param fieldList fieldList
     * @param fieldName fieldName
     * @return com.hx.repository.model.FieldInfo
     * @author Jerry.X.He
     * @date 2021-01-19 15:15
     */
    public static FieldInfo lookUpByFieldName(List<FieldInfo> fieldList, String fieldName) {
        for (FieldInfo fieldInfo : fieldList) {
            if (fieldInfo.getFieldName().equals(fieldName)) {
                return fieldInfo;
            }
        }
        return null;
    }

    public static FieldInfo lookUpByColumnName(List<FieldInfo> fieldList, String columnName) {
        for (FieldInfo fieldInfo : fieldList) {
            if (fieldInfo.getColumnName().equals(columnName)) {
                return fieldInfo;
            }
        }
        return null;
    }

    /**
     * 从 class 中解析 字段信息
     *
     * @return java.util.List<com.hx.test08.Test20CodeGenerator.FieldInfo>
     * @author Jerry.X.He
     * @date 2020-11-19 09:55
     */
    public static <T> List<FieldInfo> parseFieldInfoListFromClass(Class<T> clazz) {
        List<FieldInfo> result = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            FieldInfo fieldInfo = new FieldInfo();
            Column columnAnno = field.getDeclaredAnnotation(Column.class);

            // 从 @Column 上面获取 列明, nullable, insertable, updatable, length 等等属性
            String columnName = Tools.camel2UnderLine(field.getName()).toUpperCase();
            if (columnAnno != null && StringUtils.isNotBlank(columnAnno.name())) {
                columnName = columnAnno.name();
            }
            boolean nullable = true, insertable = true, updatable = true;
            int minLength = 0, maxLength = 1024;
            if (columnAnno != null) {
                nullable = columnAnno.nullable();
                insertable = columnAnno.insertable();
                updatable = columnAnno.updatable();
                maxLength = columnAnno.length();
            }
            boolean updateIncr = false;
            int updateIncrOffset = 0;
            if (field.equals(baseEntityVersionNumberField())) {
                updateIncr = true;
                updateIncrOffset = 1;
            }

            /** 获取当前字段的默认值 */
            Object defaultValue = defaultValue(clazz, field);

            // apply FieldInfo 的相关属性
            fieldInfo.setFieldName(field.getName());
            fieldInfo.setColumnName(columnName);
            fieldInfo.setCommentInfo(field.getName());
            fieldInfo.setNullable(nullable);
            fieldInfo.setInsertable(insertable);
            fieldInfo.setUpdatable(updatable);
            fieldInfo.setMinLength(minLength);
            fieldInfo.setMaxLength(maxLength);
            fieldInfo.setUpdateIncr(updateIncr);
            fieldInfo.setUpdateIncrOffset(updateIncrOffset);
            fieldInfo.setDefaultValue(defaultValue);
            fieldInfo.setField(field);
            result.add(fieldInfo);
        }

        return result;
    }

    /**
     * 获取 BaseEntity 的 versionNumber 字段
     *
     * @return java.lang.reflect.Field
     * @author Jerry.X.He
     * @date 2021-01-24 15:39
     */
    public static Field baseEntityVersionNumberField() {
        if (BASE_ENTITY_VERSION_NUMBER_FIELD != null) {
            return BASE_ENTITY_VERSION_NUMBER_FIELD;
        }

        try {
            BASE_ENTITY_VERSION_NUMBER_FIELD = BaseEntity.class.getDeclaredField("versionNumber");
            return BASE_ENTITY_VERSION_NUMBER_FIELD;
        } catch (Exception e) {
            // ignore
            return null;
        }
    }

    /**
     * 获取给定的 clazz 的给定的 field 的默认值
     *
     * @param clazz clazz
     * @param field field
     * @return java.lang.Object
     * @author Jerry.X.He
     * @date 2021-01-24 16:20
     */
    public static <T> Object defaultValue(Class<T> clazz, Field field) {
        try {
            Object lastCreatedInstance = LAST_CREATED_INSTANCE_REF.get();
            if (lastCreatedInstance == null || lastCreatedInstance.getClass() != clazz) {
                lastCreatedInstance = clazz.newInstance();
                LAST_CREATED_INSTANCE_REF = new WeakReference<>(lastCreatedInstance);
            }

            Object entity = lastCreatedInstance;
            field.setAccessible(true);
            return field.get(entity);
        } catch (Exception e) {
            // ignore
            return null;
        }
    }


}
