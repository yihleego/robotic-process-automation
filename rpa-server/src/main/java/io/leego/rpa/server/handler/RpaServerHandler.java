package io.leego.rpa.server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.leego.rpa.config.RpaProperties;
import io.leego.rpa.constant.Constants;
import io.leego.rpa.constant.Messages;
import io.leego.rpa.converter.MessageConverter;
import io.leego.rpa.entity.App;
import io.leego.rpa.entity.Task;
import io.leego.rpa.entity.User;
import io.leego.rpa.enumeration.AppStatus;
import io.leego.rpa.enumeration.ClientStatus;
import io.leego.rpa.enumeration.MessageCode;
import io.leego.rpa.enumeration.TaskStatus;
import io.leego.rpa.enumeration.UserStatus;
import io.leego.rpa.pojo.dto.ClientCacheDTO;
import io.leego.rpa.pojo.dto.ClientDTO;
import io.leego.rpa.pojo.dto.ClientSyncDTO;
import io.leego.rpa.pojo.dto.TaskRequestDTO;
import io.leego.rpa.pojo.dto.TaskResponseDTO;
import io.leego.rpa.pojo.dto.TaskSyncDTO;
import io.leego.rpa.repository.AppRepository;
import io.leego.rpa.repository.TaskRepository;
import io.leego.rpa.repository.UserRepository;
import io.leego.rpa.util.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author Leego Yih
 */
