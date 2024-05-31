package io.leego.rpa.controller;

import io.leego.rpa.pojo.dto.AppQueryDTO;
import io.leego.rpa.pojo.dto.AppSaveDTO;
import io.leego.rpa.pojo.dto.ClientQueryDTO;
import io.leego.rpa.pojo.dto.FuncQueryDTO;
import io.leego.rpa.pojo.dto.TaskDeleteDTO;
import io.leego.rpa.pojo.dto.TaskQueryDTO;
import io.leego.rpa.pojo.dto.TaskSaveDTO;
import io.leego.rpa.pojo.dto.UserQueryDTO;
import io.leego.rpa.pojo.dto.UserSaveDTO;
import io.leego.rpa.pojo.vo.AppVO;
import io.leego.rpa.pojo.vo.ClientVO;
import io.leego.rpa.pojo.vo.FuncVO;
import io.leego.rpa.pojo.vo.TaskVO;
import io.leego.rpa.pojo.vo.UserVO;
import io.leego.rpa.service.RpaService;
import io.leego.rpa.util.Page;
import io.leego.rpa.util.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Leego Yih
 */
@RestController
public class RpaController {
    private final RpaService rpaService;

    public RpaController(RpaService rpaService) {
        this.rpaService = rpaService;
    }

    @GetMapping("apps/{id}")
    public Result<AppVO> getApp(@PathVariable("id") String id) {
        return rpaService.getApp(id);
    }

    @GetMapping("apps")
    public Result<Page<AppVO>> listApps(@Validated AppQueryDTO dto) {
        return rpaService.listApps(dto);
    }

    @PostMapping("apps")
    public Result<List<AppVO>> saveApps(@Validated @RequestBody AppSaveDTO dto) {
        return rpaService.saveApps(dto);
    }

    @GetMapping("users/{id}")
    public Result<UserVO> getUser(@PathVariable("id") Long id) {
        return rpaService.getUser(id);
    }

    @GetMapping("users")
    public Result<Page<UserVO>> listUsers(@Validated UserQueryDTO dto) {
        return rpaService.listUsers(dto);
    }

    @PostMapping("users")
    public Result<List<UserVO>> saveUsers(@Validated @RequestBody UserSaveDTO dto) {
        return rpaService.saveUsers(dto);
    }

    @GetMapping("tasks/{id}")
    public Result<TaskVO> getTask(@PathVariable("id") Long id) {
        return rpaService.getTask(id);
    }

    @GetMapping("tasks")
    public Result<Page<TaskVO>> listTasks(@Validated TaskQueryDTO dto) {
        return rpaService.listTasks(dto);
    }

    @PostMapping("tasks")
    public Result<List<TaskVO>> saveTasks(@Validated @RequestBody TaskSaveDTO dto) {
        return rpaService.saveTasks(dto);
    }

    @DeleteMapping("tasks")
    public Result<List<TaskVO>> deleteTasks(@Validated @RequestBody TaskDeleteDTO dto) {
        return rpaService.deleteTasks(dto);
    }

    @GetMapping("clients")
    public Result<Page<ClientVO>> listClients(@Validated ClientQueryDTO dto) {
        return rpaService.listClients(dto);
    }

    @GetMapping("funcs")
    public Result<List<FuncVO>> listFuncs(@Validated FuncQueryDTO dto) {
        return rpaService.listFuncs(dto);
    }
}
