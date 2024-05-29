package io.leego.rpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "dict")
public class Dict implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "`type`", nullable = false)
    private String type;
    @Column(name = "`key`", nullable = false)
    private String key;
    @Column(name = "`value`")
    private String value;
    @Column(name = "`remark`")
    private String remark;
    @Column(name = "`order`")
    private Integer order;
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdTime;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedTime;
}