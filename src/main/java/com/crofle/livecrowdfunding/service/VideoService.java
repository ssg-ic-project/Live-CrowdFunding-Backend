package com.crofle.livecrowdfunding.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.crofle.livecrowdfunding.domain.entity.Schedule;
import com.crofle.livecrowdfunding.domain.entity.Video;
import com.crofle.livecrowdfunding.repository.ScheduleRepository;
import com.crofle.livecrowdfunding.repository.VideoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoService {

    @Value("${ncp.storage.bucket}")
    private String bucket;

    private final VideoRepository videoRepository;
    private final ScheduleRepository scheduleRepository;
    private final AmazonS3 amazonS3;

    @Transactional
    public Video uploadVideo(MultipartFile file, Long scheduleId) {
        String mediaUrl = uploadToNcp(file);
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));

        Video video = Video.builder()
//                .id(scheduleId)
                .schedule(schedule)
                .mediaUrl(mediaUrl)
                .build();

        log.info(video.toString());

        videoRepository.save(video);

        return video;
    }

    private String uploadToNcp(MultipartFile file) {
        log.info(String.valueOf(file));

        String folderName = "videos/";
        String fileName = folderName + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            // Create folder
            ObjectMetadata folderMetadata = new ObjectMetadata();
            folderMetadata.setContentLength(0L);
            folderMetadata.setContentType("application/x-directory");
            amazonS3.putObject(new PutObjectRequest(bucket, folderName,
                    new ByteArrayInputStream(new byte[0]), folderMetadata));

            // Upload file
            ObjectMetadata fileMetadata = new ObjectMetadata();
            fileMetadata.setContentType(file.getContentType());
            fileMetadata.setContentLength(file.getSize());

            amazonS3.putObject(new PutObjectRequest(bucket, fileName,
                    file.getInputStream(), fileMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            return String.format("https://%s.kr.object.ncloudstorage.com/%s", bucket, fileName);

        } catch (AmazonS3Exception | IOException e) {
            throw new RuntimeException("Failed to upload video", e);
        } catch (SdkClientException e) {
            throw new RuntimeException("AWS SDK client error", e);
        }
    }

    public String getMediaUrl(Long scheduleId) {
        Video video = videoRepository.findById(scheduleId).orElseThrow();

        String mediaUrl = video.getMediaUrl();
        return mediaUrl;
    }
}