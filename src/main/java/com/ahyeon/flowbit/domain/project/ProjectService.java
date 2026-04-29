package com.ahyeon.flowbit.domain.project;

import com.ahyeon.flowbit.domain.project.dto.CreateProjectRequest;
import com.ahyeon.flowbit.domain.project.dto.ProjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public void createProject(CreateProjectRequest request) {

        Project project = new Project(
                request.getName(),
                request.getDescription(),
                ProjectStatus.READY,
                LocalDateTime.now()
        );

        projectRepository.save(project);
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
}