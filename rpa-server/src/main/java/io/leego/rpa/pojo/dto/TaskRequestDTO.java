package io.leego.rpa.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDTO {
    private List<ClientDTO> clients;
    private Integer idleClientSize;
    private Integer totalClientSize;
}
