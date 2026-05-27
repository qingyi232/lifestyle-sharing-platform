package com.lifestyle.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lifestyle.platform.common.Result;
import com.lifestyle.platform.dto.LoginRequest;
import com.lifestyle.platform.dto.RegisterRequest;
import com.lifestyle.platform.entity.*;
import com.lifestyle.platform.mapper.*;
import com.lifestyle.platform.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Result<?> register(RegisterRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            return Result.error(400, "用户名已存在");
        }

        if (StringUtils.isNotBlank(request.getEmail())) {
            LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
            emailWrapper.eq(User::getEmail, request.getEmail());
            if (userMapper.selectCount(emailWrapper) > 0) {
                return Result.error(400, "邮箱已被注册");
            }
        }

        if (StringUtils.isNotBlank(request.getPhone())) {
            LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(User::getPhone, request.getPhone());
            if (userMapper.selectCount(phoneWrapper) > 0) {
                return Result.error(400, "手机号已被注册");
            }
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(0);
        user.setStatus(1);
        user.setFollowersCount(0);
        user.setFollowingCount(0);
        user.setNotesCount(0);

        userMapper.insert(user);
        return Result.success("注册成功", null);
    }

    public Result<?> login(LoginRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            return Result.error(400, "用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            return Result.error(403, "账号已被禁用");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return Result.error(400, "用户名或密码错误");
        }

        String token = JwtUtil.generateToken(user.getId(), user.getRole());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);

        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUsername(user.getUsername());
        safeUser.setNickname(user.getNickname());
        safeUser.setAvatar(user.getAvatar());
        safeUser.setBio(user.getBio());
        safeUser.setEmail(user.getEmail());
        safeUser.setPhone(user.getPhone());
        safeUser.setRole(user.getRole());
        safeUser.setFollowersCount(user.getFollowersCount());
        safeUser.setFollowingCount(user.getFollowingCount());
        safeUser.setNotesCount(user.getNotesCount());
        safeUser.setCreatedAt(user.getCreatedAt());
        data.put("user", safeUser);

        return Result.success("登录成功", data);
    }

    public Result<?> getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    public Result<?> updateProfile(Long userId, Map<String, String> params) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        if (params.containsKey("nickname") && StringUtils.isNotBlank(params.get("nickname"))) {
            user.setNickname(params.get("nickname"));
        }
        if (params.containsKey("bio")) {
            user.setBio(params.get("bio"));
        }
        if (params.containsKey("avatar")) {
            user.setAvatar(params.get("avatar"));
        }
        if (params.containsKey("email")) {
            user.setEmail(params.get("email"));
        }
        if (params.containsKey("phone")) {
            user.setPhone(params.get("phone"));
        }

        userMapper.updateById(user);
        user.setPassword(null);
        return Result.success("更新成功", user);
    }

    public Result<?> changePassword(Long userId, Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

        if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) {
            return Result.error(400, "密码不能为空");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error(400, "原密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
        return Result.success("密码修改成功", null);
    }

    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private FollowMapper followMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private NoteTagMapper noteTagMapper;

    public Result<?> deleteAccount(Long userId) {
        // 删除用户的笔记标签关联
        LambdaQueryWrapper<Note> noteWrapper = new LambdaQueryWrapper<>();
        noteWrapper.eq(Note::getUserId, userId);
        List<Note> userNotes = noteMapper.selectList(noteWrapper);
        for (Note note : userNotes) {
            LambdaQueryWrapper<NoteTag> ntWrapper = new LambdaQueryWrapper<>();
            ntWrapper.eq(NoteTag::getNoteId, note.getId());
            noteTagMapper.delete(ntWrapper);
        }
        // 删除用户的笔记
        noteMapper.delete(noteWrapper);
        // 删除用户的评论
        LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.eq(Comment::getUserId, userId);
        commentMapper.delete(commentWrapper);
        // 删除用户的点赞
        LambdaQueryWrapper<UserLike> likeWrapper = new LambdaQueryWrapper<>();
        likeWrapper.eq(UserLike::getUserId, userId);
        likeMapper.delete(likeWrapper);
        // 删除用户的收藏
        LambdaQueryWrapper<Favorite> favWrapper = new LambdaQueryWrapper<>();
        favWrapper.eq(Favorite::getUserId, userId);
        favoriteMapper.delete(favWrapper);
        // 删除用户的关注关系
        LambdaQueryWrapper<Follow> followWrapper = new LambdaQueryWrapper<>();
        followWrapper.eq(Follow::getFollowerId, userId).or().eq(Follow::getFollowingId, userId);
        followMapper.delete(followWrapper);
        // 删除用户的通知
        LambdaQueryWrapper<Notification> notiWrapper = new LambdaQueryWrapper<>();
        notiWrapper.eq(Notification::getUserId, userId).or().eq(Notification::getFromUserId, userId);
        notificationMapper.delete(notiWrapper);
        // 最后删除用户
        userMapper.deleteById(userId);
        return Result.success("账号已注销", null);
    }

    public Result<?> getUserPublicProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        user.setPassword(null);
        user.setEmail(null);
        user.setPhone(null);
        return Result.success(user);
    }

    public IPage<User> getUserList(int page, int size, String keyword) {
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
        return result;
    }
}
