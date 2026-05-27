package com.lifestyle.platform.controller;

import com.lifestyle.platform.common.Result;
import com.lifestyle.platform.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/note/{noteId}")
    public Result<?> getCommentsByNoteId(@PathVariable Long noteId) {
        return commentService.getCommentsByNoteId(noteId);
    }

    @PostMapping
    public Result<?> addComment(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        Long userId = (Long) request.getAttribute("userId");
        Long noteId = Long.valueOf(params.get("noteId").toString());
        String content = params.get("content").toString();
        Long parentId = params.containsKey("parentId") && params.get("parentId") != null
                ? Long.valueOf(params.get("parentId").toString()) : null;
        return commentService.addComment(userId, noteId, content, parentId);
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteComment(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        return commentService.deleteComment(userId, id);
    }
}
