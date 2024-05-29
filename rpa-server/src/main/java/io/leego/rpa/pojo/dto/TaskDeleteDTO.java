package io.leego.rpa.pojo.dto;

import io.leego.rpa.constant.Messages;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * @author Leego Yih
 */
@Data
public class TaskDeleteDTO {
    @NotEmpty(message = Messages.TASK_ID_INVALID)
    private List<Long> ids;
}
