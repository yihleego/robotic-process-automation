package io.leego.rpa.pojo.vo;

import io.leego.rpa.entity.Func;
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
public class FuncVO {
    private Long id;
    private String appId;
    private String name;
    private String remark;
    private String param;
    private Integer priority;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public static FuncVO from(Func func) {
        return new FuncVO(
                func.getId(),
                func.getAppId(),
                func.getName(),
                func.getRemark(),
                func.getParam(),
                func.getPriority(),
                func.getCreatedTime(),
                func.getUpdatedTime());
    }
}
