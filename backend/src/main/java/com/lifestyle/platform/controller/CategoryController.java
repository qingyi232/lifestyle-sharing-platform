package com.lifestyle.platform.controller;

import com.lifestyle.platform.common.Result;
import com.lifestyle.platform.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public Result<?> getAllCategories() {
        return categoryService.getAllCategories();
    }
}
