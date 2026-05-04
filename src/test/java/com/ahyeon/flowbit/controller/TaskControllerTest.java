package com.ahyeon.flowbit.controller;

import com.ahyeon.flowbit.domain.task.TaskService;
import com.ahyeon.flowbit.domain.task.TaskStatus;
import com.ahyeon.flowbit.domain.task.dto.TaskResponse;
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

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
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

    @Test
    @DisplayName("존재하지 않는 작업 조회 시 400을 반환한다")
    void getTask_notFound() throws Exception {
        when(taskService.getTask(999L))
                .thenThrow(new IllegalArgumentException("작업을 찾을 수 없습니다."));

        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("작업을 찾을 수 없습니다."))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(taskService).getTask(999L);
    }

    @Test
    @DisplayName("존재하지 않는 작업 시작 시 400을 반환한다")
    void startTask_notFound() throws Exception {
        when(taskService.startTask(999L))
                .thenThrow(new IllegalArgumentException("작업을 찾을 수 없습니다."));

        mockMvc.perform(patch("/api/tasks/999/start"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("작업을 찾을 수 없습니다."))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(taskService).startTask(999L);
    }

    @Test
    @DisplayName("존재하지 않는 작업 완료 시 400을 반환한다")
    void completeTask_notFound() throws Exception {
        when(taskService.completeTask(999L))
                .thenThrow(new IllegalArgumentException("작업을 찾을 수 없습니다."));

        mockMvc.perform(patch("/api/tasks/999/complete"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("작업을 찾을 수 없습니다."))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(taskService).completeTask(999L);
    }

    @Test
    @DisplayName("존재하지 않는 작업 보류 시 400을 반환한다")
    void blockTask_notFound() throws Exception {
        when(taskService.blockTask(999L))
                .thenThrow(new IllegalArgumentException("작업을 찾을 수 없습니다."));

        mockMvc.perform(patch("/api/tasks/999/block"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("작업을 찾을 수 없습니다."))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(taskService).blockTask(999L);
    }

    @Test
    @DisplayName("존재하지 않는 작업 삭제 시 400을 반환한다")
    void deleteTask_notFound() throws Exception {
        when(taskService.deleteTask(999L))
                .thenThrow(new IllegalArgumentException("작업을 찾을 수 없습니다."));

        mockMvc.perform(patch("/api/tasks/999/delete"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("작업을 찾을 수 없습니다."))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(taskService).deleteTask(999L);
    }

    @Test
    @DisplayName("존재하지 않는 작업 이벤트 조회 시 400을 반환한다")
    void getTaskEvents_notFound() throws Exception {
        when(taskService.getTaskEvents(999L))
                .thenThrow(new IllegalArgumentException("작업을 찾을 수 없습니다."));

        mockMvc.perform(get("/api/tasks/999/events"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("작업을 찾을 수 없습니다."))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(taskService).getTaskEvents(999L);
    }

    @Test
    @DisplayName("존재하지 않는 작업 타임라인 조회 시 400을 반환한다")
    void getTimeline_notFound() throws Exception {
        when(taskService.getTimeline(999L))
                .thenThrow(new IllegalArgumentException("작업을 찾을 수 없습니다."));

        mockMvc.perform(get("/api/tasks/999/timeline"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("작업을 찾을 수 없습니다."))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(taskService).getTimeline(999L);
    }

    @Test
    @DisplayName("잘못된 status 파라미터 요청 시 400을 반환한다")
    void getTasks_invalidStatus() throws Exception {
        mockMvc.perform(get("/api/tasks")
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("요청 파라미터 값이 올바르지 않습니다."))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(taskService, never()).getTasks(any(), any());
    }
}