package com.lifestyle.platform.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifestyle.platform.common.Result;
import com.lifestyle.platform.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");

        if (StringUtils.isNotBlank(token)) {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            try {
                if (JwtUtil.validateToken(token)) {
                    Claims claims = JwtUtil.parseToken(token);
                    Long userId = Long.valueOf(claims.get("userId").toString());
                    Integer role = Integer.valueOf(claims.get("role").toString());
                    request.setAttribute("userId", userId);
                    request.setAttribute("role", role);
                    return true;
                }
            } catch (Exception e) {
                // token无效
            }
        }

        if (isOptionalAuth(request)) {
            return true;
        }

        sendError(response, 401, "未登录或登录已过期");
        return false;
    }

    private boolean isOptionalAuth(HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        if (!"GET".equalsIgnoreCase(method)) {
            return false;
        }

        if (uri.matches("/api/notes/\\d+")) return true;
        if ("/api/notes".equals(uri)) return true;
        if ("/api/notes/hot".equals(uri)) return true;
        if ("/api/categories".equals(uri)) return true;
        if (uri.matches("/api/comments/note/\\d+")) return true;
        if (uri.matches("/api/users/\\d+")) return true;
        if (uri.matches("/api/users/\\d+/notes")) return true;
        if (uri.matches("/api/users/\\d+/followers")) return true;
        if (uri.matches("/api/users/\\d+/following")) return true;

        return false;
    }

    private void sendError(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(code);
        response.setContentType("application/json;charset=UTF-8");
        Result<?> result = Result.error(code, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
