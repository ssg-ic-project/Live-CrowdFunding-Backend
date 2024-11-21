package com.crofle.livecrowdfunding.repository;

import com.crofle.livecrowdfunding.domain.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
