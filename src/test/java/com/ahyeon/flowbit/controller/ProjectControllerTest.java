package com.ahyeon.flowbit.controller;

import com.ahyeon.flowbit.domain.project.ProjectService;
import com.ahyeon.flowbit.domain.project.dto.ProjectResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @Test
    @DisplayName("GET /api/projects 요청 시 프로젝트 목록을 반환한다")
    void getProjects() throws Exception {
        ProjectResponse response = mock(ProjectResponse.class);

        when(response.getId()).thenReturn(1L);
        when(response.getName()).thenReturn("Flowbit MVP");
        when(response.getDescription()).thenReturn("이벤트 기반 작업 흐름 관리 시스템");
        when(response.getStatus()).thenReturn("READY");
        when(response.getCreatedAt()).thenReturn(LocalDateTime.of(2026, 5, 3, 10, 0));

        when(projectService.getProjects()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Flowbit MVP"))
                .andExpect(jsonPath("$[0].status").value("READY"));

        verify(projectService).getProjects();
    }

    @Test
    @DisplayName("POST /api/projects 요청 시 프로젝트를 생성한다")
    void createProject() throws Exception {
        ProjectResponse response = mock(ProjectResponse.class);

        when(response.getId()).thenReturn(1L);
        when(response.getName()).thenReturn("Flowbit MVP");
        when(response.getDescription()).thenReturn("이벤트 기반 작업 흐름 관리 시스템");
        when(response.getStatus()).thenReturn("READY");
        when(response.getCreatedAt()).thenReturn(LocalDateTime.of(2026, 5, 3, 10, 0));

        when(projectService.createProject(any())).thenReturn(response);

        mockMvc.perform(post("/api/projects")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "Flowbit MVP",
                                  "description": "이벤트 기반 작업 흐름 관리 시스템"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Flowbit MVP"))
                .andExpect(jsonPath("$.status").value("READY"));

        verify(projectService).createProject(any());
    }
}