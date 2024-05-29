package io.leego.rpa.pojo.vo;

import io.leego.rpa.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskVO {
    private Long id;
    private String appId;
    private Long userId;
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

    public static TaskVO from(Task task) {
        return new TaskVO(
                task.getId(),
                task.getAppId(),
                task.getUserId(),
                task.getType(),
                task.getStatus(),
                task.getPriority(),
                task.getData(),
                task.getResult(),
                task.getMessage(),
                task.getScheduleTime(),
                task.getExecutedTime(),
                task.getFinishedTime(),
                task.getCreatedTime(),
                task.getUpdatedTime());
    }
}
