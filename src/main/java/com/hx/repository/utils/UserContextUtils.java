package com.hx.repository.utils;

import com.hx.log.util.Tools;
import com.hx.repository.context.user.UserContext;

import java.util.HashMap;
import java.util.Map;

/**
 * TaskContextUtils
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-19 15:15
 */
public final class UserContextUtils {

    /** ID_2_CONTEXT */
    public static final Map<String, UserContext> ID_2_CONTEXT = new HashMap<>();

    // disable constructor
    private UserContextUtils() {
        Tools.assert0("can't instantiate !");
    }

    /**
     * 根据 userId 获取 UserContext
     *
     * @param userId userId
     * @return com.hx.repository.model.FieldInfo
     * @author Jerry.X.He
     * @date 2021-01-19 15:15
     */
    public static UserContext lookUp(String userId) {
        UserContext context = ID_2_CONTEXT.get(userId);
        if (context == null) {
            synchronized (ID_2_CONTEXT) {
                context = ID_2_CONTEXT.get(userId);
                if (context == null) {
                    context = new UserContext(userId, null);
                    ID_2_CONTEXT.put(userId, context);
                }
            }
        }

        return context;
    }


}
