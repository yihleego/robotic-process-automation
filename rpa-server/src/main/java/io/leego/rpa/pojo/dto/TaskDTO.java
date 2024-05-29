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
public class TaskDTO {
    private Long appId;
    private Long userId;
    private Long taskId;
    private String account;
    private String data;
    private String type;
}
