package io.leego.rpa.service.impl;

import io.leego.rpa.config.RpaProperties;
import io.leego.rpa.constant.Constants;
import io.leego.rpa.converter.MessageConverter;
import io.leego.rpa.entity.App;
import io.leego.rpa.entity.BaseEntity;
import io.leego.rpa.entity.Dict;
import io.leego.rpa.entity.QApp;
import io.leego.rpa.entity.QTask;
import io.leego.rpa.entity.QUser;
import io.leego.rpa.entity.Task;
import io.leego.rpa.entity.User;
import io.leego.rpa.enumeration.AppStatus;
import io.leego.rpa.enumeration.ClientStatus;
import io.leego.rpa.enumeration.ErrorCode;
import io.leego.rpa.enumeration.TaskStatus;
import io.leego.rpa.enumeration.UserStatus;
import io.leego.rpa.pojo.dto.AppQueryDTO;
import io.leego.rpa.pojo.dto.AppSaveDTO;
import io.leego.rpa.pojo.dto.ClientCacheDTO;
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
import io.leego.rpa.repository.AppRepository;
import io.leego.rpa.repository.DictRepository;
import io.leego.rpa.repository.TaskRepository;
import io.leego.rpa.repository.UserRepository;
import io.leego.rpa.service.RpaService;
import io.leego.rpa.util.Option;
import io.leego.rpa.util.Page;
import io.leego.rpa.util.QPredicate;
import io.leego.rpa.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Leego Yih
 */
@Service
public class RpaServiceImpl implements RpaService {
    private static final Logger logger = LoggerFactory.getLogger(RpaServiceImpl.class);
    private final RpaProperties rpaProperties;
    private final AppRepository appRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final DictRepository dictRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MessageConverter messageConverter;

    public RpaServiceImpl(RpaProperties rpaProperties, AppRepository appRepository, UserRepository userRepository, TaskRepository taskRepository, DictRepository dictRepository, RedisTemplate<String, Object> redisTemplate, MessageConverter messageConverter) {
        this.rpaProperties = rpaProperties;
        this.appRepository = appRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.dictRepository = dictRepository;
        this.redisTemplate = redisTemplate;
        this.messageConverter = messageConverter;
    }

    @Override
    public Result<AppVO> getApp(String id) {
        return Result.buildSuccess(appRepository.findById(id).map(this::toVO).orElse(null));
    }

    @Override
    public Result<Page<AppVO>> listApps(AppQueryDTO dto) {
        QPredicate predicate = QPredicate.create()
                .and(QApp.app.id::in, dto.getIds())
                .and(QApp.app.name::startsWith, dto.getName())
                .and(QApp.app.createdTime::between, dto.getBeginCreatedTime(), dto.getEndCreatedTime());
        return Result.buildSuccess(appRepository.findAll(predicate, dto).map(this::toVO));
    }

    @Override
    @Transactional
    public Result<List<AppVO>> saveApps(AppSaveDTO dto) {
        List<App> apps = dto.getApps()
                .stream()
                .map(o -> {
                    App app = new App(o.getName(), AppStatus.ENABLED.getCode());
                    if (StringUtils.hasText(o.getId())) {
                        app.setId(o.getId());
                        app.makeUpdatable();
                    }
                    return app;
                })
                .collect(Collectors.toList());
        appRepository.saveAll(apps);
        return Result.buildSuccess(apps.stream().map(this::toVO).collect(Collectors.toList()));
    }

    @Override
    public Result<UserVO> getUser(String id) {
        return Result.buildSuccess(userRepository.findById(id).map(this::toVO).orElse(null));
    }

    @Override
    public Result<Page<UserVO>> listUsers(UserQueryDTO dto) {
        QPredicate predicate = QPredicate.create()
                .and(QUser.user.id::in, dto.getIds())
                .and(QUser.user.appId::in, dto.getAppIds())
                .and(QUser.user.account::startsWith, dto.getAccount())
                .and(QUser.user.nickname::startsWith, dto.getNickname())
                .and(QUser.user.realname::startsWith, dto.getRealname())
                .and(QUser.user.company::startsWith, dto.getCompany())
                .and(QUser.user.createdTime::between, dto.getBeginCreatedTime(), dto.getEndCreatedTime());
        return Result.buildSuccess(userRepository.findAll(predicate, dto).map(this::toVO));
    }

    @Override
    @Transactional
    public Result<List<UserVO>> saveUsers(UserSaveDTO dto) {
        List<String> appIds = dto.getUsers().stream().map(UserSaveDTO.UserDTO::getAppId).distinct().collect(Collectors.toList());
        List<App> apps = appRepository.findAllById(appIds);
        if (appIds.size() != apps.size()) {
            return Result.buildFailure(ErrorCode.APP_ABSENT);
        }
        if (apps.stream().anyMatch(o -> o.getStatus() != AppStatus.ENABLED.getCode())) {
            return Result.buildFailure(ErrorCode.APP_DISABLED);
        }
        List<User> users = dto.getUsers()
                .stream()
                .map(o -> {
                    User user = new User(
                            o.getAppId(), o.getAccount(), o.getNickname(), o.getRealname(), o.getCompany(),
                            o.getPhone(), o.getAvatar(), UserStatus.ENABLED.getCode());
                    if (StringUtils.hasText(o.getId())) {
                        user.setId(o.getId());
                        user.makeUpdatable();
                    }
                    return user;
                })
                .collect(Collectors.toList());
        userRepository.saveAll(users);
        return Result.buildSuccess(users.stream().map(this::toVO).collect(Collectors.toList()));
    }

