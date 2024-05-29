package io.leego.rpa.pojo.vo;

import io.leego.rpa.entity.App;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @author Leego Yih
 */
@Data
public class AppVO {
    private Long id;
    private String code;
    private String name;
    private Integer status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public static AppVO from(App app) {
        AppVO vo = new AppVO();
        BeanUtils.copyProperties(app, vo);
        return vo;
    }
}
