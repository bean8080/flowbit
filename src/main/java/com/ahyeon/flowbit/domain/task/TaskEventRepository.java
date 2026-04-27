package com.ahyeon.flowbit.domain.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskEventRepository extends JpaRepository<TaskEvent, Long> {

    List<TaskEvent> findByTaskIdOrderByCreatedAtAsc(Long taskId);
}