package com.lifestyle.platform.controller;

import com.lifestyle.platform.common.Result;
import com.lifestyle.platform.service.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/interactions")
public class InteractionController {

    @Autowired
    private InteractionService interactionService;

    @PostMapping("/like/{noteId}")
    public Result<?> toggleLike(HttpServletRequest request, @PathVariable Long noteId) {
        Long userId = (Long) request.getAttribute("userId");
        return interactionService.toggleLike(userId, noteId);
    }

    @PostMapping("/favorite/{noteId}")
    public Result<?> toggleFavorite(HttpServletRequest request, @PathVariable Long noteId) {
        Long userId = (Long) request.getAttribute("userId");
        return interactionService.toggleFavorite(userId, noteId);
    }

    @PostMapping("/follow/{userId}")
    public Result<?> toggleFollow(HttpServletRequest request, @PathVariable Long userId) {
        Long currentUserId = (Long) request.getAttribute("userId");
        return interactionService.toggleFollow(currentUserId, userId);
    }

    @GetMapping("/status/{noteId}")
    public Result<?> getInteractionStatus(HttpServletRequest request, @PathVariable Long noteId) {
        Long userId = (Long) request.getAttribute("userId");
        return interactionService.getInteractionStatus(userId, noteId);
    }

    @GetMapping("/follow-status/{userId}")
    public Result<?> getFollowStatus(HttpServletRequest request, @PathVariable Long userId) {
        Long currentUserId = (Long) request.getAttribute("userId");
        return interactionService.getFollowStatus(currentUserId, userId);
    }
}
