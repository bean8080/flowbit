package com.ahyeon.flowbit.domain.snapshot;

import com.ahyeon.flowbit.domain.project.Project;
import com.ahyeon.flowbit.domain.project.ProjectRepository;
import com.ahyeon.flowbit.domain.snapshot.dto.ProjectSnapshotResponse;
import com.ahyeon.flowbit.domain.snapshot.dto.TaskSnapshotResponse;
import com.ahyeon.flowbit.domain.task.Task;
import com.ahyeon.flowbit.domain.task.TaskEvent;
import com.ahyeon.flowbit.domain.task.TaskEventRepository;
import com.ahyeon.flowbit.domain.task.TaskRepository;
import com.ahyeon.flowbit.domain.task.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SnapshotService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskEventRepository taskEventRepository;

    @Transactional(readOnly = true)
    public ProjectSnapshotResponse getProjectSnapshot(Long projectId, LocalDateTime at) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        List<Task> tasks = taskRepository.findByProject_Id(projectId);

        if (tasks.isEmpty()) {
            return new ProjectSnapshotResponse(
                    project.getId(),
                    project.getName(),
                    at,
                    0,
                    0,
                    0,
                    0,
                    0,
                    List.of()
            );
        }

        List<Long> taskIds = tasks.stream()
                .map(Task::getId)
                .toList();

        Map<Long, Task> taskMap = tasks.stream()
                .collect(Collectors.toMap(Task::getId, task -> task));

        List<TaskEvent> events =
                taskEventRepository.findByTaskIdInAndCreatedAtLessThanEqualOrderByCreatedAtAsc(
                        taskIds,
                        at
                );

        Map<Long, TaskEvent> latestEventByTaskId = new HashMap<>();

        for (TaskEvent event : events) {
            latestEventByTaskId.put(event.getTaskId(), event);
        }

        List<TaskSnapshotResponse> taskSnapshots = new ArrayList<>();

        for (TaskEvent latestEvent : latestEventByTaskId.values()) {
            TaskStatus snapshotStatus = latestEvent.getToStatus();

            if (snapshotStatus == TaskStatus.DELETED) {
                continue;
            }

            Task task = taskMap.get(latestEvent.getTaskId());

            if (task == null) {
                continue;
            }

            taskSnapshots.add(new TaskSnapshotResponse(
                    task.getId(),
                    task.getTitle(),
                    snapshotStatus.name(),
                    latestEvent.getCreatedAt()
            ));
        }

        int todoCount = countByStatus(taskSnapshots, TaskStatus.TODO);
        int inProgressCount = countByStatus(taskSnapshots, TaskStatus.IN_PROGRESS);
        int blockedCount = countByStatus(taskSnapshots, TaskStatus.BLOCKED);
        int doneCount = countByStatus(taskSnapshots, TaskStatus.DONE);

        return new ProjectSnapshotResponse(
                project.getId(),
                project.getName(),
                at,
                taskSnapshots.size(),
                todoCount,
                inProgressCount,
                blockedCount,
                doneCount,
                taskSnapshots
        );
    }

    private int countByStatus(List<TaskSnapshotResponse> tasks, TaskStatus status) {
        return (int) tasks.stream()
                .filter(task -> task.getStatus().equals(status.name()))
                .count();
    }
}