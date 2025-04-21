package com.sky.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishFlavorMapper {

    /**
     * 根据菜品id删除数据
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    public void deleteByDishId(Long dishId);
}
