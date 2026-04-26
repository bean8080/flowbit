package com.ahyeon.flowbit.domain.task;

import com.ahyeon.flowbit.domain.task.dto.CreateTaskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public void createTask(CreateTaskRequest request) {

        Task task = new Task(
                request.getProjectId(),
                request.getTitle(),
                request.getDescription(),
                TaskStatus.TODO,
                request.getAssigneeId(),
                request.getPriority(),
                LocalDateTime.now()
        );

        taskRepository.save(task);
    }
}