package com.hx.repository.context.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * TaskContext
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-19 17:17
 */
@Getter
@AllArgsConstructor
public class TaskContext implements Serializable {

    /** taskId */
    private String taskId;

    /** taskName */
    private String taskName;

}
