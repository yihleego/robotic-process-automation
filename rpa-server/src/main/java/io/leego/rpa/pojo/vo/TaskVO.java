package io.leego.rpa.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Leego Yih
 */
@Data
public class TaskVO {
    private String id;
    private String appId;
    private String userId;
    private String type;
    private Integer status;
    private Integer priority;
    private String data;
    private String result;
    private String message;
    private LocalDateTime scheduleTime;
    private LocalDateTime executedTime;
    private LocalDateTime finishedTime;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
