package com.ahyeon.flowbit.domain.project;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectInitializer implements CommandLineRunner {

    private final ProjectService projectService;

    @Override
    public void run(String... args) {
        projectService.getOrCreateDefaultProject();
    }
}