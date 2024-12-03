package com.crofle.livecrowdfunding.repository;

import com.crofle.livecrowdfunding.domain.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {

    @Query("SELECT m FROM Manager m WHERE m.identificationNumber = :identificationNumber")
    Optional<Manager> findManager(@Param("identificationNumber") String identificationNumber);
}
