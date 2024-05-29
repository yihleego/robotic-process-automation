package io.leego.rpa.pojo.vo;

import io.leego.rpa.entity.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @author Leego Yih
 */
@Data
public class UserVO {
    private Long id;
    private Long appId;
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
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
