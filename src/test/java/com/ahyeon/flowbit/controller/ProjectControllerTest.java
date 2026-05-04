package com.ahyeon.flowbit.controller;

import com.ahyeon.flowbit.domain.project.ProjectService;
import com.ahyeon.flowbit.domain.project.dto.ProjectResponse;
import com.ahyeon.flowbit.common.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Import;
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
@Import(GlobalExceptionHandler.class)
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

    @Test
    @DisplayName("존재하지 않는 프로젝트 조회 시 400을 반환한다")
    void getProject_notFound() throws Exception {
        when(projectService.getProject(999L))
                .thenThrow(new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        mockMvc.perform(get("/api/projects/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("프로젝트를 찾을 수 없습니다."))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(projectService).getProject(999L);
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트 수정 시 400을 반환한다")
    void updateProject_notFound() throws Exception {
        when(projectService.updateProject(eq(999L), any()))
                .thenThrow(new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        mockMvc.perform(patch("/api/projects/999")
                        .contentType("application/json")
                        .content("""
                            {
                              "name": "수정된 프로젝트",
                              "description": "수정된 설명"
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("프로젝트를 찾을 수 없습니다."))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(projectService).updateProject(eq(999L), any());
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트 삭제 시 400을 반환한다")
    void deleteProject_notFound() throws Exception {
        when(projectService.deleteProject(999L))
                .thenThrow(new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        mockMvc.perform(patch("/api/projects/999/delete"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("프로젝트를 찾을 수 없습니다."))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(projectService).deleteProject(999L);
    }

    @Test
    @DisplayName("기본 프로젝트 수정 시 409를 반환한다")
    void updateDefaultProject_conflict() throws Exception {
        when(projectService.updateProject(eq(1L), any()))
                .thenThrow(new IllegalStateException("기본 프로젝트는 수정/삭제할 수 없습니다."));

        mockMvc.perform(patch("/api/projects/1")
                        .contentType("application/json")
                        .content("""
                            {
                              "name": "DEFAULT 수정",
                              "description": "수정 불가"
                            }
                            """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("기본 프로젝트는 수정/삭제할 수 없습니다."))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(projectService).updateProject(eq(1L), any());
    }
}