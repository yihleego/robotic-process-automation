package io.leego.rpa.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskSyncDTO {
    private String taskId;
    private Integer status;
    private String message;
    private String result;
}
