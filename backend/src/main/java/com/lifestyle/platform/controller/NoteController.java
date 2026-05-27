package com.lifestyle.platform.controller;

import com.lifestyle.platform.common.Result;
import com.lifestyle.platform.dto.NoteRequest;
import com.lifestyle.platform.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @GetMapping
    public Result<?> getNoteList(@RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) Long categoryId,
                                 @RequestParam(required = false) String orderBy,
                                 HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        return noteService.getNoteList(page, size, keyword, categoryId, orderBy, currentUserId);
    }

    @GetMapping("/{id}")
    public Result<?> getNoteDetail(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        return noteService.getNoteDetail(id, currentUserId);
    }

    @PostMapping
    public Result<?> createNote(HttpServletRequest request, @Valid @RequestBody NoteRequest noteRequest) {
        Long userId = (Long) request.getAttribute("userId");
        return noteService.createNote(userId, noteRequest);
    }

    @PutMapping("/{id}")
    public Result<?> updateNote(HttpServletRequest request, @PathVariable Long id,
                                @Valid @RequestBody NoteRequest noteRequest) {
        Long userId = (Long) request.getAttribute("userId");
        return noteService.updateNote(userId, id, noteRequest);
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteNote(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        return noteService.deleteNote(userId, id);
    }

    @GetMapping("/my")
    public Result<?> getMyNotes(HttpServletRequest request,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        return noteService.getMyNotes(userId, page, size);
    }

    @GetMapping("/favorites")
    public Result<?> getMyFavorites(HttpServletRequest request,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        return noteService.getMyFavorites(userId, page, size);
    }

    @GetMapping("/following")
    public Result<?> getFollowingNotes(HttpServletRequest request,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        return noteService.getFollowingNotes(userId, page, size);
    }

    @GetMapping("/hot")
    public Result<?> getHotNotes(HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        return noteService.getHotNotes(currentUserId);
    }
}
