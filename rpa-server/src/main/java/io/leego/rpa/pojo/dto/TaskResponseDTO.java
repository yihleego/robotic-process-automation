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
public class TaskResponseDTO {
    private String appId;
    private String userId;
    private String taskId;
    private String account;
    private String data;
    private String type;
    private Integer priority;
    private boolean login;
}
