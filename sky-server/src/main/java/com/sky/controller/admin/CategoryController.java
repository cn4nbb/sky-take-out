package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 分类管理
 */
@Slf4j
@RestController
@RequestMapping("/admin/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分类分页查询，参数：{}",categoryPageQueryDTO);

        PageResult result = categoryService.pageQuery(categoryPageQueryDTO);

        return Result.success(result);
    }

    /**
     * 修改分类
     * @param categoryDTO
     * @return
     */
    @PutMapping()
    public Result update(@RequestBody CategoryDTO categoryDTO){
        log.info("修改分类，参数：{}",categoryDTO);

        categoryService.update(categoryDTO);

        return Result.success();
    }
}
