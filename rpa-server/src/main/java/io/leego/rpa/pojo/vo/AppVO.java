package io.leego.rpa.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Leego Yih
 */
@Data
public class AppVO {
    private String id;
    private String name;
    private Integer status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
