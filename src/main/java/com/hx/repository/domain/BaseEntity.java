package com.hx.repository.domain;

import com.hx.repository.context.user.UserContextThreadLocal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

import static com.hx.repository.consts.SqlConstants.*;

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
    @Column(name = COLUMN_ID)
    private String id;

    @Column(name = COLUMN_SOURCE, nullable = false, updatable = false)
    private String source;

    @Column(name = COLUMN_ENABLED, nullable = false)
    private Boolean enabled;

    @Column(name = COLUMN_LOCKED, nullable = false)
    private Boolean locked;

    @Column(name = COLUMN_DELETED, nullable = false)
    private Boolean deleted;

    @Basic
    @Column(name = COLUMN_CREATED_USER_ID, updatable = false)
    private String createUserId;

    @Basic
    @Column(name = COLUMN_CREATED_AT, updatable = false)
    private Long createdAt;

    @Basic
    @Column(name = COLUMN_UPDATED_USER_ID)
    private String updateUserId;

    @Basic
    @Column(name = COLUMN_UPDATED_AT)
    private Long updatedAt;

    @Version
    @Column(name = COLUMN_VERSION)
    private long version;

    /**
     * default constructor
     *
     * @return BaseEntity
     * @author Jerry.X.He
     * @date 2021-01-24 17:51
     */
    public BaseEntity() {
        source = COLUMN_DEFAULT_SOURCE;
        enabled = COLUMN_DEFAULT_ENABLED;
        locked = COLUMN_DEFAULT_LOCKED;
        deleted = COLUMN_DEFAULT_DELETED;
        version = COLUMN_DEFAULT_VERSION;
    }

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }

        createUserId = UserContextThreadLocal.newIfNecessary().getUserId();
        createdAt = System.currentTimeMillis();

        updateUserId = createUserId;
        updatedAt = createdAt;

        version = 1;
    }

    @PreUpdate
    public void preUpdate() {
        updateUserId = UserContextThreadLocal.newIfNecessary().getUserId();
        updatedAt = System.currentTimeMillis();
    }

}
