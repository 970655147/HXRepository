package com.hx.repository.consts;

import com.hx.log.util.Tools;

/**
 * WebContextConstants
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-19 15:42
 */
public final class WebContextConstants {

    /** pageNo */
    public static final String PAGE_NO = "pageNo";
    /** pageSzie */
    public static final String PAGE_SIZE = "pageSize";

    // disable constructor
    private WebContextConstants() {
        Tools.assert0("can't instantiate !");
    }


}
