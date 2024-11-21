package com.crofle.livecrowdfunding.repository;

import com.crofle.livecrowdfunding.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
