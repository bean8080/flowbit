package com.ahyeon.flowbit.domain.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByStatusNot(TaskStatus status);

    List<Task> findByProject_IdAndStatus(Long projectId, TaskStatus status);

    List<Task> findByProject_IdAndStatusNot(Long projectId, TaskStatus status);
}