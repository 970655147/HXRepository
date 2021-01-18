package com.hx.repository.model;

import lombok.Data;

import java.lang.reflect.Field;

/**
 * FieldInfo
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2020-11-18 10:20
 */
@Data
public class FieldInfo<T> {
    private String commentInfo;
    private String fieldName;
    private String columnName;
    private boolean nullable;
    private Field field;
}
