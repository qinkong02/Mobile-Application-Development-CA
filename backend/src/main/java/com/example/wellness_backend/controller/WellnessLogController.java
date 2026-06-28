package com.example.wellness_backend.controller;

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
    public WellnessLog createLog(@RequestBody WellnessLog log) {
        Long userId = 1L; // temporary user id before login/JWT is finished
        return wellnessLogService.createLog(userId, log);
    }

    @GetMapping
    public List<WellnessLog> getAllLogs() {
        Long userId = 1L;
        return wellnessLogService.getAllLogs(userId);
    }

    @GetMapping("/{id}")
    public WellnessLog getOneLog(@PathVariable Long id) {
        Long userId = 1L;
        return wellnessLogService.getLogById(userId, id);
    }

    @PutMapping("/{id}")
    public WellnessLog updateLog(@PathVariable Long id, @RequestBody WellnessLog log) {
        Long userId = 1L;
        return wellnessLogService.updateLog(userId, id, log);
    }

    @DeleteMapping("/{id}")
    public String deleteLog(@PathVariable Long id) {
        Long userId = 1L;
        wellnessLogService.deleteLog(userId, id);
        return "Deleted successfully";
    }
}
