package io.leego.rpa.pojo.dto;

import io.leego.rpa.constant.Messages;
import io.leego.rpa.entity.App;
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
        App.Fields.id,
        App.Fields.createdTime,
        App.Fields.updatedTime,
        App.Fields.name},
        message = Messages.SORT_INVALID)
public class AppQueryDTO extends PageRequest {
    private List<String> ids;
    private String name;
    private LocalDateTime beginCreatedTime;
    private LocalDateTime endCreatedTime;
}