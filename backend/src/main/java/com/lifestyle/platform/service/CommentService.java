package com.lifestyle.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifestyle.platform.common.Result;
import com.lifestyle.platform.dto.CommentVO;
import com.lifestyle.platform.entity.Comment;
import com.lifestyle.platform.entity.Note;
import com.lifestyle.platform.entity.Notification;
import com.lifestyle.platform.entity.User;
import com.lifestyle.platform.mapper.CommentMapper;
import com.lifestyle.platform.mapper.NoteMapper;
import com.lifestyle.platform.mapper.NotificationMapper;
import com.lifestyle.platform.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private NotificationMapper notificationMapper;

    public Result<?> getCommentsByNoteId(Long noteId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getNoteId, noteId)
                .orderByAsc(Comment::getCreatedAt);

        List<Comment> allComments = commentMapper.selectList(wrapper);
        List<CommentVO> voList = allComments.stream()
                .map(this::convertToFlatVO)
                .collect(Collectors.toList());

        return Result.success(voList);
    }

    @Transactional
    public Result<?> addComment(Long userId, Long noteId, String content, Long parentId) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) {
            return Result.error(404, "笔记不存在");
        }

        Comment comment = new Comment();
        comment.setNoteId(noteId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setParentId(parentId);
        commentMapper.insert(comment);

        note.setCommentCount(note.getCommentCount() + 1);
        noteMapper.updateById(note);

        if (!note.getUserId().equals(userId)) {
            User commenter = userMapper.selectById(userId);
            Notification notification = new Notification();
            notification.setUserId(note.getUserId());
            notification.setFromUserId(userId);
            notification.setType("comment");
            notification.setNoteId(noteId);
            notification.setContent(commenter.getNickname() + " 评论了你的笔记");
            notification.setIsRead(0);
            notificationMapper.insert(notification);
        }

        CommentVO vo = convertToFlatVO(comment);
        return Result.success("评论成功", vo);
    }

    @Transactional
    public Result<?> deleteComment(Long userId, Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            return Result.error(404, "评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            return Result.error(403, "无权删除他人的评论");
        }

        LambdaQueryWrapper<Comment> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(Comment::getParentId, commentId);
        long childCount = commentMapper.selectCount(childWrapper);

        commentMapper.deleteById(commentId);
        commentMapper.delete(childWrapper);

        Note note = noteMapper.selectById(comment.getNoteId());
        if (note != null && note.getCommentCount() > 0) {
            note.setCommentCount((int) Math.max(0, note.getCommentCount() - 1 - childCount));
            noteMapper.updateById(note);
        }

        return Result.success("删除成功", null);
    }

    private CommentVO convertToFlatVO(Comment comment) {
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(comment, vo);

        User user = userMapper.selectById(comment.getUserId());
        if (user != null) {
            vo.setUserNickname(user.getNickname());
            vo.setUserAvatar(user.getAvatar());
        }

        vo.setChildren(new ArrayList<>());
        return vo;
    }

    private CommentVO convertToVO(Comment comment) {
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(comment, vo);

        User user = userMapper.selectById(comment.getUserId());
        if (user != null) {
            vo.setUserNickname(user.getNickname());
            vo.setUserAvatar(user.getAvatar());
        }

        LambdaQueryWrapper<Comment> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(Comment::getParentId, comment.getId())
                .orderByAsc(Comment::getCreatedAt);
        List<Comment> children = commentMapper.selectList(childWrapper);

        if (!children.isEmpty()) {
            vo.setChildren(children.stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList()));
        } else {
            vo.setChildren(new ArrayList<>());
        }

        return vo;
    }
}
