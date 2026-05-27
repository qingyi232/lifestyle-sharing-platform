package com.lifestyle.platform.controller;

import com.lifestyle.platform.common.Result;
import com.lifestyle.platform.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public Result<?> getNotifications(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return notificationService.getNotifications(userId);
    }

    @PutMapping("/{id}/read")
    public Result<?> markAsRead(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        return notificationService.markAsRead(userId, id);
    }

    @PutMapping("/read-all")
    public Result<?> markAllAsRead(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return notificationService.markAllAsRead(userId);
    }

    @GetMapping("/unread-count")
    public Result<?> getUnreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return notificationService.getUnreadCount(userId);
    }
}
