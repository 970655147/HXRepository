package com.hx.repository.consts;

import com.hx.log.util.Tools;

/**
 * SqlConstants
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-19 15:42
 */
public final class SqlConstants {

    /** 列名 : ID */
    public static final String COLUMN_ID = "ID";

    /** empty string */
    public static final String EMPTY_STR = "";
    /** empty string */
    public static final String COMMA = ",";

    /** and */
    public static final String OPERATOR_AND = "AND";
    /** or */
    public static final String OPERATOR_OR = "OR";

    /** asc */
    public static final String SORT_ASC = "ASC";
    /** desc */
    public static final String SORT_DESC = "DESC";

    /** ORDER_BY_SUFFIX */
    public static final String ORDER_BY_SUFFIX = "$OrderBy";
    /** 列名 : SOURCE */
    public static final String COLUMN_SOURCE = "SOURCE";
    /** 列名 : ENABLED */
    public static final String COLUMN_ENABLED = "ENABLED";
    /** 列名 : LOCKED */
    public static final String COLUMN_LOCKED = "LOCKED";
    /** 列名 : DELETED */
    public static final String COLUMN_DELETED = "DELETED";
    /** 列名 : CREATED_AT */
    public static final String COLUMN_CREATED_AT = "CREATED_AT";
    /** 列名 : CREATED_USER_ID */
    public static final String COLUMN_CREATED_USER_ID = "CREATED_USER_ID";
    /** 列名 : UPDATED_AT */
    public static final String COLUMN_UPDATED_AT = "UPDATED_AT";
    /** 列名 : UPDATED_USER_ID */
    public static final String COLUMN_UPDATED_USER_ID = "UPDATED_USER_ID";
    /** 列名 : VERSION */
    public static final String COLUMN_VERSION = "VERSION";
    /** 列名 :创建时间 */
    public static final String COLUMN_CATM = "CREATED_AT";
    /** 列名 :更新时间 */
    public static final String COLUMN_UPTM = "UPDATED_AT";

    /** 列名 : 默认的 SOURCE */
    public static final String COLUMN_DEFAULT_SOURCE = "HXRepo";
    /** 列名 : 默认的 ENABLED */
    public static final Boolean COLUMN_DEFAULT_ENABLED = Boolean.TRUE;
    /** 列名 : 默认的 LOCKED */
    public static final Boolean COLUMN_DEFAULT_LOCKED = Boolean.FALSE;
    /** 列名 : 默认的 DELETED */
    public static final Boolean COLUMN_DEFAULT_DELETED = Boolean.FALSE;
    /** 列名 : 默认的 SOURCE */
    public static final Long COLUMN_DEFAULT_VERSION = 1L;

    /** dummy sql */
    public static final String SQL_DUMMY_SQL = " select 1; ";

    // disable constructor
    private SqlConstants() {
        Tools.assert0("can't instantiate !");
    }

}
