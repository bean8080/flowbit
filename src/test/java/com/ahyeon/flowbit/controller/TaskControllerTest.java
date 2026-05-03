package com.ahyeon.flowbit.controller;

import com.ahyeon.flowbit.domain.task.TaskService;
import com.ahyeon.flowbit.domain.task.TaskStatus;
import com.ahyeon.flowbit.domain.task.dto.TaskResponse;
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

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    @DisplayName("GET /api/tasks 요청 시 작업 목록을 반환한다")
    void getTasks() throws Exception {
        TaskResponse response = mock(TaskResponse.class);

        when(response.getId()).thenReturn(1L);
        when(response.getProjectId()).thenReturn(1L);
        when(response.getProjectName()).thenReturn("Flowbit MVP");
        when(response.getTitle()).thenReturn("배포 준비");
        when(response.getDescription()).thenReturn("EC2 배포 전 테스트 코드 작성");
        when(response.getStatus()).thenReturn("TODO");
        when(response.getAssigneeId()).thenReturn(1L);
        when(response.getPriority()).thenReturn(1);
        when(response.getCreatedAt()).thenReturn(LocalDateTime.of(2026, 5, 3, 10, 0));

        when(taskService.getTasks(null, null)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("배포 준비"))
                .andExpect(jsonPath("$[0].status").value("TODO"))
                .andExpect(jsonPath("$[0].projectName").value("Flowbit MVP"));

        verify(taskService).getTasks(null, null);
    }

    @Test
    @DisplayName("GET /api/tasks?status=TODO 요청 시 상태 조건으로 작업 목록을 조회한다")
    void getTasksByStatus() throws Exception {
        when(taskService.getTasks(TaskStatus.TODO, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/tasks")
                        .param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(taskService).getTasks(TaskStatus.TODO, null);
    }

    @Test
    @DisplayName("POST /api/tasks 요청 시 작업을 생성한다")
    void createTask() throws Exception {
        TaskResponse response = mock(TaskResponse.class);

        when(response.getId()).thenReturn(1L);
        when(response.getProjectId()).thenReturn(1L);
        when(response.getProjectName()).thenReturn("Flowbit MVP");
        when(response.getTitle()).thenReturn("배포 준비");
        when(response.getDescription()).thenReturn("EC2 배포 전 테스트 코드 작성");
        when(response.getStatus()).thenReturn("TODO");
        when(response.getAssigneeId()).thenReturn(1L);
        when(response.getPriority()).thenReturn(1);
        when(response.getCreatedAt()).thenReturn(LocalDateTime.of(2026, 5, 3, 10, 0));

        when(taskService.createTask(any())).thenReturn(response);

        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content("""
                                {
                                  "projectId": 1,
                                  "title": "배포 준비",
                                  "description": "EC2 배포 전 테스트 코드 작성",
                                  "assigneeId": 1,
                                  "priority": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("배포 준비"))
                .andExpect(jsonPath("$.status").value("TODO"));

        verify(taskService).createTask(any());
    }

    @Test
    @DisplayName("PATCH /api/tasks/{id}/start 요청 시 작업을 시작 상태로 변경한다")
    void startTask() throws Exception {
        TaskResponse response = mock(TaskResponse.class);

        when(response.getId()).thenReturn(1L);
        when(response.getProjectId()).thenReturn(1L);
        when(response.getProjectName()).thenReturn("Flowbit MVP");
        when(response.getTitle()).thenReturn("배포 준비");
        when(response.getStatus()).thenReturn("IN_PROGRESS");

        when(taskService.startTask(1L)).thenReturn(response);

        mockMvc.perform(patch("/api/tasks/1/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        verify(taskService).startTask(1L);
    }
}