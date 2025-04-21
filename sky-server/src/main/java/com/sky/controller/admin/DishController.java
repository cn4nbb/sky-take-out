package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping()
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品，参数为{}",dishDTO);

        dishService.save(dishDTO);

        return Result.success();
    }

    /**
     * 菜品分页查询
     */
    @GetMapping("/page")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询，参数为：{}",dishPageQueryDTO);

        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 菜品批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result deleteBatch(@RequestParam List<Long> ids){
        log.info("菜品批量删除，参数为：{}",ids);

        dishService.deleteBatch(ids);

        return Result.success();
    }

    /**
     * 根据id查询菜品
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品，参数为：{}",id);

        DishVO dishVO = dishService.getById(id);

        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品，参数为：{}",dishDTO);

        dishService.update(dishDTO);

        return Result.success();
    }
}


