package io.leego.rpa.pojo.vo;

import io.leego.rpa.entity.User;
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
public class UserVO {
    private Long id;
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

    public static UserVO from(User user) {
        return new UserVO(
                user.getId(),
                user.getAppId(),
                user.getAccount(),
                user.getNickname(),
                user.getRealname(),
                user.getCompany(),
                user.getPhone(),
                user.getAvatar(),
                user.getStatus(),
                user.getCreatedTime(),
                user.getUpdatedTime());
    }
}
