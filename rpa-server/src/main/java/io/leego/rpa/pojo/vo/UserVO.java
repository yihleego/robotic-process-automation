package io.leego.rpa.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Leego Yih
 */
@Data
public class UserVO {
    private String id;
    private String appId;
    private String account;
    private String nickname;
    private String realname;
    private String company;
    private String phone;
    private String avatar;
    private Integer status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
