package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


import java.util.List;

@Mapper
public interface CategoryMapper {

    /**
     * 动态查询
     * @param categoryPageQueryDTO
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 动态修改分类
     * @param category
     */
    @AutoFill(OperationType.UPDATE)
    void update(Category category);

    /**
     * 新增分类
     * @param category
     */
    @AutoFill(OperationType.INSERT)
    void insert(Category category);

    @Select("select * from category where type = #{type}")
    List<Category> typeQuery(Integer type);

    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);
}
