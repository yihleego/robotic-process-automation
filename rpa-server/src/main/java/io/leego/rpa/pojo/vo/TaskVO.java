package io.leego.rpa.pojo.vo;

import io.leego.rpa.entity.Task;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @author Leego Yih
 */
@Data
public class TaskVO {
    private Long id;
    private Long appId;
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
        TaskVO vo = new TaskVO();
        BeanUtils.copyProperties(task, vo);
        return vo;
    }
}
