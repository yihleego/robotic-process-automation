package io.leego.rpa.repository;

import io.leego.rpa.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Leego Yih
 */
public interface TaskRepository extends JpaRepository<Task, Long>, QuerydslRepository<Task> {

    @Nullable
    Task findFirstByTypeAndStatusAndScheduleTimeLessThanEqualOrderByPriorityAscScheduleTimeAsc(String type, Integer status, LocalDateTime scheduleTime);

    @Nullable
    Task findFirstByUserIdInAndTypeNotAndStatusAndScheduleTimeLessThanEqualOrderByPriorityAscScheduleTimeAsc(List<Long> userIds, String type, Integer status, LocalDateTime scheduleTime);

    @Transactional
    @Modifying
    @Query("update Task set status = :newStatus where id = :id and status = :oldStatus")
    int updateStatus(@Param("id") Long id, @Param("oldStatus") Integer oldStatus, @Param("newStatus") Integer newStatus);

    @Transactional
    @Modifying
    @Query("update Task set status = :newStatus, executedTime = :#{T(java.time.LocalDateTime).now()} where id = :id and status = :oldStatus")
    int updateStatusAndExecuted(@Param("id") Long id, @Param("oldStatus") Integer oldStatus, @Param("newStatus") Integer newStatus);

    @Transactional
    @Modifying
    @Query("update Task set status = :newStatus, message = :message, result = :result, finishedTime = :#{T(java.time.LocalDateTime).now()} where id = :id and status = :oldStatus")
    int updateStatusAndFinished(@Param("id") Long id, @Param("oldStatus") Integer oldStatus, @Param("newStatus") Integer newStatus, @Nullable @Param("message") String message, @Nullable @Param("result") String result);

    boolean existsByIdIn(List<Long> ids);

}
