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
        AppQueryDTO.ID,
        AppQueryDTO.NAME,
        AppQueryDTO.CREATED_TIME,
        AppQueryDTO.UPDATED_TIME},
        message = Messages.SORT_INVALID)
public class AppQueryDTO extends PageRequest {
    private List<String> ids;
    private String name;
    private LocalDateTime beginCreatedTime;
    private LocalDateTime endCreatedTime;

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String CREATED_TIME = "createdTime";
    public static final String UPDATED_TIME = "updatedTime";
}