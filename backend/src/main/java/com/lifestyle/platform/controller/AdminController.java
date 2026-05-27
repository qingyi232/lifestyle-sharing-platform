package com.lifestyle.platform.controller;

import com.lifestyle.platform.common.Result;
import com.lifestyle.platform.entity.Category;
import com.lifestyle.platform.service.AdminService;
import com.lifestyle.platform.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private CategoryService categoryService;

    private boolean isAdmin(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        return role != null && role == 1;
    }

    @GetMapping("/stats")
    public Result<?> getStats(HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.getStats();
    }

    @GetMapping("/users")
    public Result<?> getUserList(HttpServletRequest request,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String keyword) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.getUserList(page, size, keyword);
    }

    @PutMapping("/users/{id}/status")
    public Result<?> toggleUserStatus(HttpServletRequest request, @PathVariable Long id,
                                       @RequestBody Map<String, Integer> params) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.toggleUserStatus(id, params.get("status"));
    }

    @PutMapping("/users/{id}/reset-password")
    public Result<?> resetPassword(HttpServletRequest request, @PathVariable Long id) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.resetPassword(id);
    }

    @PutMapping("/users/{id}/role")
    public Result<?> toggleUserRole(HttpServletRequest request, @PathVariable Long id,
                                     @RequestBody Map<String, Integer> params) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.toggleUserRole(id, params.get("role"));
    }

    @GetMapping("/notes")
    public Result<?> getPendingNotes(HttpServletRequest request,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.getPendingNotes(page, size);
    }

    @GetMapping("/notes/all")
    public Result<?> getAllNotes(HttpServletRequest request,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) Integer status) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.getAllNotes(page, size, keyword, status);
    }

    @PutMapping("/notes/{id}/approve")
    public Result<?> approveNote(HttpServletRequest request, @PathVariable Long id) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.approveNote(id);
    }

    @PutMapping("/notes/{id}/reject")
    public Result<?> rejectNote(HttpServletRequest request, @PathVariable Long id,
                                @RequestBody Map<String, String> params) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return adminService.rejectNote(id, params.get("reason"));
    }

    @PostMapping("/categories")
    public Result<?> addCategory(HttpServletRequest request, @RequestBody Category category) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return categoryService.addCategory(category);
    }

    @PutMapping("/categories/{id}")
    public Result<?> updateCategory(HttpServletRequest request, @PathVariable Long id,
                                    @RequestBody Category category) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return categoryService.updateCategory(id, category);
    }

    @DeleteMapping("/categories/{id}")
    public Result<?> deleteCategory(HttpServletRequest request, @PathVariable Long id) {
        if (!isAdmin(request)) return Result.error(403, "无管理员权限");
        return categoryService.deleteCategory(id);
    }
}
