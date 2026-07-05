package com.example.wellness_backend.utils;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.JWTPayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    // 从配置文件读取密钥和有效期
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire:604800}") // 默认 7 天，单位秒
    private long expire;

    /**
     * 生成 Token
     * 1. 准备载荷（用户ID + 标准时间字段）
     * 2. 调用 Hutool 的 JWTUtil 一行生成
     */
    public String generateToken(Long userId) {
        Map<String, Object> payload = new HashMap<>();
        Date now = new Date();
        // 自定义数据
        payload.put("userId", userId);
        // 标准字段（JWTUtil.createToken 不会自动设置过期时间，需要手动塞）
        payload.put(JWTPayload.ISSUED_AT, now);
        payload.put(JWTPayload.EXPIRES_AT, new Date(now.getTime() + expire * 1000));

        return JWTUtil.createToken(payload, secret.getBytes());
    }

    /**
     * 验证 Token（签名 + 过期时间）
     * 使用 JWTUtil.verify 验签，用 JWTValidator 验过期
     */
    public boolean validateToken(String token) {
        try {
            // 1. 验签
            if (!JWTUtil.verify(token, secret.getBytes())) {
                return false;
            }
            // 2. 验过期（Hutool 推荐使用 JWTValidator 来验证时间）[citation:2]
            JWTValidator.of(token).validateDate(new Date());
            return true;
        } catch (Exception e) {
            // 任何异常都视为无效
            return false;
        }
    }

    /**
     * 从 Token 中提取 userId
     */
    public Long getUserIdFromToken(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        return Long.valueOf(jwt.getPayload("userId").toString());
    }
}