package com.ahyeon.flowbit.controller;

import com.ahyeon.flowbit.domain.project.ProjectService;
import com.ahyeon.flowbit.domain.project.dto.CreateProjectRequest;
import com.ahyeon.flowbit.domain.project.dto.ProjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public void createProject(@RequestBody CreateProjectRequest request) {
        projectService.createProject(request);
    }

    @GetMapping
    public List<ProjectResponse> getProjects() {
        return projectService.getProjects();
    }

    @GetMapping("/{id}")
    public ProjectResponse getProject(@PathVariable Long id) {
        return projectService.getProject(id);
    }
}