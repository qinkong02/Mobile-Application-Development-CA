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

    List<WellnessLog> findByUser_IdOrderByLogDateDesc(Long userId);

    Optional<WellnessLog> findByIdAndUser_Id(Long id, Long userId);

    List<WellnessLog> findByUser_IdAndLogDateAfter(Long userId, LocalDate logDate);
}