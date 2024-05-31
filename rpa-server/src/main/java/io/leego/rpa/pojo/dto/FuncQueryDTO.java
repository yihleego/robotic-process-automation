package io.leego.rpa.pojo.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Leego Yih
 */
@Data
public class FuncQueryDTO {
    private List<Long> ids;
    private List<String> appIds;
    private String name;
}
