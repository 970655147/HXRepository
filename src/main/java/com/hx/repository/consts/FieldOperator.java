package com.hx.repository.consts;

import com.hx.log.interf.Code2Msg;

/**
 * FieldOperator
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2020-11-18 11:20
 */
public enum FieldOperator implements Code2Msg<String, String> {
    /** 等于 */
    EQ("EQ", "Eq", "等于"),
    /** 不等于 */
    NE("NE", "Ne", "不等于"),
    /** 大于 */
    GT("GT", "Gt", "大于"),
    /** 大于等于 */
    GE("GE", "Ge", "大于等于"),
    /** 小于 */
    LT("LT", "Lt", "小于"),
    /** 小于等于 */
    LE("LE", "Le", "小于等于"),
    /** like */
    LIKE("LIKE", "Like", "like"),
    /** not like */
    NOT_LIKE("NOT_LIKE", "NotLike", "not like"),
    /** in */
    IN("IN", "In", "in"),
    /** not in */
    NOT_IN("NOT_IN", "NotIn", "not in");
//    /** between */
//    BETWEEN("BETWEEN", "between");
//    /** not between */
//    NOT_BETWEEN("NOT_BETWEEN", "not between");

    private final String code;

    private final String camelCode;

    private final String msg;

    FieldOperator(String code, String camelCode, String msg) {
        this.code = code;
        this.camelCode = camelCode;
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

    public String getCamelCode() {
        return camelCode;
    }
}
