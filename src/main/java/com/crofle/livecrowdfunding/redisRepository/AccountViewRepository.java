package com.crofle.livecrowdfunding.redisRepository;

import com.crofle.livecrowdfunding.domain.entity.AccountView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountViewRepository extends JpaRepository<AccountView, Long> {

    // 이메일로 계정 정보 조회
    Optional<AccountView> findByEmail(String email);

    // 이름과 전화번호로 계정 정보 조회
    @Query("SELECT DISTINCT u FROM AccountView u WHERE u.name = :name AND u.phone = :phone")
    Optional<AccountView> findEmailByNameAndPhone(@Param("name") String name, @Param("phone") String phone);

    // 이름, 이메일, 전화번호로 특정 이메일 조회
    @Query("SELECT u.email FROM AccountView u WHERE u.name = :name AND u.email = :email AND u.phone = :phone")
    Optional<String> findEmailByNameAndMailAndPhone(@Param("name") String name, @Param("email") String email, @Param("phone") String phone);
}
