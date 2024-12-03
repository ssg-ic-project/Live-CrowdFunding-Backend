package com.crofle.livecrowdfunding.service.video;

import com.crofle.livecrowdfunding.domain.entity.Video;
import com.crofle.livecrowdfunding.repository.VideoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
public class VideoServiceTest {

    @Autowired
    VideoRepository videoRepository;

    @Test
    public void TestMediaUrl() {
        // given: 테스트 실행을 준비하는 단계
        Long scheduleId = 4L;
        String mediaUrl = "111https://crofle-media-bucket.kr.object.ncloudstorage.com/videos/32e4b6d4-cfef-4281-ba26-f56cc1d48ee7_recording-1732846680529.webm";

        // when: 테스트를 진행하는 단계
        Video video = videoRepository.findById(scheduleId).orElseThrow();
        String url = video.getMediaUrl();

        // then: 테스트 결과를 검증하는 단계
        assertThat(url).isEqualTo(mediaUrl);
    }
}
