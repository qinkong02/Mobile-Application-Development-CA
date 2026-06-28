package com.example.wellness_backend.service;

import com.example.wellness_backend.entity.WellnessLog;
import com.example.wellness_backend.repository.WellnessLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author: Boliang Wang
 * Service class for handling wellness log CRUD logic.
 */
@Service
public class WellnessLogService {

    private final WellnessLogRepository wellnessLogRepository;

    public WellnessLogService(WellnessLogRepository wellnessLogRepository) {
        this.wellnessLogRepository = wellnessLogRepository;
    }

    public WellnessLog createLog(Long userId, WellnessLog log) {
        log.setId(null);
        log.setUserId(userId);

        return wellnessLogRepository.save(log);
    }

    public List<WellnessLog> getAllLogs(Long userId) {
        return wellnessLogRepository.findByUserIdOrderByLogDateDesc(userId);
    }

    public WellnessLog getLogById(Long userId, Long id) {
        return wellnessLogRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Wellness log not found"));
    }

    public WellnessLog updateLog(Long userId, Long id, WellnessLog newLog) {
        WellnessLog oldLog = wellnessLogRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Wellness log not found"));

        oldLog.setLogDate(newLog.getLogDate());
        oldLog.setSleepHours(newLog.getSleepHours());
        oldLog.setExerciseType(newLog.getExerciseType());
        oldLog.setExerciseMinutes(newLog.getExerciseMinutes());
        oldLog.setMoodScore(newLog.getMoodScore());
        oldLog.setNotes(newLog.getNotes());

        return wellnessLogRepository.save(oldLog);
    }

    public void deleteLog(Long userId, Long id) {
        WellnessLog log = wellnessLogRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Wellness log not found"));

        wellnessLogRepository.delete(log);
    }
}
