package com.hx.repository.context.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * UserContext
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-19 17:17
 */
@Getter
@AllArgsConstructor
public class UserContext implements Serializable {

    /** userId */
    private String userId;

    /** userName */
    private String userName;

}
