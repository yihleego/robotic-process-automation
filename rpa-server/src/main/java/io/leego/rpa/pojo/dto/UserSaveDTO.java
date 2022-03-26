package io.leego.rpa.pojo.dto;

import io.leego.rpa.constant.Messages;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * @author Leego Yih
 */
@Data
public class UserSaveDTO {
    @Valid
    @NotEmpty(message = Messages.USER_INVALID)
    private List<UserDTO> users;

    @Data
    public static class UserDTO {
        private String id;
        @NotEmpty(message = Messages.USER_APP_ID_INVALID)
        private String appId;
        @NotEmpty(message = Messages.USER_ACCOUNT_INVALID)
        private String account;
        private String nickname;
        private String realname;
        private String company;
        private String phone;
        private String avatar;
    }
}
