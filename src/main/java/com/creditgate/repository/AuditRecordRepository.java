package com.creditgate.repository;

import com.creditgate.entity.AuditRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data Repository for AuditRecord.
 */
@Repository
public interface AuditRecordRepository extends JpaRepository<AuditRecord, UUID> {
}
