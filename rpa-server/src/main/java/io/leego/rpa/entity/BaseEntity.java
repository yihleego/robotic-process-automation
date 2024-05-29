package io.leego.rpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * @author Leego Yih
 */
@Data
@FieldNameConstants
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdTime;
    @LastModifiedDate
    @Column(nullable = true, insertable = false)
    protected LocalDateTime updatedTime;
    @Transient
    private transient boolean _new = true;

    /**
     * Persist the entity if the returned value is <code>true</code>,
     * otherwise merge the entity.
     *
     * @see jakarta.persistence.EntityManager#persist(Object)
     * @see jakarta.persistence.EntityManager#merge(Object)
     */
    @Override
    public boolean isNew() {
        return _new;
    }

    @PostLoad
    void postLoad() {
        this._new = false;
    }

    public void makeInsertable() {
        this._new = true;
    }

    public void makeUpdatable() {
        this._new = false;
    }
}
