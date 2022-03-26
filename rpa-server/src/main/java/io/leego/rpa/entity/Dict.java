package io.leego.rpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Entity
@Table(name = "dict")
@IdClass(Dict.PrimaryKey.class)
public class Dict {
    @Id
    @Column(name = "`type`", nullable = false, updatable = false)
    private String type;
    @Id
    @Column(name = "`key`", nullable = false, updatable = false)
    private String key;
    @Column(name = "`value`")
    private String value;
    @Column(name = "`remark`")
    private String remark;
    @Column(name = "`order`")
    private Integer order;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrimaryKey implements Serializable {
        private String type;
        private String key;
    }
}