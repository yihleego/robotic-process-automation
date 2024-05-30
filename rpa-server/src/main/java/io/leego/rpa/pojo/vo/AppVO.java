package io.leego.rpa.pojo.vo;

import io.leego.rpa.entity.App;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppVO {
    private String id;
    private String name;
    private String logo;
    private Integer status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public static AppVO from(App app) {
        return new AppVO(
                app.getId(),
                app.getName(),
                app.getLogo(),
                app.getStatus(),
                app.getCreatedTime(),
                app.getUpdatedTime());
    }
}
