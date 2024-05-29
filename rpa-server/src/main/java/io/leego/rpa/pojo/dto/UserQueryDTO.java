package io.leego.rpa.pojo.dto;

import io.leego.rpa.constant.Messages;
import io.leego.rpa.entity.BaseEntity;
import io.leego.rpa.entity.User;
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
        BaseEntity.Fields.id,
        BaseEntity.Fields.createdTime,
        BaseEntity.Fields.updatedTime,
        User.Fields.appId,
        User.Fields.account},
        message = Messages.SORT_INVALID)
public class UserQueryDTO extends PageRequest {
    private List<Long> ids;
    private List<Long> appIds;
    private String account;
    private String nickname;
    private String realname;
    private String company;
    private LocalDateTime beginCreatedTime;
    private LocalDateTime endCreatedTime;
}
