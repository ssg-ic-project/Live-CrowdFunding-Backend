package com.crofle.livecrowdfunding.repository;

import com.crofle.livecrowdfunding.domain.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
