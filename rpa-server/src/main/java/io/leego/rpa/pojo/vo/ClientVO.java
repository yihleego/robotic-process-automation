package io.leego.rpa.pojo.vo;

import io.leego.rpa.entity.User;
import io.leego.rpa.pojo.dto.ClientCacheDTO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @author Leego Yih
 */
@Data
public class ClientVO {
    private String appId;
    private Long userId;
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

    public static ClientVO from(ClientCacheDTO client, User user) {
        ClientVO vo = new ClientVO();
        BeanUtils.copyProperties(client, vo);
        if (user != null) {
            vo.setUserId(user.getId());
            vo.setNickname(user.getNickname());
            vo.setRealname(user.getRealname());
            vo.setCompany(user.getCompany());
            vo.setPhone(user.getPhone());
            vo.setAvatar(user.getAvatar());
        }
        return vo;
    }
}
