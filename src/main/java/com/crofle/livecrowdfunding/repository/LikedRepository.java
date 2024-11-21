package com.crofle.livecrowdfunding.repository;

import com.crofle.livecrowdfunding.domain.entity.Liked;
import com.crofle.livecrowdfunding.domain.entity.Project;
import com.crofle.livecrowdfunding.domain.entity.User;
import com.crofle.livecrowdfunding.domain.id.LikedId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikedRepository extends JpaRepository<Liked, LikedId> {

    @Query("SELECT l.project FROM Liked l WHERE l.user.id = :userId ORDER BY l.createdAt DESC")
    Page<Project> findByUser(@Param("userId") Long userId, Pageable pageable);
}
