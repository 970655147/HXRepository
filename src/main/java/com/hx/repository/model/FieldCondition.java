package com.hx.repository.model;

import com.hx.repository.consts.FieldOperator;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * FieldCondition
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-19 14:59
 */
@Getter
@AllArgsConstructor
public class FieldCondition {

    private String columnName;
    private FieldOperator operator;
    private Object value;

    private FieldInfo fieldInfo;

}
