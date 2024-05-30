package io.leego.rpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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
@Table(name = "app")
public class App implements Serializable {
    @Id
    private String id;
    @Column(nullable = false)
    private String name;
    private String logo;
    private Integer status;
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdTime;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedTime;
}