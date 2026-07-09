package com.example.wellness_backend.utils;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
//Author:Zhang Yuhao
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取请求路径
        String path = request.getRequestURI();
        System.out.println("=== 拦截器拦截路径: " + path + " ===");

        //放行登录和注册接口
        if (path.equals("/api/auth/login") || path.equals("/api/auth/register")) {
            System.out.println("=== 放行: " + path + " ===");
            return true;
        }

        //从请求头中获取 Token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return false;
        }

        // 去掉 "Bearer " 前缀
        token = token.substring(7);

        // 验证 Token
        if (!jwtUtils.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return false;
        }

        //验证通过，从 Token 中提取 userId 并存入request
        Long userId = jwtUtils.getUserIdFromToken(token);
        request.setAttribute("currentUserId", userId);

        return true;
    }
}