package com.crofle.livecrowdfunding.repository;

import com.crofle.livecrowdfunding.domain.id.UserCategoryId;
import com.crofle.livecrowdfunding.domain.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInterestRepository extends JpaRepository<UserInterest, UserCategoryId> {
}
