package io.leego.rpa.service;

import io.leego.rpa.pojo.dto.AppQueryDTO;
import io.leego.rpa.pojo.dto.AppSaveDTO;
import io.leego.rpa.pojo.dto.ClientQueryDTO;
import io.leego.rpa.pojo.dto.TaskDeleteDTO;
import io.leego.rpa.pojo.dto.TaskQueryDTO;
import io.leego.rpa.pojo.dto.TaskSaveDTO;
import io.leego.rpa.pojo.dto.UserQueryDTO;
import io.leego.rpa.pojo.dto.UserSaveDTO;
import io.leego.rpa.pojo.vo.AppVO;
import io.leego.rpa.pojo.vo.ClientVO;
import io.leego.rpa.pojo.vo.TaskVO;
import io.leego.rpa.pojo.vo.UserVO;
import io.leego.rpa.util.Option;
import io.leego.rpa.util.Page;
import io.leego.rpa.util.Result;

import java.util.List;
import java.util.Map;

/**
 * @author Leego Yih
 */
public interface RpaService {

    Result<AppVO> getApp(Long id);

    Result<Page<AppVO>> listApps(AppQueryDTO dto);

    Result<List<AppVO>> saveApps(AppSaveDTO dto);

    Result<UserVO> getUser(Long id);

    Result<Page<UserVO>> listUsers(UserQueryDTO dto);

    Result<List<UserVO>> saveUsers(UserSaveDTO dto);

    Result<TaskVO> getTask(Long id);

    Result<Page<TaskVO>> listTasks(TaskQueryDTO dto);

    Result<List<TaskVO>> saveTasks(TaskSaveDTO dto);

    Result<List<TaskVO>> deleteTasks(TaskDeleteDTO dto);

    Result<Page<ClientVO>> listClients(ClientQueryDTO dto);

    Result<Map<String, List<Option<String, String>>>> listTaskTypes();

    Result<Map<String, List<Option<Object, Object>>>> listEnums();

}
