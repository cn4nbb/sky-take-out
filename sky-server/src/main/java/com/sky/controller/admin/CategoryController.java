package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 启用禁用分类
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result enableOrDisable(@PathVariable Integer status,Long id){
        log.info("启用禁用分类，参数：{}",status,id);

        categoryService.enableOrDisable(status,id);

        return Result.success();
    }

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    @PostMapping()
    public Result save(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类，参数为：{}",categoryDTO);

        categoryService.save(categoryDTO);

        return Result.success();
    }

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> getByType(Integer type){
        log.info("根据类型查询分类，参数为：{}",type);

        List<Category> categoryList = categoryService.getByType(type);

        return Result.success(categoryList);
    }

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    @DeleteMapping()
    public Result deleteById(Long id){
        log.info("根据id删除分类：参数：{}",id);

        categoryService.deleteById(id);

        return Result.success();
    }
}
