package com.hx.repository.consts;

import com.hx.log.interf.Code2Msg;

/**
 * FieldOperator
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2020-11-18 11:20
 */
public enum DbType implements Code2Msg<String, String> {
    /** mysql */
    MYSQL("MYSQL", "mysql"),
    /** sql server */
    SQL_SERVER("SQL_SERVER", "sql server"),
    /** oracle */
    ORACLE("ORACLE", "oracle"),
    /** postgres */
    POSTGRES("POSTGRES", "postgres"),
    /** redis */
    REDIS("REDIS", "redis"),
    /** mongo */
    MONGO("MONGO", "mongo"),
    ;

    private final String code;

    private final String msg;

    DbType(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String msg() {
        return msg;
    }

}