@Component
@ChannelHandler.Sharable
public class RpaServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RpaServerHandler.class);
    private static final ConcurrentMap<ChannelId, ChannelHandlerContext> contexts = new ConcurrentHashMap<>();
    private final RpaProperties rpaProperties;
    private final AppRepository appRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MessageConverter messageConverter;
    private final ObjectMapper objectMapper;
    private final Object taskLock = new Object();

    public RpaServerHandler(RpaProperties rpaProperties, AppRepository appRepository, UserRepository userRepository, TaskRepository taskRepository, RedisTemplate<String, Object> redisTemplate, MessageConverter messageConverter, ObjectMapper objectMapper) {
        this.rpaProperties = rpaProperties;
        this.appRepository = appRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.redisTemplate = redisTemplate;
        this.messageConverter = messageConverter;
        this.objectMapper = objectMapper;
    }

    @Override
    public void channelActive(ChannelHandlerContext context) {
        logger.info("A new client is connected [id={}, R={}]", getChannelId(context), getAddress(context));
        contexts.put(getChannelId(context), context);
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) {
        logger.info("The client is disconnected [id={}, R={}]", getChannelId(context), getAddress(context));
        contexts.remove(getChannelId(context));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        logger.error("An error has occurred [id={}, R={}]", getChannelId(context), getAddress(context), cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext context, Object event) {
        if (event instanceof IdleStateEvent && ((IdleStateEvent) event).state() == IdleState.READER_IDLE) {
            if (logger.isWarnEnabled()) {
                logger.warn("Client will be disconnected because no data was either received or sent for a long time [id={}, R={}]", getChannelId(context), getAddress(context));
            }
            context.close();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object object) {
        if (!(object instanceof Message<?> message)) {
            logger.error("Invalid message: {}", object);
            return;
        }
        MessageCode code = MessageCode.getOrDefault(message.getCode(), MessageCode.UNKNOWN);
        Object data = message.getData();
        switch (code) {
            case HEARTBEAT -> handleHeartbeat(context);
            case CLIENT_SYNC -> handleClientSync(context, objectMapper.convertValue(data, ClientSyncDTO.class));
            case TASK_SYNC -> handleTaskSync(context, objectMapper.convertValue(data, TaskSyncDTO.class));
            case TASK_REQUEST -> handleTaskRequest(context, objectMapper.convertValue(data, TaskRequestDTO.class));
            default -> logger.error("Invalid message: {}", object);
        }
    }

    public void send(ChannelHandlerContext context, MessageCode type, Object data) {
        context.writeAndFlush(new Message<>(type.getCode(), data));
    }

    private void handleHeartbeat(ChannelHandlerContext context) {
        logger.debug("Heartbeat from {}", getAddress(context));
        send(context, MessageCode.HEARTBEAT, null);
    }

    private void handleClientSync(ChannelHandlerContext context, ClientSyncDTO dto) {
        logger.debug("Sync clients: {}", dto);
        String address = String.valueOf(getAddress(context));
        List<ClientCacheDTO> clients = new ArrayList<>();
        for (ClientDTO client : dto.getClients()) {
            ClientStatus clientStatus = ClientStatus.get(client.getStatus());
            if (!StringUtils.hasText(client.getAppId()) || clientStatus == null) {
                logger.error("Invalid client: {}", client);
                continue;
            }
            if (!StringUtils.hasText(client.getAccount()) || clientStatus != ClientStatus.ONLINE) {
                logger.debug("Offline client: {}", client);
                continue;
            }
            clients.add(new ClientCacheDTO(client.getAppId(), client.getAccount(), client.getStatus(), client.getHandle(), client.getProcess(), client.getStartedTime(), client.getOnlineTime(), address));
        }
        Set<String> newKeys = clients.stream().map(this::buildClientKey).collect(Collectors.toSet());
        Set<String> oldKeys = getAttr(context, Constants.CLIENT_CACHE_KEYS);
        if (!CollectionUtils.isEmpty(oldKeys)) {
            if (!CollectionUtils.isEmpty(newKeys)) {
                oldKeys.removeAll(newKeys);
            }
            if (!CollectionUtils.isEmpty(oldKeys)) {
                redisTemplate.delete(oldKeys.stream().map(Object::toString).toList());
                oldKeys.clear();
            }
        }
        clients.forEach(o -> redisTemplate.opsForValue().set(buildClientKey(o), o, rpaProperties.getClient().getCacheTimeout()));
        setAttr(context, Constants.CLIENT_CACHE_KEYS, newKeys);
    }

    private void handleTaskSync(ChannelHandlerContext context, TaskSyncDTO dto) {
        logger.debug("Sync the task: {}", dto);
        if (dto.getStatus() == TaskStatus.CREATED.getCode()
                || dto.getStatus() == TaskStatus.DELETED.getCode()
                || dto.getStatus() == TaskStatus.CANCELLED.getCode()) {
            logger.error("Invalid task status: {}", dto.getStatus());
            return;
        }
        Task task = taskRepository.findById(dto.getTaskId()).orElse(null);
        if (task == null) {
            logger.error("The task '{}' cannot be found", dto.getTaskId());
            return;
        }
        if (task.getStatus() != TaskStatus.CREATED.getCode() && task.getStatus() != TaskStatus.RUNNING.getCode()) {
            logger.error("The task '{}' cannot be updated, task: {}, data: {}", task.getId(), task, dto);
            return;
        }
        taskRepository.updateStatusAndFinished(task.getId(), task.getStatus(), dto.getStatus(), dto.getMessage(), dto.getResult());
        logger.debug("The task '{}' has been updated, task: {}, data: {}", task.getId(), task, dto);
    }

    private void handleTaskRequest(ChannelHandlerContext context, TaskRequestDTO dto) {
        logger.debug("Find one executable task for clients: {}", dto);
        // Find one login task if there are idle clients
        if (dto.getIdleClientSize() > 0) {
            synchronized (taskLock) {
                while (true) {
                    Task task = taskRepository.findFirstByTypeAndStatusAndScheduleTimeLessThanEqualOrderByPriorityAscScheduleTimeAsc(Constants.TASK_LOGIN, TaskStatus.CREATED.getCode(), LocalDateTime.now());
                    if (task == null) {
                        break;
                    }
                    App app = appRepository.findById(task.getAppId()).orElse(null);
                    if (app == null) {
                        logger.error("The app '{}' cannot be found", task.getAppId());
                        String message = messageConverter.convert(Messages.TASK_CANCELLED_APP_UNSUPPORTED);
                        taskRepository.updateStatusAndFinished(task.getId(), task.getStatus(), TaskStatus.CANCELLED.getCode(), message, null);
                        continue;
                    }
                    if (app.getStatus() != AppStatus.ENABLED.getCode()) {
                        logger.error("The app '{}' has been disabled", task.getAppId());
                        String message = messageConverter.convert(Messages.TASK_CANCELLED_APP_UNAVAILABLE);
                        taskRepository.updateStatusAndFinished(task.getId(), task.getStatus(), TaskStatus.CANCELLED.getCode(), message, null);
                        continue;
                    }
                    User user = userRepository.findById(task.getUserId()).orElse(null);
                    if (user == null) {
                        logger.error("The user '{}' cannot be found", task.getUserId());
                        String message = messageConverter.convert(Messages.TASK_CANCELLED_USER_UNSUPPORTED);
                        taskRepository.updateStatusAndFinished(task.getId(), task.getStatus(), TaskStatus.CANCELLED.getCode(), message, null);
                        continue;
                    }
                    if (user.getStatus() != UserStatus.ENABLED.getCode()) {
                        logger.error("The app '{}' has been disabled", task.getUserId());
                        String message = messageConverter.convert(Messages.TASK_CANCELLED_USER_UNAVAILABLE);
                        taskRepository.updateStatusAndFinished(task.getId(), task.getStatus(), TaskStatus.CANCELLED.getCode(), message, null);
                        continue;
                    }
                    String address = String.valueOf(getAddress(context));
                    ClientCacheDTO cache = new ClientCacheDTO(user.getAppId(), user.getAccount(), ClientStatus.WAITING.getCode(), null, null, null, null, address);
                    Boolean success = redisTemplate.opsForValue().setIfAbsent(buildClientKey(cache), cache, rpaProperties.getClient().getCacheTimeout());
                    if (success == null || !success) {
                        logger.error("Failed to set client status, task: {}, user: {}", task, user);
                        String message = messageConverter.convert(Messages.TASK_CANCELLED_USER_LOGGED);
                        taskRepository.updateStatusAndFinished(task.getId(), task.getStatus(), TaskStatus.CANCELLED.getCode(), message, null);
                        continue;
                    }
                    taskRepository.updateStatusAndExecuted(task.getId(), task.getStatus(), TaskStatus.RUNNING.getCode());
                    send(context, MessageCode.TASK_RESPONSE, new TaskResponseDTO(task.getAppId(), task.getUserId(), task.getId(), user.getAccount(), task.getData(), task.getType(), task.getPriority(), true));
                    logger.info("The task '{}' is running", task.getId());
                    return;
                }
            }
        }
        // Return empty if no clients
        if (CollectionUtils.isEmpty(dto.getClients())) {
            logger.debug("No client online");
            return;
        }
        // Find online users
        List<String> accounts = dto.getClients().stream()
                .filter(o -> StringUtils.hasText(o.getAccount()) && Objects.equals(o.getStatus(), ClientStatus.ONLINE.getCode()))
                .map(ClientDTO::getAccount)
                .collect(Collectors.toList());
        List<User> users = userRepository.findByAccountIn(accounts);
        if (CollectionUtils.isEmpty(users)) {
            logger.debug("No user found");
            return;
        }
        Map<String, Set<String>> clientMap = dto.getClients().stream()
                .filter(o -> StringUtils.hasText(o.getAccount()) && Objects.equals(o.getStatus(), ClientStatus.ONLINE.getCode()))
                .collect(Collectors.groupingBy(ClientDTO::getAccount, Collectors.mapping(ClientDTO::getAppId, Collectors.toSet())));
        List<Long> userIds = users.stream()
                .filter(user -> clientMap.getOrDefault(user.getAccount(), Collections.emptySet()).contains(user.getAppId()))
                .map(User::getId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIds)) {
            logger.debug("No user found");
        }
        // Find one non-login task
        Task task = taskRepository.findFirstByUserIdInAndTypeNotAndStatusAndScheduleTimeLessThanEqualOrderByPriorityAscScheduleTimeAsc(userIds, Constants.TASK_LOGIN, TaskStatus.CREATED.getCode(), LocalDateTime.now());
        if (task == null) {
            logger.debug("No task to execute");
            return;
        }
        // The account will not be null
        String account = users.stream().filter(o -> Objects.equals(o.getId(), task.getUserId())).findFirst().map(User::getAccount).orElse(null);
        taskRepository.updateStatusAndExecuted(task.getId(), task.getStatus(), TaskStatus.RUNNING.getCode());
        send(context, MessageCode.TASK_RESPONSE, new TaskResponseDTO(task.getAppId(), task.getUserId(), task.getId(), account, task.getData(), task.getType(), task.getPriority(), false));
        logger.info("The task '{}' is running", task.getId());
    }

    private String buildClientKey(ClientCacheDTO cache) {
        return rpaProperties.getClient().getCacheKey() + cache.getAppId() + ":" + cache.getAccount();
    }

    @SuppressWarnings("unchecked")
    private <T> T getAttr(ChannelHandlerContext context, String key) {
        Object value = context.channel().attr(AttributeKey.valueOf(key)).get();
        if (value == null) {
            return null;
        }
        return (T) value;
    }

    private <T> void setAttr(ChannelHandlerContext context, String key, T value) {
        context.channel().attr(AttributeKey.valueOf(key)).set(value);
    }

    private ChannelId getChannelId(ChannelHandlerContext context) {
        return context.channel().id();
    }

    private SocketAddress getAddress(ChannelHandlerContext context) {
        return context.channel().remoteAddress();
    }
}