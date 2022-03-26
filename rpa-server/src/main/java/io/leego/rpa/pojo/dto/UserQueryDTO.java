package io.leego.rpa.pojo.dto;

import io.leego.rpa.constant.Messages;
import io.leego.rpa.util.PageRequest;
import io.leego.rpa.validation.Paged;
import io.leego.rpa.validation.Sorted;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Leego Yih
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Paged(message = Messages.PAGE_INVALID)
@Sorted(properties = {
        UserQueryDTO.ID,
        UserQueryDTO.APP_ID,
        UserQueryDTO.ACCOUNT,
        UserQueryDTO.CREATED_TIME,
        UserQueryDTO.UPDATED_TIME},
        message = Messages.SORT_INVALID)
public class UserQueryDTO extends PageRequest {
    private List<String> ids;
    private List<String> appIds;
    private String account;
    private String nickname;
    private String realname;
    private String company;
    private LocalDateTime beginCreatedTime;
    private LocalDateTime endCreatedTime;

    public static final String ID = "id";
    public static final String APP_ID = "appId";
    public static final String ACCOUNT = "account";
    public static final String CREATED_TIME = "createdTime";
    public static final String UPDATED_TIME = "updatedTime";
}
