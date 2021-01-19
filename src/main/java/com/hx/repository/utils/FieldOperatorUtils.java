package com.hx.repository.utils;

import com.hx.log.util.Tools;
import com.hx.repository.consts.FieldOperator;

import java.util.HashMap;
import java.util.Map;

/**
 * FieldOperatorUtils
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-19 15:29
 */
public final class FieldOperatorUtils {

    /** COMMON_SQL_OPERATOR_MAP */
    public static final Map<FieldOperator, String> COMMON_SQL_OPERATOR_MAP = new HashMap<>();

    static {
        COMMON_SQL_OPERATOR_MAP.put(FieldOperator.EQ, "=");
        COMMON_SQL_OPERATOR_MAP.put(FieldOperator.NE, "<>");
        COMMON_SQL_OPERATOR_MAP.put(FieldOperator.GT, ">");
        COMMON_SQL_OPERATOR_MAP.put(FieldOperator.GE, ">=");
        COMMON_SQL_OPERATOR_MAP.put(FieldOperator.LT, "<");
        COMMON_SQL_OPERATOR_MAP.put(FieldOperator.LE, "<=");
        COMMON_SQL_OPERATOR_MAP.put(FieldOperator.LIKE, "like");
        COMMON_SQL_OPERATOR_MAP.put(FieldOperator.NOT_LIKE, "not like");
        COMMON_SQL_OPERATOR_MAP.put(FieldOperator.IN, "in");
        COMMON_SQL_OPERATOR_MAP.put(FieldOperator.NOT_IN, "not in");
    }

    // disable constructor
    private FieldOperatorUtils() {
        Tools.assert0("can't instantiate !");
    }

    /**
     * 根据查询操作符 获取 postgres 该操作符对应的字符串表示
     *
     * @param queryOperator queryOperator
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-19 15:36
     */
    public static String getSqliteOperatorSql(FieldOperator queryOperator) {
        return COMMON_SQL_OPERATOR_MAP.get(queryOperator);
    }


}
