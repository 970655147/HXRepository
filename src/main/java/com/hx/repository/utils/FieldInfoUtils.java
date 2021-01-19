package com.hx.repository.utils;

import com.hx.log.util.Tools;
import com.hx.repository.model.FieldInfo;

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


}
