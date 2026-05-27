package com.lifestyle.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifestyle.platform.common.Result;
import com.lifestyle.platform.entity.Follow;
import com.lifestyle.platform.entity.User;
import com.lifestyle.platform.mapper.FollowMapper;
import com.lifestyle.platform.mapper.UserMapper;
import com.lifestyle.platform.service.NoteService;
import com.lifestyle.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private NoteService noteService;
    @Autowired
    private FollowMapper followMapper;
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/profile")
    public Result<?> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return userService.getProfile(userId);
    }

    @PutMapping("/profile")
    public Result<?> updateProfile(HttpServletRequest request, @RequestBody Map<String, String> params) {
        Long userId = (Long) request.getAttribute("userId");
        return userService.updateProfile(userId, params);
    }

    @PutMapping("/password")
    public Result<?> changePassword(HttpServletRequest request, @RequestBody Map<String, String> params) {
        Long userId = (Long) request.getAttribute("userId");
        return userService.changePassword(userId, params);
    }

    @DeleteMapping("/account")
    public Result<?> deleteAccount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return userService.deleteAccount(userId);
    }

    @GetMapping("/{id}")
    public Result<?> getUserPublicProfile(@PathVariable Long id) {
        return userService.getUserPublicProfile(id);
    }

    @GetMapping("/{id}/notes")
    public Result<?> getUserNotes(@PathVariable Long id,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        return noteService.getUserNotes(id, page, size);
    }

    @GetMapping("/{id}/followers")
    public Result<?> getFollowers(@PathVariable Long id) {
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getFollowingId, id);
        List<Follow> follows = followMapper.selectList(wrapper);

        List<User> followers = follows.stream()
                .map(f -> {
                    User user = userMapper.selectById(f.getFollowerId());
                    if (user != null) user.setPassword(null);
                    return user;
                })
                .filter(u -> u != null)
                .collect(Collectors.toList());

        return Result.success(followers);
    }

    @GetMapping("/{id}/following")
    public Result<?> getFollowing(@PathVariable Long id) {
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getFollowerId, id);
        List<Follow> follows = followMapper.selectList(wrapper);

        List<User> following = follows.stream()
                .map(f -> {
                    User user = userMapper.selectById(f.getFollowingId());
                    if (user != null) user.setPassword(null);
                    return user;
                })
                .filter(u -> u != null)
                .collect(Collectors.toList());

        return Result.success(following);
    }
}
