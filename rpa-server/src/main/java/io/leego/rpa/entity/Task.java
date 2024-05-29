package io.leego.rpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "task")
public class Task extends BaseEntity {
    @Column(nullable = false, updatable = false)
    private Long appId;
    @Column(nullable = false, updatable = false)
    private Long userId;
    @Column(nullable = false, updatable = false)
    private String type;
    private Integer status;
    private Integer priority;
    private String data;
    private String result;
    private String message;
    private LocalDateTime scheduleTime;
    private LocalDateTime executedTime;
    private LocalDateTime finishedTime;
}