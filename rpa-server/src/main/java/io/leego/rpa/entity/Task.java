package io.leego.rpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "task")
public class Task implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(updatable = false, nullable = false)
    private String appId;
    @Column(updatable = false, nullable = false)
    private Long userId;
    @Column(updatable = false, nullable = false)
    private String type;
    private Integer status;
    private Integer priority;
    private String data;
    private String result;
    private String message;
    private LocalDateTime scheduleTime;
    private LocalDateTime executedTime;
    private LocalDateTime finishedTime;
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdTime;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedTime;
}