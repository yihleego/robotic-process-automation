package io.leego.rpa.pojo.dto;

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
public class ClientCacheDTO {
    private String appCode;
    private String account;
    private Integer status;
    private Integer handle;
    private Integer process;
    private LocalDateTime startedTime;
    private LocalDateTime onlineTime;
    private String address;
}
