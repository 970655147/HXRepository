package com.hx.repository.domain;

import com.hx.repository.context.user.UserContextThreadLocal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

/**
 * BaseEntity
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-15 17:22
 */
@Getter
@Setter
public class BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "ID")
    private String id;

    @Column(name = "SOURCE", nullable = false, updatable = false)
    private String source = "SYSTEM";

    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled = Boolean.TRUE;

    @Column(name = "LOCKED", nullable = false)
    private Boolean locked = Boolean.FALSE;

    @Column(name = "DELETED", nullable = false)
    private Boolean deleted = Boolean.FALSE;

    @Basic
    @Column(name = "CREATED_BY", updatable = false)
    private String createdBy;

    @Basic
    @Column(name = "CREATED_BY_USER", updatable = false)
    private String createdByUser;

    @Basic
    @Column(name = "CREATED_ON", updatable = false)
    private Long createdOn;

    @Basic
    @Column(name = "LAST_UPDATED_BY")
    private String lastUpdatedBy;

    @Basic
    @Column(name = "LAST_UPDATED_BY_USER")
    private String lastUpdatedByUser;

    @Basic
    @Column(name = "LAST_UPDATED_ON")
    private Long lastUpdatedOn;

    @Version
    @Column(name = "VERSION_NUMBER")
    private long versionNumber = 1;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }

        createdBy = UserContextThreadLocal.newIfNecessary().getUserId();
        createdByUser = UserContextThreadLocal.newIfNecessary().getUserName();
        createdOn = System.currentTimeMillis();

        lastUpdatedBy = createdBy;
        lastUpdatedByUser = createdByUser;
        lastUpdatedOn = createdOn;

        versionNumber = 1;
    }

    @PreUpdate
    public void preUpdate() {
        lastUpdatedBy = UserContextThreadLocal.newIfNecessary().getUserId();
        lastUpdatedByUser = UserContextThreadLocal.newIfNecessary().getUserName();
        lastUpdatedOn = System.currentTimeMillis();
    }

}
