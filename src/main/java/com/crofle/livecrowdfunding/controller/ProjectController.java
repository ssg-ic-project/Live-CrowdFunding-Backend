package com.crofle.livecrowdfunding.controller;

import com.crofle.livecrowdfunding.dto.request.*;
import com.crofle.livecrowdfunding.dto.response.*;
import com.crofle.livecrowdfunding.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDetailResponseDTO> getProject(@PathVariable Long id) {

        return ResponseEntity.ok(projectService.getProjectForUser(id));
    }

    @PostMapping
    public ResponseEntity<Void> createProject(@RequestBody ProjectRegisterRequestDTO requestDTO) {
        projectService.createProject(requestDTO);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("{id}/status/")
    public ResponseEntity<Void> updateProjectStatus(@PathVariable Long id, @RequestBody ProjectStatusRequestDTO requestDTO) {
        projectService.updateProjectStatus(id, requestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/update")
    public ResponseEntity<ProjectDetailToUpdateResponseDTO> getProjectToUpdate(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectForManagerUpdate(id));
    }

    @GetMapping("/{id}/maker")
    public ResponseEntity<ProjectDetailForMakerResponseDTO> getProjectForMaker(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectForMaker(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateProject(@PathVariable Long id, @RequestBody ProjectUpdateRequestDTO requestDTO) {
        projectService.updateProject(id, requestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<PageListResponseDTO<ProjectListResponseDTO>> getProjectList(@RequestBody ProjectListRequestDTO requestDTO, @ModelAttribute PageRequestDTO pageRequestDTO) {
        return ResponseEntity.ok(projectService.getProjectList(requestDTO, pageRequestDTO));
    }
}
