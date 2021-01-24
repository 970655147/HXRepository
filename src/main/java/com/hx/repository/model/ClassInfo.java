package com.hx.repository.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassInfo
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2020-11-18 10:20
 */
@Data
public class ClassInfo<T> {

    /** 表名 */
    private String tableName;

    /** class */
    private Class<T> clazz;

    /** 字段列表 */
    private List<FieldInfo> fields;

    /** 基类信息 */
    private ClassInfo<? super T> superClassInfo;

    /**
     * 获取 columnName 对应的字段
     *
     * @param fieldName columnName
     * @return com.hx.test08.Test20CodeGenerator.FieldInfo
     * @author Jerry.X.He
     * @date 2020-11-17 11:37
     */
    public FieldInfo getField(String fieldName) {
        for (FieldInfo field : fields) {
            if (field.getFieldName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    /**
     * 获取所有的字段列表, 包括继承过来的
     *
     * @return java.util.List<dcamsclient.repository.base.FieldInfo>
     * @author Jerry.X.He
     * @date 2021-01-15 17:50
     */
    public List<FieldInfo> allFieldInfo() {
        List<FieldInfo> result = new ArrayList<>(fields);
        ClassInfo iteClassInfo = this.superClassInfo;
        while (iteClassInfo != null) {
            result.addAll(iteClassInfo.getFields());
            iteClassInfo = iteClassInfo.superClassInfo;
        }
        return result;
    }

}
