package com.hx.repository.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

/**
 * AbstractAuditable
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-15 17:22
 */
@Getter
@Setter
public class AbstractAuditable {

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

    }

    @PreUpdate
    public void preUpdate() {


    }

    @PreRemove
    public void preRemove() {
        boolean isLocked = this.getLocked() != null && this.getLocked();
        if (isLocked) {
            throw new RuntimeException("RECORD_LOCKED");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractAuditable other = (AbstractAuditable) obj;
        if (!Objects.equals(this.getId(), other.getId())) {
            return false;
        }
        return Objects.equals(getEntity(), obj);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
               "id='" + id + '\'' +
               ", source='" + source + '\'' +
               ", enabled=" + enabled +
               ", locked=" + locked +
               ", deleted=" + deleted +
               ", createdBy='" + createdBy + '\'' +
               ", createdByUser='" + createdByUser + '\'' +
               ", createdOn=" + createdOn +
               ", lastUpdatedBy='" + lastUpdatedBy + '\'' +
               ", lastUpdatedByUser='" + lastUpdatedByUser + '\'' +
               ", lastUpdatedOn=" + lastUpdatedOn +
               ", versionNumber=" + versionNumber +
               '}';
    }

    private Object getEntity() {
        return this;
    }


}
