package com.hx.repository.domain;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;

/**
 * BaseView
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-24 18:48
 */
@Data
public class BaseView implements Serializable {

    @Id
    private String id;

    private String source;

    private Boolean enabled;

    private Boolean locked;

    private Boolean deleted;

    private String createdUserId;

    private Long createdAt;

    private String updatedUserId;

    private Long updatedAt;

    private Long version;

}
