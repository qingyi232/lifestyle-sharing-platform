package com.lifestyle.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lifestyle.platform.common.Result;
import com.lifestyle.platform.dto.NoteVO;
import com.lifestyle.platform.entity.*;
import com.lifestyle.platform.mapper.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class AdminService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private NoteService noteService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Result<?> getStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", userMapper.selectCount(null));
        stats.put("totalNotes", noteMapper.selectCount(null));
        stats.put("totalComments", commentMapper.selectCount(null));

        LambdaQueryWrapper<Note> publishedWrapper = new LambdaQueryWrapper<>();
        publishedWrapper.eq(Note::getStatus, 1);
        stats.put("publishedNotes", noteMapper.selectCount(publishedWrapper));

        LambdaQueryWrapper<Note> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(Note::getStatus, 0);
        stats.put("pendingNotes", noteMapper.selectCount(pendingWrapper));

        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<User> todayUserWrapper = new LambdaQueryWrapper<>();
        todayUserWrapper.ge(User::getCreatedAt, today.atStartOfDay());
        long todayNew = userMapper.selectCount(todayUserWrapper);
        stats.put("todayNewUsers", todayNew);

        Set<Long> activeUserIds = new HashSet<>();
        LambdaQueryWrapper<Note> todayNoteWrapper = new LambdaQueryWrapper<>();
        todayNoteWrapper.ge(Note::getCreatedAt, today.atStartOfDay());
        noteMapper.selectList(todayNoteWrapper).forEach(n -> activeUserIds.add(n.getUserId()));
        LambdaQueryWrapper<Comment> todayCommentWrapper = new LambdaQueryWrapper<>();
        todayCommentWrapper.ge(Comment::getCreatedAt, today.atStartOfDay());
        commentMapper.selectList(todayCommentWrapper).forEach(c -> activeUserIds.add(c.getUserId()));
        LambdaQueryWrapper<UserLike> todayLikeWrapper = new LambdaQueryWrapper<>();
        todayLikeWrapper.ge(UserLike::getCreatedAt, today.atStartOfDay());
        likeMapper.selectList(todayLikeWrapper).forEach(l -> activeUserIds.add(l.getUserId()));
        stats.put("dailyActiveUsers", activeUserIds.size());

        List<Map<String, Object>> userTrend = new ArrayList<>();
        List<Map<String, Object>> noteTrend = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);

            LambdaQueryWrapper<User> uw = new LambdaQueryWrapper<>();
            uw.between(User::getCreatedAt, start, end);
            long userCount = userMapper.selectCount(uw);

            Map<String, Object> userItem = new HashMap<>();
            userItem.put("date", date.toString());
            userItem.put("count", userCount);
            userTrend.add(userItem);

            LambdaQueryWrapper<Note> nw = new LambdaQueryWrapper<>();
            nw.between(Note::getCreatedAt, start, end);
            long noteCount = noteMapper.selectCount(nw);

            Map<String, Object> noteItem = new HashMap<>();
            noteItem.put("date", date.toString());
            noteItem.put("count", noteCount);
            noteTrend.add(noteItem);
        }

        stats.put("userTrend", userTrend);
        stats.put("noteTrend", noteTrend);

        return Result.success(stats);
    }

    public Result<?> getUserList(int page, int size, String keyword) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getEmail, keyword);
        }
        wrapper.orderByDesc(User::getCreatedAt);
        IPage<User> result = userMapper.selectPage(pageParam, wrapper);
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(result);
    }

    public Result<?> toggleUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        if (user.getRole() == 1) {
            return Result.error(400, "不能修改管理员状态");
        }
        user.setStatus(status);
        userMapper.updateById(user);
        return Result.success(status == 1 ? "启用成功" : "禁用成功", null);
    }

    public Result<?> toggleUserRole(Long userId, Integer role) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        if ("admin".equals(user.getUsername())) {
            return Result.error(400, "不能修改超级管理员角色");
        }
        user.setRole(role);
        userMapper.updateById(user);
        return Result.success(role == 1 ? "已设为管理员" : "已取消管理员", null);
    }

    public Result<?> resetPassword(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        user.setPassword(passwordEncoder.encode("123456"));
        userMapper.updateById(user);
        return Result.success("密码已重置为123456", null);
    }

    public Result<?> getPendingNotes(int page, int size) {
        Page<Note> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Note::getStatus, 0).orderByDesc(Note::getCreatedAt);
        IPage<Note> notePages = noteMapper.selectPage(pageParam, wrapper);
        IPage<NoteVO> voPage = notePages.convert(note -> noteService.convertToVO(note, null));
        return Result.success(voPage);
    }

    public Result<?> getAllNotes(int page, int size, String keyword, Integer status) {
        Page<Note> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Note::getStatus, status);
        }
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Note::getTitle, keyword).or().like(Note::getContent, keyword));
        }
        wrapper.orderByDesc(Note::getCreatedAt);
        IPage<Note> notePages = noteMapper.selectPage(pageParam, wrapper);
        IPage<NoteVO> voPage = notePages.convert(note -> noteService.convertToVO(note, null));
        return Result.success(voPage);
    }

    public Result<?> approveNote(Long noteId) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) {
            return Result.error(404, "笔记不存在");
        }
        note.setStatus(1);
        note.setRejectReason(null);
        noteMapper.updateById(note);
        return Result.success("审核通过", null);
    }

    public Result<?> rejectNote(Long noteId, String reason) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) {
            return Result.error(404, "笔记不存在");
        }
        note.setStatus(2);
        note.setRejectReason(reason);
        noteMapper.updateById(note);
        return Result.success("已驳回", null);
    }
}
