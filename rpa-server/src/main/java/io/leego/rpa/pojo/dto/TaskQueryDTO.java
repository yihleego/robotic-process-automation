package io.leego.rpa.pojo.dto;

import io.leego.rpa.constant.Messages;
import io.leego.rpa.entity.Task;
import io.leego.rpa.util.PageRequest;
import io.leego.rpa.validation.Paged;
import io.leego.rpa.validation.Sorted;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Leego Yih
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Paged(message = Messages.PAGE_INVALID)
@Sorted(properties = {
        Task.Fields.id,
        Task.Fields.createdTime,
        Task.Fields.updatedTime,
        Task.Fields.appId,
        Task.Fields.userId,
        Task.Fields.scheduleTime,
        Task.Fields.executedTime,
        Task.Fields.finishedTime},
        message = Messages.SORT_INVALID)
public class TaskQueryDTO extends PageRequest {
    private List<Long> ids;
    private List<String> appIds;
    private List<Long> userIds;
    private String type;
    private Integer status;
    private LocalDateTime beginScheduleTime;
    private LocalDateTime endScheduleTime;
    private LocalDateTime beginExecutedTime;
    private LocalDateTime endExecutedTime;
    private LocalDateTime beginFinishedTime;
    private LocalDateTime endFinishedTime;
    private LocalDateTime beginCreatedTime;
    private LocalDateTime endCreatedTime;
}
