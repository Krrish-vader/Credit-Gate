package com.creditgate.repository;

import com.creditgate.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data Repository for LoanApplication.
 */
@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, UUID> {
}
