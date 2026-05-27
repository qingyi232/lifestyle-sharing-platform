package com.lifestyle.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lifestyle.platform.common.Result;
import com.lifestyle.platform.dto.NoteRequest;
import com.lifestyle.platform.dto.NoteVO;
import com.lifestyle.platform.entity.*;
import com.lifestyle.platform.mapper.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NoteService {

    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private NoteTagMapper noteTagMapper;
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private FollowMapper followMapper;

    public Result<?> getNoteList(int page, int size, String keyword, Long categoryId, String orderBy, Long currentUserId) {
        Page<Note> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Note::getStatus, 1);

        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Note::getTitle, keyword).or().like(Note::getContent, keyword));
        }
        if (categoryId != null) {
            wrapper.eq(Note::getCategoryId, categoryId);
        }

        if ("hot".equals(orderBy)) {
            wrapper.orderByDesc(Note::getLikeCount);
        } else if ("views".equals(orderBy)) {
            wrapper.orderByDesc(Note::getViewCount);
        } else {
            wrapper.orderByDesc(Note::getCreatedAt);
        }

        IPage<Note> notePages = noteMapper.selectPage(pageParam, wrapper);
        IPage<NoteVO> voPage = notePages.convert(note -> convertToVO(note, currentUserId));

        return Result.success(voPage);
    }

    public Result<?> getNoteDetail(Long id, Long currentUserId) {
        Note note = noteMapper.selectById(id);
        if (note == null) {
            return Result.error(404, "笔记不存在");
        }

        note.setViewCount(note.getViewCount() + 1);
        noteMapper.updateById(note);

        NoteVO vo = convertToVO(note, currentUserId);
        return Result.success(vo);
    }

    @Transactional
    public Result<?> createNote(Long userId, NoteRequest request) {
        Note note = new Note();
        note.setUserId(userId);
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setCoverImage(request.getCoverImage());
        note.setImages(request.getImages());
        note.setCategoryId(request.getCategoryId());
        note.setStatus(0);
        note.setViewCount(0);
        note.setLikeCount(0);
        note.setCommentCount(0);
        note.setFavoriteCount(0);

        noteMapper.insert(note);

        if (request.getTags() != null && !request.getTags().isEmpty()) {
            saveTags(note.getId(), request.getTags());
        }

        User user = userMapper.selectById(userId);
        user.setNotesCount(user.getNotesCount() + 1);
        userMapper.updateById(user);

        return Result.success("发布成功，等待审核", note);
    }

    @Transactional
    public Result<?> updateNote(Long userId, Long noteId, NoteRequest request) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) {
            return Result.error(404, "笔记不存在");
        }
        if (!note.getUserId().equals(userId)) {
            return Result.error(403, "无权修改他人的笔记");
        }

        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setCoverImage(request.getCoverImage());
        note.setImages(request.getImages());
        note.setCategoryId(request.getCategoryId());
        note.setStatus(0);
        note.setRejectReason(null);

        noteMapper.updateById(note);

        LambdaQueryWrapper<NoteTag> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(NoteTag::getNoteId, noteId);
        noteTagMapper.delete(deleteWrapper);

        if (request.getTags() != null && !request.getTags().isEmpty()) {
            saveTags(noteId, request.getTags());
        }

        return Result.success("更新成功，等待审核", note);
    }

    @Transactional
    public Result<?> deleteNote(Long userId, Long noteId) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) {
            return Result.error(404, "笔记不存在");
        }
        if (!note.getUserId().equals(userId)) {
            return Result.error(403, "无权删除他人的笔记");
        }

        noteMapper.deleteById(noteId);

        LambdaQueryWrapper<NoteTag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.eq(NoteTag::getNoteId, noteId);
        noteTagMapper.delete(tagWrapper);

        User user = userMapper.selectById(userId);
        if (user.getNotesCount() > 0) {
            user.setNotesCount(user.getNotesCount() - 1);
            userMapper.updateById(user);
        }

        return Result.success("删除成功", null);
    }

    public Result<?> getMyNotes(Long userId, int page, int size) {
        Page<Note> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Note::getUserId, userId).orderByDesc(Note::getCreatedAt);
        IPage<Note> notePages = noteMapper.selectPage(pageParam, wrapper);
        IPage<NoteVO> voPage = notePages.convert(note -> convertToVO(note, userId));
        return Result.success(voPage);
    }

    public Result<?> getMyFavorites(Long userId, int page, int size) {
        Page<Favorite> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).orderByDesc(Favorite::getCreatedAt);
        IPage<Favorite> favPage = favoriteMapper.selectPage(pageParam, wrapper);

        List<NoteVO> voList = new ArrayList<>();
        for (Favorite fav : favPage.getRecords()) {
            Note note = noteMapper.selectById(fav.getNoteId());
            if (note != null && note.getStatus() == 1) {
                voList.add(convertToVO(note, userId));
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("records", voList);
        result.put("total", favPage.getTotal());
        result.put("current", favPage.getCurrent());
        result.put("size", favPage.getSize());
        result.put("pages", favPage.getPages());

        return Result.success(result);
    }

    public Result<?> getFollowingNotes(Long userId, int page, int size) {
        LambdaQueryWrapper<Follow> followWrapper = new LambdaQueryWrapper<>();
        followWrapper.eq(Follow::getFollowerId, userId);
        List<Follow> follows = followMapper.selectList(followWrapper);

        if (follows.isEmpty()) {
            Page<NoteVO> emptyPage = new Page<>(page, size);
            emptyPage.setTotal(0);
            emptyPage.setRecords(new ArrayList<>());
            return Result.success(emptyPage);
        }

        List<Long> followingIds = follows.stream().map(Follow::getFollowingId).collect(Collectors.toList());

        Page<Note> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Note::getUserId, followingIds)
                .eq(Note::getStatus, 1)
                .orderByDesc(Note::getCreatedAt);

        IPage<Note> notePages = noteMapper.selectPage(pageParam, wrapper);
        IPage<NoteVO> voPage = notePages.convert(note -> convertToVO(note, userId));
        return Result.success(voPage);
    }

    public Result<?> getHotNotes(Long currentUserId) {
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Note::getStatus, 1)
                .orderByDesc(Note::getLikeCount)
                .last("LIMIT 20");

        List<Note> notes = noteMapper.selectList(wrapper);
        List<NoteVO> voList = notes.stream()
                .map(note -> convertToVO(note, currentUserId))
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    public Result<?> getUserNotes(Long userId, int page, int size) {
        Page<Note> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Note::getUserId, userId)
                .eq(Note::getStatus, 1)
                .orderByDesc(Note::getCreatedAt);
        IPage<Note> notePages = noteMapper.selectPage(pageParam, wrapper);
        IPage<NoteVO> voPage = notePages.convert(note -> convertToVO(note, null));
        return Result.success(voPage);
    }

    private void saveTags(Long noteId, List<String> tagNames) {
        for (String tagName : tagNames) {
            if (StringUtils.isBlank(tagName)) continue;
            tagName = tagName.trim();

            LambdaQueryWrapper<Tag> tagWrapper = new LambdaQueryWrapper<>();
            tagWrapper.eq(Tag::getName, tagName);
            Tag tag = tagMapper.selectOne(tagWrapper);

            if (tag == null) {
                tag = new Tag();
                tag.setName(tagName);
                tagMapper.insert(tag);
            }

            NoteTag noteTag = new NoteTag();
            noteTag.setNoteId(noteId);
            noteTag.setTagId(tag.getId());
            noteTagMapper.insert(noteTag);
        }
    }

    public NoteVO convertToVO(Note note, Long currentUserId) {
        NoteVO vo = new NoteVO();
        BeanUtils.copyProperties(note, vo);

        User author = userMapper.selectById(note.getUserId());
        if (author != null) {
            vo.setAuthorName(author.getNickname());
            vo.setAuthorAvatar(author.getAvatar());
        }

        Category category = categoryMapper.selectById(note.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }

        LambdaQueryWrapper<NoteTag> ntWrapper = new LambdaQueryWrapper<>();
        ntWrapper.eq(NoteTag::getNoteId, note.getId());
        List<NoteTag> noteTags = noteTagMapper.selectList(ntWrapper);
        List<String> tagNames = new ArrayList<>();
        for (NoteTag nt : noteTags) {
            Tag tag = tagMapper.selectById(nt.getTagId());
            if (tag != null) {
                tagNames.add(tag.getName());
            }
        }
        vo.setTags(tagNames);

        if (currentUserId != null) {
            LambdaQueryWrapper<UserLike> likeWrapper = new LambdaQueryWrapper<>();
            likeWrapper.eq(UserLike::getUserId, currentUserId).eq(UserLike::getNoteId, note.getId());
            vo.setLiked(likeMapper.selectCount(likeWrapper) > 0);

            LambdaQueryWrapper<Favorite> favWrapper = new LambdaQueryWrapper<>();
            favWrapper.eq(Favorite::getUserId, currentUserId).eq(Favorite::getNoteId, note.getId());
            vo.setFavorited(favoriteMapper.selectCount(favWrapper) > 0);
        }

        return vo;
    }
}
