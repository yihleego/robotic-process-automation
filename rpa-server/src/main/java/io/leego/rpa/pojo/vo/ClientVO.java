package io.leego.rpa.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Leego Yih
 */
@Data
public class ClientVO {
    private String appId;
    private String userId;
    private String account;
    private String nickname;
    private String realname;
    private String company;
    private String phone;
    private String avatar;

    private Integer status;
    private Integer handle;
    private Integer process;
    private LocalDateTime startedTime;
    private LocalDateTime onlineTime;
    private String address;
}
