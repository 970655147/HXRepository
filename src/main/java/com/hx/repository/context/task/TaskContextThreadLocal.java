package com.hx.repository.context.task;

import java.io.Serializable;

/**
 * TaskContextThreadLocal
 *
 * @author Jerry.X.He
 * @version 1.0
 * @date 2021-01-19 17:22
 */
public class TaskContextThreadLocal implements Serializable {

    /**
     * ThreadLocal
     */
    private static final ThreadLocal<TaskContext> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * get
     *
     * @return void
     * @author Jerry.X.He
     * @date 2020-08-31 18:00
     */
    public static TaskContext get() {
        if (THREAD_LOCAL.get() != null) {
            return THREAD_LOCAL.get();
        }

        return null;
    }

    /**
     * newContextIfNecessary
     *
     * @return void
     * @author Jerry.X.He
     * @date 2020-08-31 18:00
     */
    public static TaskContext newIfNecessary() {
        if (THREAD_LOCAL.get() != null) {
            return THREAD_LOCAL.get();
        }

        TaskContext result = new TaskContext(null, null);
        THREAD_LOCAL.set(result);
        return result;
    }

    /**
     * set
     *
     * @param context context
     * @return TaskContext
     * @author Jerry.X.He
     * @date 2020-09-01 09:41
     */
    public static TaskContext set(TaskContext context) {
        THREAD_LOCAL.set(context);
        return context;
    }

    /**
     * remove
     *
     * @return void
     * @author Jerry.X.He
     * @date 2020-08-31 18:02
     */
    public static void remove() {
        THREAD_LOCAL.remove();
    }

}
