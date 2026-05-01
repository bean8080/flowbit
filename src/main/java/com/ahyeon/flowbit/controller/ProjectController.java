package com.ahyeon.flowbit.controller;

import com.ahyeon.flowbit.domain.project.ProjectService;
import com.ahyeon.flowbit.domain.project.dto.CreateProjectRequest;
import com.ahyeon.flowbit.domain.project.dto.ProjectResponse;
import com.ahyeon.flowbit.domain.project.dto.ProjectTimelineResponse;
import com.ahyeon.flowbit.domain.project.dto.ProjectAnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectResponse createProject(@RequestBody CreateProjectRequest request) {
        return projectService.createProject(request);
    }

    @GetMapping
    public List<ProjectResponse> getProjects() {
        return projectService.getProjects();
    }

    @GetMapping("/{id}")
    public ProjectResponse getProject(@PathVariable Long id) {
        return projectService.getProject(id);
    }

    @GetMapping("/{id}/timeline")
    public List<ProjectTimelineResponse> getProjectTimeline(@PathVariable Long id) {
        return projectService.getProjectTimeline(id);
    }

    @GetMapping("/{id}/analysis")
    public ProjectAnalysisResponse getProjectAnalysis(@PathVariable Long id) {
        return projectService.getProjectAnalysis(id);
    }
}