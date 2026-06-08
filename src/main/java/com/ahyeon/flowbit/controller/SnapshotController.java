package com.ahyeon.flowbit.controller;

import com.ahyeon.flowbit.domain.snapshot.SnapshotService;
import com.ahyeon.flowbit.domain.snapshot.dto.ProjectSnapshotResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:5173")
public class SnapshotController {

    private final SnapshotService snapshotService;

    @GetMapping("/{projectId}/snapshot")
    public ProjectSnapshotResponse getProjectSnapshot(
            @PathVariable Long projectId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime at,
            @RequestParam(defaultValue = "false")
            boolean includeDeleted
    ) {
        return snapshotService.getProjectSnapshot(projectId, at, includeDeleted);
    }
}