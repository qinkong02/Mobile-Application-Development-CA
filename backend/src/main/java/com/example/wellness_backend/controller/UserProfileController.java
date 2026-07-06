package com.example.wellness_backend.controller;

import com.example.wellness_backend.dto.ApiResponse;
import com.example.wellness_backend.dto.UserProfileDTO;
import com.example.wellness_backend.entity.User;
import com.example.wellness_backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author XieMaonan
 * 身高/体重/年龄/性别等资料的读写接口，供 chatbot 的 WellnessTools 做个性化建议使用。
 */
@RestController
@RequestMapping("/api/user")
public class UserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public ApiResponse<UserProfileDTO> getProfile(HttpServletRequest request) {
        try {
            Long currentUserId = (Long) request.getAttribute("currentUserId");
            User user = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            UserProfileDTO dto = new UserProfileDTO(
                    user.getHeightCm(), user.getWeightKg(), user.getAge(), user.getGender());
            return ApiResponse.success(dto);
        } catch (Exception e) {
            logger.error("获取用户资料异常", e);
            return ApiResponse.error("系统异常，请稍后重试");
        }
    }

    @PutMapping("/profile")
    public ApiResponse<UserProfileDTO> updateProfile(@RequestBody UserProfileDTO dto, HttpServletRequest request) {
        try {
            Long currentUserId = (Long) request.getAttribute("currentUserId");
            User user = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            user.setHeightCm(dto.getHeightCm());
            user.setWeightKg(dto.getWeightKg());
            user.setAge(dto.getAge());
            user.setGender(dto.getGender());
            userRepository.save(user);

            return ApiResponse.success("Profile updated successfully", dto);
        } catch (Exception e) {
            logger.error("更新用户资料异常", e);
            return ApiResponse.error("系统异常，请稍后重试");
        }
    }
}
