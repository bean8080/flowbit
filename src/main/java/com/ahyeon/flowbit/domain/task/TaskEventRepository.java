package com.ahyeon.flowbit.domain.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskEventRepository extends JpaRepository<TaskEvent, Long> {

    List<TaskEvent> findByTaskIdOrderByCreatedAtAsc(Long taskId);

    Optional<TaskEvent> findTopByTaskIdOrderByCreatedAtDesc(Long taskId);
}