package io.leego.rpa.pojo.dto;

import io.leego.rpa.constant.Messages;
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
        TaskQueryDTO.ID,
        TaskQueryDTO.APP_ID,
        TaskQueryDTO.USER_ID,
        TaskQueryDTO.SCHEDULE_TIME,
        TaskQueryDTO.EXECUTED_TIME,
        TaskQueryDTO.FINISHED_TIME,
        TaskQueryDTO.CREATED_TIME,
        TaskQueryDTO.UPDATED_TIME},
        message = Messages.SORT_INVALID)
public class TaskQueryDTO extends PageRequest {
    private List<String> ids;
    private List<String> appIds;
    private List<String> userIds;
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

    public static final String ID = "id";
    public static final String APP_ID = "appId";
    public static final String USER_ID = "userId";
    public static final String SCHEDULE_TIME = "scheduleTime";
    public static final String EXECUTED_TIME = "executedTime";
    public static final String FINISHED_TIME = "finishedTime";
    public static final String CREATED_TIME = "createdTime";
    public static final String UPDATED_TIME = "updatedTime";
}
