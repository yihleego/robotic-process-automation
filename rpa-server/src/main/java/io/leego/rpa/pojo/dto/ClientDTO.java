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
public class ClientDTO {
    private String appId;
    private Integer status;
    private Integer handle;
    private Integer process;
    private String account;
    private String nickname;
    private String realname;
    private String company;
    private LocalDateTime startedTime;
    private LocalDateTime onlineTime;
}
