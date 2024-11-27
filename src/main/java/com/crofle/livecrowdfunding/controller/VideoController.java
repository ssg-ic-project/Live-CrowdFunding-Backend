//package com.crofle.livecrowdfunding.controller;
//
//import com.crofle.livecrowdfunding.domain.entity.Video;
//import com.crofle.livecrowdfunding.dto.response.VideoResponseDTO;
//import com.crofle.livecrowdfunding.service.VideoService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/recordings")
//public class VideoController {
//    private final VideoService videoService;
//
//    @PostMapping
//    public ResponseEntity<VideoResponseDTO> uploadVideo(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("scheduleId") Long scheduleId) {
//        Video video = videoService.uploadVideo(file, scheduleId);
//        return ResponseEntity.ok(new VideoResponseDTO(video.getMediaUrl()));
//    }
//}