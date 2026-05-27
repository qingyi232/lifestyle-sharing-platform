package com.lifestyle.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lifestyle.platform.common.Result;
import com.lifestyle.platform.entity.Notification;
import com.lifestyle.platform.entity.User;
import com.lifestyle.platform.mapper.NotificationMapper;
import com.lifestyle.platform.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private UserMapper userMapper;

    public Result<?> getNotifications(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
                .orderByDesc(Notification::getCreatedAt);
        List<Notification> notifications = notificationMapper.selectList(wrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Notification n : notifications) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", n.getId());
            item.put("userId", n.getUserId());
            item.put("fromUserId", n.getFromUserId());
            item.put("type", n.getType());
            item.put("noteId", n.getNoteId());
            item.put("content", n.getContent());
            item.put("isRead", n.getIsRead());
            item.put("createdAt", n.getCreatedAt());

            if (n.getFromUserId() != null) {
                User fromUser = userMapper.selectById(n.getFromUserId());
                if (fromUser != null) {
                    item.put("fromUserName", fromUser.getNickname());
                    item.put("fromUserAvatar", fromUser.getAvatar());
                }
            }
            result.add(item);
        }

        return Result.success(result);
    }

    public Result<?> markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            return Result.error(404, "通知不存在");
        }
        if (!notification.getUserId().equals(userId)) {
            return Result.error(403, "无权操作");
        }
        notification.setIsRead(1);
        notificationMapper.updateById(notification);
        return Result.success("已标记为已读", null);
    }

    public Result<?> markAllAsRead(Long userId) {
        LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0)
                .set(Notification::getIsRead, 1);
        notificationMapper.update(null, wrapper);
        return Result.success("全部已读", null);
    }

    public Result<?> getUnreadCount(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0);
        long count = notificationMapper.selectCount(wrapper);

        Map<String, Long> result = new HashMap<>();
        result.put("count", count);
        return Result.success(result);
    }
}
