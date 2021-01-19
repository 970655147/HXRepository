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

    /** empty string */
    public static final String EMPTY_STR = "";
    /** and */
    public static final String OPERATOR_AND = "AND";
    /** or */
    public static final String OPERATOR_OR = "OR";

    // disable constructor
    private SqlConstants() {
        Tools.assert0("can't instantiate !");
    }


}
