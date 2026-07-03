package com.example.wellness_backend.repository;

import com.example.wellness_backend.entity.WellnessLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Author: Boliang Wang
 * Repository for wellness log database operations.
 */
public interface WellnessLogRepository extends JpaRepository<WellnessLog, Long> {

    List<WellnessLog> findByUserIdOrderByLogDateDesc(Long userId);

    Optional<WellnessLog> findByIdAndUserId(Long id, Long userId);

    List<WellnessLog> findByUserIdAndLogDateAfter(Long userId, LocalDate logDate);
}
