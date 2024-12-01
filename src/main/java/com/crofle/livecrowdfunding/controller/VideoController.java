package com.crofle.livecrowdfunding.controller;

import com.crofle.livecrowdfunding.domain.entity.Video;
import com.crofle.livecrowdfunding.dto.response.VideoResponseDTO;
import com.crofle.livecrowdfunding.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recordings")
public class VideoController {
    private final VideoService videoService;

    @PostMapping
    public ResponseEntity<VideoResponseDTO> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("scheduleId") Long scheduleId) {
        Video video = videoService.uploadVideo(file, scheduleId);
        return ResponseEntity.ok(new VideoResponseDTO(video.getMediaUrl()));
    }

    @GetMapping("/media/{scheduleId}")
    public ResponseEntity<String> greeting(@PathVariable(name="scheduleId") Long scheduleId) {
        String mediaUrl = videoService.getMediaUrl(scheduleId);
        log.info(mediaUrl);
        return ResponseEntity.ok(mediaUrl);
    }
}