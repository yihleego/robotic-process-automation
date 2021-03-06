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
public class AppSaveDTO {
    @Valid
    @NotEmpty(message = Messages.APP_INVALID)
    private List<AppDTO> apps;

    @Data
    public static class AppDTO {
        @NotEmpty(message = Messages.APP_ID_INVALID)
        private String id;
        @NotEmpty(message = Messages.APP_NAME_INVALID)
        private String name;
    }
}
