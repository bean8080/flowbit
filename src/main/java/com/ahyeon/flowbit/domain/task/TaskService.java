package com.ahyeon.flowbit.domain.task;

import com.ahyeon.flowbit.domain.task.dto.CreateTaskRequest;
import com.ahyeon.flowbit.domain.task.dto.TaskResponse;
import com.ahyeon.flowbit.domain.task.dto.TaskEventResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskEventRepository taskEventRepository;

    public void createTask(CreateTaskRequest request) {

        LocalDateTime now = LocalDateTime.now();

        Task task = new Task(
                request.getProjectId(),
                request.getTitle(),
                request.getDescription(),
                TaskStatus.TODO,
                request.getAssigneeId(),
                request.getPriority(),
                now
        );

        Task savedTask = taskRepository.save(task);

        TaskEvent event = new TaskEvent(
                savedTask.getId(),
                TaskEventType.CREATED,
                null,
                TaskStatus.TODO,
                "Task created",
                now,
                null
        );

        taskEventRepository.save(event);
    }

    public List<TaskResponse> getTasks(TaskStatus status) {
        List<Task> tasks = status == null
                ? taskRepository.findByStatusNot(TaskStatus.DELETED)
                : taskRepository.findByStatus(status);

        return tasks.stream()
                .map(TaskResponse::new)
                .toList();
    }

    public TaskResponse getTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        return new TaskResponse(task);
    }

    public TaskResponse startTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        TaskStatus fromStatus = task.getStatus();

        if (fromStatus != TaskStatus.TODO) {
            throw new IllegalStateException("TODO 상태의 작업만 시작할 수 있습니다.");
        }

        LocalDateTime now = LocalDateTime.now();

        task.start(now);

        TaskEvent event = new TaskEvent(
                task.getId(),
                TaskEventType.STARTED,
                fromStatus,
                TaskStatus.IN_PROGRESS,
                "Task started",
                now,
                null
        );

        taskEventRepository.save(event);

        return new TaskResponse(task);
    }

    public List<TaskEventResponse> getTaskEvents(Long taskId) {
        return taskEventRepository.findByTaskIdOrderByCreatedAtAsc(taskId)
                .stream()
                .map(TaskEventResponse::new)
                .toList();
    }

    public TaskResponse completeTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        TaskStatus fromStatus = task.getStatus();

        LocalDateTime now = LocalDateTime.now();

        task.complete(now);

        TaskEvent event = new TaskEvent(
                task.getId(),
                TaskEventType.COMPLETED,
                fromStatus,
                TaskStatus.DONE,
                "Task completed",
                now,
                null
        );

        taskEventRepository.save(event);

        return new TaskResponse(task);
    }

    public TaskResponse blockTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        TaskStatus fromStatus = task.getStatus();

        LocalDateTime now = LocalDateTime.now();

        task.block();

        TaskEvent event = new TaskEvent(
                task.getId(),
                TaskEventType.BLOCKED,
                fromStatus,
                TaskStatus.BLOCKED,
                "Task blocked",
                now,
                null
        );

        taskEventRepository.save(event);

        return new TaskResponse(task);
    }

    public TaskResponse deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다."));

        TaskStatus fromStatus = task.getStatus();

        LocalDateTime now = LocalDateTime.now();

        task.delete(now);

        TaskEvent event = new TaskEvent(
                task.getId(),
                TaskEventType.DELETED,
                fromStatus,
                TaskStatus.DELETED,
                "Task deleted",
                now,
                null
        );

        taskEventRepository.save(event);

        return new TaskResponse(task);
    }
}