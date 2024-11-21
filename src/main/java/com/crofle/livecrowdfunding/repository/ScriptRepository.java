package com.crofle.livecrowdfunding.repository;

import com.crofle.livecrowdfunding.domain.entity.Script;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScriptRepository extends JpaRepository<Script, Long> {
}
