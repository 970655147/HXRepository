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

    /** 注释信息 */
    private String commentInfo;

    /** 字段名称 */
    private String fieldName;

    /** 字段对应的列名 */
    private String columnName;

    /** 是否可以为 null */
    private boolean nullable;

    /** 是否可以为 出现在 INSERT 里面 */
    private boolean insertable;

    /** 是否可以为 出现在 UPDATE 里面 */
    private boolean updatable;

    /** 字段最短的长度 */
    private int minLength;

    /** 字段最长的长度 */
    private int maxLength;

    /** 更新的时候 增量处理 */
    private boolean updateIncr;

    /** 更新的时候 增量处理的数量 */
    private int updateIncrOffset;

    /** 字段对应的 Field */
    private Field field;

}
