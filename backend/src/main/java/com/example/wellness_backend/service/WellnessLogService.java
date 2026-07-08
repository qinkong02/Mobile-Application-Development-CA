package com.example.wellness_backend.service;

import com.example.wellness_backend.entity.User;
import com.example.wellness_backend.entity.WellnessLog;
import com.example.wellness_backend.repository.UserRepository;
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
    private final UserRepository userRepository;

    public WellnessLogService(WellnessLogRepository wellnessLogRepository, UserRepository userRepository) {
        this.wellnessLogRepository = wellnessLogRepository;
        this.userRepository = userRepository;
    }

    public WellnessLog createLog(Long userId, WellnessLog log) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.setId(null);
        log.setUser(user);

        return wellnessLogRepository.save(log);
    }

    public List<WellnessLog> getAllLogs(Long userId) {
        return wellnessLogRepository.findByUser_IdOrderByLogDateDesc(userId);
    }

    public WellnessLog getLogById(Long userId, Long id) {
        return wellnessLogRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new RuntimeException("Wellness log not found"));
    }

    public WellnessLog updateLog(Long userId, Long id, WellnessLog newLog) {
        WellnessLog oldLog = wellnessLogRepository.findByIdAndUser_Id(id, userId)
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
        WellnessLog log = wellnessLogRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new RuntimeException("Wellness log not found"));

        wellnessLogRepository.delete(log);
    }
}