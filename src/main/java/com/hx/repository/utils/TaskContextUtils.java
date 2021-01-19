package com.hx.repository.utils;

import com.hx.log.util.Tools;
import com.hx.repository.context.task.TaskContext;

import java.util.HashMap;
import java.util.Map;

/**
 * TaskContextUtils
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-19 15:15
 */
public final class TaskContextUtils {

    /** ID_2_CONTEXT */
    public static final Map<String, TaskContext> ID_2_CONTEXT = new HashMap<>();

    // disable constructor
    private TaskContextUtils() {
        Tools.assert0("can't instantiate !");
    }

    /**
     * 根据 taskId 获取 TaskContext
     *
     * @param taskId taskId
     * @return com.hx.repository.model.FieldInfo
     * @author Jerry.X.He
     * @date 2021-01-19 15:15
     */
    public static TaskContext lookUp(String taskId) {
        TaskContext context = ID_2_CONTEXT.get(taskId);
        if (context == null) {
            synchronized (ID_2_CONTEXT) {
                context = ID_2_CONTEXT.get(taskId);
                if (context == null) {
                    context = new TaskContext(taskId, null);
                    ID_2_CONTEXT.put(taskId, context);
                }
            }
        }

        return context;
    }


}
