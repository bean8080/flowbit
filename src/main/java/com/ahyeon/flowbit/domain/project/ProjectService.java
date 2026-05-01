package com.ahyeon.flowbit.domain.project;

import com.ahyeon.flowbit.domain.project.dto.CreateProjectRequest;
import com.ahyeon.flowbit.domain.project.dto.ProjectResponse;
import com.ahyeon.flowbit.domain.project.dto.ProjectTimelineResponse;
import com.ahyeon.flowbit.domain.project.dto.ProjectAnalysisResponse;
import com.ahyeon.flowbit.domain.task.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskEventRepository taskEventRepository;

    public ProjectResponse createProject(CreateProjectRequest request) {

        Project project = new Project(
                request.getName(),
                request.getDescription(),
                ProjectStatus.READY,
                LocalDateTime.now()
        );

        Project savedProject = projectRepository.save(project);

        return new ProjectResponse(savedProject);
    }

    public List<ProjectResponse> getProjects() {

        return projectRepository.findByStatusNot(ProjectStatus.DELETED)
                .stream()
                .map(ProjectResponse::new)
                .toList();
    }

    public ProjectResponse getProject(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        return new ProjectResponse(project);
    }

    public Project getOrCreateDefaultProject() {

        return projectRepository.findByName("DEFAULT")
                .orElseGet(() -> {
                    Project defaultProject = new Project(
                            "DEFAULT",
                            "기본 프로젝트",
                            ProjectStatus.READY,
                            LocalDateTime.now()
                    );
                    return projectRepository.save(defaultProject);
                });
    }

    public List<ProjectTimelineResponse> getProjectTimeline(Long projectId) {

        projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        List<Task> tasks = taskRepository.findByProject_IdAndStatusNot(projectId, TaskStatus.DELETED);

        List<Long> taskIds = tasks.stream()
                .map(Task::getId)
                .toList();

        List<TaskEvent> events = taskEventRepository.findByTaskIdInOrderByCreatedAtAsc(taskIds);

        return events.stream()
                .map(ProjectTimelineResponse::new)
                .toList();
    }

    @Cacheable(value = "projectAnalysis", key = "#projectId")
    public ProjectAnalysisResponse getProjectAnalysis(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        List<Task> tasks = taskRepository.findByProject_IdAndStatusNot(projectId, TaskStatus.DELETED);

        List<Long> taskIds = tasks.stream()
                .map(Task::getId)
                .toList();

        List<TaskEvent> events = taskIds.isEmpty()
                ? List.of()
                : taskEventRepository.findByTaskIdInOrderByCreatedAtAsc(taskIds);

        int totalTaskCount = tasks.size();

        int todoCount = countByStatus(tasks, TaskStatus.TODO);
        int inProgressCount = countByStatus(tasks, TaskStatus.IN_PROGRESS);
        int blockedCount = countByStatus(tasks, TaskStatus.BLOCKED);
        int doneCount = countByStatus(tasks, TaskStatus.DONE);

        LocalDateTime lastEventAt = events.isEmpty()
                ? null
                : events.get(events.size() - 1).getCreatedAt();

        return new ProjectAnalysisResponse(
                project.getId(),
                project.getName(),
                totalTaskCount,
                todoCount,
                inProgressCount,
                blockedCount,
                doneCount,
                events.size(),
                lastEventAt
        );
    }

    private int countByStatus(List<Task> tasks, TaskStatus status) {
        return (int) tasks.stream()
                .filter(task -> task.getStatus() == status)
                .count();
    }
}