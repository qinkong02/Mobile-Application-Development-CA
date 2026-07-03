package com.example.wellness_backend.controller;

import com.example.wellness_backend.dto.ApiResponse;
import com.example.wellness_backend.entity.WellnessLog;
import com.example.wellness_backend.service.WellnessLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author: Boliang Wang
 * Controller for wellness log CRUD APIs.
 */
@RestController
@RequestMapping("/api/wellness")
@CrossOrigin(origins = "*")
public class WellnessLogController {

    private final WellnessLogService wellnessLogService;

    public WellnessLogController(WellnessLogService wellnessLogService) {
        this.wellnessLogService = wellnessLogService;
    }

    @PostMapping
    public ApiResponse<WellnessLog> createLog(@RequestBody WellnessLog log) {
        Long userId = 1L; // temporary user id before login/JWT is finished

        WellnessLog savedLog = wellnessLogService.createLog(userId, log);

        return ApiResponse.success("Wellness log created successfully", savedLog);
    }

    @GetMapping
    public ApiResponse<List<WellnessLog>> getAllLogs() {
        Long userId = 1L; // temporary user id before login/JWT is finished

        List<WellnessLog> logs = wellnessLogService.getAllLogs(userId);

        return ApiResponse.success(logs);
    }

    @GetMapping("/{id}")
    public ApiResponse<WellnessLog> getOneLog(@PathVariable Long id) {
        Long userId = 1L; // temporary user id before login/JWT is finished

        WellnessLog log = wellnessLogService.getLogById(userId, id);

        return ApiResponse.success(log);
    }

    @PutMapping("/{id}")
    public ApiResponse<WellnessLog> updateLog(@PathVariable Long id,
                                              @RequestBody WellnessLog log) {
        Long userId = 1L; // temporary user id before login/JWT is finished

        WellnessLog updatedLog = wellnessLogService.updateLog(userId, id, log);

        return ApiResponse.success("Wellness log updated successfully", updatedLog);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteLog(@PathVariable Long id) {
        Long userId = 1L; // temporary user id before login/JWT is finished

        wellnessLogService.deleteLog(userId, id);

        return ApiResponse.success("Deleted successfully", null);
    }
}