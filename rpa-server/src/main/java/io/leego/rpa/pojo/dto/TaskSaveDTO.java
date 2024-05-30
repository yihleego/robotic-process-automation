package io.leego.rpa.pojo.dto;

import io.leego.rpa.constant.Messages;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Leego Yih
 */
@Data
public class TaskSaveDTO {
    @Valid
    @NotEmpty(message = Messages.TASK_INVALID)
    private List<TaskDTO> tasks;

    @Data
    public static class TaskDTO {
        private Long id;
        @NotEmpty(message = Messages.TASK_USER_ID_INVALID)
        private Long userId;
        @NotNull(message = Messages.TASK_TYPE_INVALID)
        private String type;
        private Integer priority;
        private String data;
        private LocalDateTime scheduleTime;
    }
}

