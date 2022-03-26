package io.leego.rpa.pojo.dto;

import io.leego.rpa.constant.Messages;
import io.leego.rpa.util.PageRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Leego Yih
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClientQueryDTO extends PageRequest {
    @NotEmpty(message = Messages.APP_ID_INVALID)
    private List<String> appIds;
}
