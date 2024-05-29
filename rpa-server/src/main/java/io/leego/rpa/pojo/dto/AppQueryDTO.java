package io.leego.rpa.pojo.dto;

import io.leego.rpa.constant.Messages;
import io.leego.rpa.entity.App;
import io.leego.rpa.entity.BaseEntity;
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
        App.Fields.name},
        message = Messages.SORT_INVALID)
public class AppQueryDTO extends PageRequest {
    private List<Long> ids;
    private List<String> codes;
    private String name;
    private LocalDateTime beginCreatedTime;
    private LocalDateTime endCreatedTime;
}