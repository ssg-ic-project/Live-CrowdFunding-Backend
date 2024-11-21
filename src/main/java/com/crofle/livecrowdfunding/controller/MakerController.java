package com.crofle.livecrowdfunding.controller;

import com.crofle.livecrowdfunding.dto.request.MakerInfoRequestDTO;
import com.crofle.livecrowdfunding.dto.request.SaveMakerRequestDTO;
import com.crofle.livecrowdfunding.dto.response.MakerInfoResponseDTO;
import com.crofle.livecrowdfunding.service.MakerService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maker")
@RequiredArgsConstructor
public class MakerController {

    private final MakerService makerService;

    @GetMapping("/{id}")
    public ResponseEntity<MakerInfoResponseDTO> findMaker(@PathVariable Long id) {
        return ResponseEntity.ok(makerService.findMaker(id));
    }

    //Patch로 변경 예정
    @PostMapping("/{id}")
    public ResponseEntity<Void> updateMaker(@PathVariable Long id, @RequestBody MakerInfoRequestDTO makerInfoRequestDTO) {
        makerService.updateMaker(id, makerInfoRequestDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<SaveMakerRequestDTO> saveMaker(@RequestBody SaveMakerRequestDTO request) {
        return ResponseEntity.ok(makerService.saveMaker(request));
    }
}

