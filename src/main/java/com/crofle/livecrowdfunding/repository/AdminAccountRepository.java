package com.crofle.livecrowdfunding.repository;

import com.crofle.livecrowdfunding.domain.entity.AdminAccountView;
import com.crofle.livecrowdfunding.domain.entity.Manager;
import com.crofle.livecrowdfunding.dto.request.AdminAccountLoginRequestDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdminAccountRepository extends JpaRepository<AdminAccountView, Long> {

//    @Query("SELECT a FROM AdminAccountView a WHERE a.idNum = :idNum")
    Optional<AdminAccountView> findByIdNum (String idNum);


    // Repository에 테스트 메소드 추가
//    @Query(value = "SELECT a FROM admin_account_view a where a.id = 1", nativeQuery = true)
//    AdminAccountView checkViewExists();
}
