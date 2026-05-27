package com.lifestyle.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifestyle.platform.common.Result;
import com.lifestyle.platform.entity.Category;
import com.lifestyle.platform.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public Result<?> getAllCategories() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSortOrder);
        List<Category> categories = categoryMapper.selectList(wrapper);
        return Result.success(categories);
    }

    public Result<?> addCategory(Category category) {
        categoryMapper.insert(category);
        return Result.success("添加成功", category);
    }

    public Result<?> updateCategory(Long id, Category category) {
        Category existing = categoryMapper.selectById(id);
        if (existing == null) {
            return Result.error(404, "分类不存在");
        }
        existing.setName(category.getName());
        existing.setIcon(category.getIcon());
        existing.setSortOrder(category.getSortOrder());
        categoryMapper.updateById(existing);
        return Result.success("更新成功", existing);
    }

    public Result<?> deleteCategory(Long id) {
        Category existing = categoryMapper.selectById(id);
        if (existing == null) {
            return Result.error(404, "分类不存在");
        }
        categoryMapper.deleteById(id);
        return Result.success("删除成功", null);
    }
}
