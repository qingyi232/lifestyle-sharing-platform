package com.lifestyle.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifestyle.platform.common.Result;
import com.lifestyle.platform.entity.*;
import com.lifestyle.platform.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class InteractionService {

    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private FollowMapper followMapper;
    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationMapper notificationMapper;

    @Transactional
    public Result<?> toggleLike(Long userId, Long noteId) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) {
            return Result.error(404, "笔记不存在");
        }

        LambdaQueryWrapper<UserLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLike::getUserId, userId).eq(UserLike::getNoteId, noteId);
        UserLike existing = likeMapper.selectOne(wrapper);

        Map<String, Object> result = new HashMap<>();
        if (existing != null) {
            likeMapper.deleteById(existing.getId());
            note.setLikeCount(Math.max(0, note.getLikeCount() - 1));
            noteMapper.updateById(note);
            result.put("liked", false);
            return Result.success("取消点赞", result);
        } else {
            UserLike like = new UserLike();
            like.setUserId(userId);
            like.setNoteId(noteId);
            likeMapper.insert(like);
            note.setLikeCount(note.getLikeCount() + 1);
            noteMapper.updateById(note);

            if (!note.getUserId().equals(userId)) {
                User liker = userMapper.selectById(userId);
                Notification notification = new Notification();
                notification.setUserId(note.getUserId());
                notification.setFromUserId(userId);
                notification.setType("like");
                notification.setNoteId(noteId);
                notification.setContent(liker.getNickname() + " 赞了你的笔记");
                notification.setIsRead(0);
                notificationMapper.insert(notification);
            }

            result.put("liked", true);
            return Result.success("点赞成功", result);
        }
    }

    @Transactional
    public Result<?> toggleFavorite(Long userId, Long noteId) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) {
            return Result.error(404, "笔记不存在");
        }

        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getNoteId, noteId);
        Favorite existing = favoriteMapper.selectOne(wrapper);

        Map<String, Object> result = new HashMap<>();
        if (existing != null) {
            favoriteMapper.deleteById(existing.getId());
            note.setFavoriteCount(Math.max(0, note.getFavoriteCount() - 1));
            noteMapper.updateById(note);
            result.put("favorited", false);
            return Result.success("取消收藏", result);
        } else {
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setNoteId(noteId);
            favoriteMapper.insert(favorite);
            note.setFavoriteCount(note.getFavoriteCount() + 1);
            noteMapper.updateById(note);
            result.put("favorited", true);
            return Result.success("收藏成功", result);
        }
    }

    @Transactional
    public Result<?> toggleFollow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            return Result.error(400, "不能关注自己");
        }

        User targetUser = userMapper.selectById(followingId);
        if (targetUser == null) {
            return Result.error(404, "用户不存在");
        }

        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getFollowerId, followerId).eq(Follow::getFollowingId, followingId);
        Follow existing = followMapper.selectOne(wrapper);

        User follower = userMapper.selectById(followerId);
        Map<String, Object> result = new HashMap<>();

        if (existing != null) {
            followMapper.deleteById(existing.getId());

            follower.setFollowingCount(Math.max(0, follower.getFollowingCount() - 1));
            userMapper.updateById(follower);

            targetUser.setFollowersCount(Math.max(0, targetUser.getFollowersCount() - 1));
            userMapper.updateById(targetUser);

            result.put("followed", false);
            return Result.success("取消关注", result);
        } else {
            Follow follow = new Follow();
            follow.setFollowerId(followerId);
            follow.setFollowingId(followingId);
            followMapper.insert(follow);

            follower.setFollowingCount(follower.getFollowingCount() + 1);
            userMapper.updateById(follower);

            targetUser.setFollowersCount(targetUser.getFollowersCount() + 1);
            userMapper.updateById(targetUser);

            Notification notification = new Notification();
            notification.setUserId(followingId);
            notification.setFromUserId(followerId);
            notification.setType("follow");
            notification.setContent(follower.getNickname() + " 关注了你");
            notification.setIsRead(0);
            notificationMapper.insert(notification);

            result.put("followed", true);
            return Result.success("关注成功", result);
        }
    }

    public Result<?> getInteractionStatus(Long userId, Long noteId) {
        Map<String, Boolean> status = new HashMap<>();

        LambdaQueryWrapper<UserLike> likeWrapper = new LambdaQueryWrapper<>();
        likeWrapper.eq(UserLike::getUserId, userId).eq(UserLike::getNoteId, noteId);
        status.put("liked", likeMapper.selectCount(likeWrapper) > 0);

        LambdaQueryWrapper<Favorite> favWrapper = new LambdaQueryWrapper<>();
        favWrapper.eq(Favorite::getUserId, userId).eq(Favorite::getNoteId, noteId);
        status.put("favorited", favoriteMapper.selectCount(favWrapper) > 0);

        return Result.success(status);
    }

    public Result<?> getFollowStatus(Long userId, Long targetId) {
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getFollowerId, userId).eq(Follow::getFollowingId, targetId);
        boolean followed = followMapper.selectCount(wrapper) > 0;

        Map<String, Boolean> status = new HashMap<>();
        status.put("followed", followed);
        return Result.success(status);
    }
}