    @Override
    public Result<TaskVO> getTask(String id) {
        return Result.buildSuccess(taskRepository.findById(id).map(this::toVO).orElse(null));
    }

    @Override
    public Result<Page<TaskVO>> listTasks(TaskQueryDTO dto) {
        QPredicate predicate = QPredicate.create()
                .and(QTask.task.id::in, dto.getIds())
                .and(QTask.task.appId::in, dto.getAppIds())
                .and(QTask.task.userId::in, dto.getUserIds())
                .and(QTask.task.type::eq, dto.getType())
                .and(QTask.task.status::eq, dto.getStatus())
                .and(QTask.task.scheduleTime::between, dto.getBeginScheduleTime(), dto.getEndScheduleTime())
                .and(QTask.task.executedTime::between, dto.getBeginExecutedTime(), dto.getEndExecutedTime())
                .and(QTask.task.finishedTime::between, dto.getBeginFinishedTime(), dto.getEndFinishedTime())
                .and(QTask.task.createdTime::between, dto.getBeginCreatedTime(), dto.getEndCreatedTime());
        return Result.buildSuccess(taskRepository.findAll(predicate, dto).map(this::toVO));
    }

    @Override
    @Transactional
    public Result<List<TaskVO>> saveTasks(TaskSaveDTO dto) {
        // Return error if any task already exists
        List<String> taskIds = dto.getTasks().stream().map(TaskSaveDTO.TaskDTO::getId).filter(StringUtils::hasText).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(taskIds) && taskRepository.existsByIdIn(taskIds)) {
            return Result.buildFailure(ErrorCode.TASK_PRESENT);
        }
        // Find and verify users
        List<String> userIds = dto.getTasks().stream().map(TaskSaveDTO.TaskDTO::getUserId).distinct().collect(Collectors.toList());
        List<User> users = userRepository.findAllById(userIds);
        if (userIds.size() != users.size()) {
            return Result.buildFailure(ErrorCode.USER_ABSENT);
        }
        if (users.stream().anyMatch(o -> o.getStatus() != UserStatus.ENABLED.getCode())) {
            return Result.buildFailure(ErrorCode.USER_DISABLED);
        }
        // user_id -> app_id
        Map<String, String> userIdAppIdMap = users.stream().collect(Collectors.toMap(BaseEntity::getId, User::getAppId));
        // dict_type = task_type:${app_id}
        List<String> dictTypes = users.stream().map(User::getAppId).distinct().map(o -> Constants.TASK_TYPE_PREFIX + o).collect(Collectors.toList());
        // app_id -> task_type -> task_priority
        Map<String, Map<String, String>> appIdTaskTypeMap = dictRepository.findByTypeIn(dictTypes).stream()
                .collect(Collectors.groupingBy(
                        o -> o.getType().substring(Constants.TASK_TYPE_PREFIX.length()),
                        Collectors.mapping(Function.identity(), Collectors.toMap(Dict::getKey, Dict::getValue))));
        // Verify task types
        for (TaskSaveDTO.TaskDTO task : dto.getTasks()) {
            String appId = userIdAppIdMap.get(task.getUserId());
            if (appId == null) {
                return Result.buildFailure(ErrorCode.APP_ABSENT);
            }
            Map<String, String> taskTypeMap = appIdTaskTypeMap.get(appId);
            if (taskTypeMap == null || !taskTypeMap.containsKey(task.getType())) {
                return Result.buildFailure(ErrorCode.TASK_TYPE_INVALID);
            }
        }
        List<Task> tasks = dto.getTasks()
                .stream()
                .map(o -> {
                    String appId = userIdAppIdMap.get(o.getUserId());
                    Task task = new Task(
                            appId, o.getUserId(), o.getType(), TaskStatus.CREATED.getCode(),
                            o.getPriority(), o.getData(), null, null,
                            o.getScheduleTime(), null, null);
                    if (StringUtils.hasText(o.getId())) {
                        task.setId(o.getId());
                    }
                    if (task.getScheduleTime() == null) {
                        task.setScheduleTime(LocalDateTime.now());
                    }
                    if (task.getPriority() == null) {
                        String priority = appIdTaskTypeMap.get(appId).get(task.getType());
                        if (StringUtils.hasText(priority)) {
                            task.setPriority(Integer.parseInt(priority));
                        }
                    }
                    return task;
                })
                .collect(Collectors.toList());
        taskRepository.saveAll(tasks);
        return Result.buildSuccess(tasks.stream().map(this::toVO).collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public Result<List<TaskVO>> deleteTasks(TaskDeleteDTO dto) {
        List<TaskVO> result = new ArrayList<>();
        List<Task> tasks = taskRepository.findAllById(dto.getIds());
        tasks.forEach(task -> {
            if (task.getStatus() == TaskStatus.CREATED.getCode()) {
                int deleted = taskRepository.updateStatus(task.getId(), task.getStatus(), TaskStatus.DELETED.getCode());
                if (deleted > 0) {
                    task.setStatus(TaskStatus.DELETED.getCode());
                }
            }
            result.add(toVO(task));
        });
        return Result.buildSuccess(result);
    }

    @Override
    public Result<Page<ClientVO>> listClients(ClientQueryDTO dto) {
        List<String> appIds = dto.getAppIds();
        Set<String> keys;
        if (appIds.size() == 1 && appIds.get(0).equals("*")) {
            keys = redisTemplate.keys(rpaProperties.getClient().getCacheKey() + "*");
        } else {
            keys = appIds.stream()
                    .map(appId -> redisTemplate.keys(rpaProperties.getClient().getCacheKey() + appId + ":*"))
                    .filter(o -> !CollectionUtils.isEmpty(o))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
        }
        if (CollectionUtils.isEmpty(keys)) {
            return Result.buildSuccess(Page.empty());
        }
        List<Object> values = redisTemplate.opsForValue().multiGet(keys);
        if (CollectionUtils.isEmpty(values)) {
            return Result.buildSuccess(Page.empty());
        }
        List<ClientCacheDTO> caches = values.stream()
                .filter(o -> o instanceof ClientCacheDTO)
                .map(o -> (ClientCacheDTO) o)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(caches)) {
            return Result.buildSuccess(Page.empty());
        }
        List<String> accounts = caches.stream()
                .map(ClientCacheDTO::getAccount)
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());
        List<User> users = userRepository.findByAccountIn(accounts);
        // app_id -> account -> user
        Map<String, Map<String, User>> userMap = CollectionUtils.isEmpty(accounts)
                ? Collections.emptyMap()
                : users.stream().collect(Collectors.groupingBy(User::getAppId, Collectors.mapping(Function.identity(), Collectors.toMap(User::getAccount, Function.identity()))));
        List<ClientVO> clients = caches.stream().map(o -> toVO(o, userMap.getOrDefault(o.getAppId(), Collections.emptyMap()).get(o.getAccount()))).collect(Collectors.toList());
        return Result.buildSuccess(Page.of(clients));
    }

    @Override
    public Result<Map<String, List<Option<String, String>>>> listTaskTypes() {
        List<Dict> taskTypes = dictRepository.findByTypeStartingWith(Constants.TASK_TYPE_PREFIX);
        return Result.buildSuccess(taskTypes.stream()
                .sorted(Comparator.comparing(Dict::getOrder))
                .collect(Collectors.groupingBy(
                        o -> o.getType().substring(Constants.TASK_TYPE_PREFIX.length()),
                        Collectors.mapping(o -> Option.of(o.getKey(), o.getRemark()), Collectors.toList()))));
    }

    @Override
    public Result<Map<String, List<Option<Object, Object>>>> listConstants() {
        return Result.buildSuccess(Map.of(
                AppStatus.class.getSimpleName(), Option.of(AppStatus.values(), AppStatus::getCode, o -> messageConverter.convert(o.getName())),
                UserStatus.class.getSimpleName(), Option.of(UserStatus.values(), UserStatus::getCode, o -> messageConverter.convert(o.getName())),
                TaskStatus.class.getSimpleName(), Option.of(TaskStatus.values(), TaskStatus::getCode, o -> messageConverter.convert(o.getName())),
                ClientStatus.class.getSimpleName(), Option.of(ClientStatus.values(), ClientStatus::getCode, o -> messageConverter.convert(o.getName())),
                ErrorCode.class.getSimpleName(), Option.of(ErrorCode.values(), ErrorCode::getCode, o -> messageConverter.convert(o.getMessage()))));
    }

    private AppVO toVO(App app) {
        return copyProperties(app, AppVO::new);
    }

    private UserVO toVO(User user) {
        return copyProperties(user, UserVO::new);
    }

    private TaskVO toVO(Task task) {
        return copyProperties(task, TaskVO::new);
    }

    private ClientVO toVO(ClientCacheDTO client, User user) {
        ClientVO vo = copyProperties(client, ClientVO::new);
        if (user != null) {
            vo.setUserId(user.getId());
            vo.setNickname(user.getNickname());
            vo.setRealname(user.getRealname());
            vo.setCompany(user.getCompany());
            vo.setPhone(user.getPhone());
            vo.setAvatar(user.getAvatar());
        }
        return vo;
    }

    private <T> T copyProperties(Object source, Supplier<T> factory) {
        T target = factory.get();
        if (source != null) {
            BeanUtils.copyProperties(source, target);
        }
        return target;
    }
}
