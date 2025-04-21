package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface FlavorMapper {

    /**
     * 插入多条数据
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品id查询口味
     * @param dishId
     * @return
     */
    List<DishFlavor> getByDishId(Long dishId);

    /**
     * 修改口味
     * @param flavor
     */
    @Update("update dish_flavor set name = #{name},value = #{value} where id = #{id} and dish_id = #{dishId}")
    void update(DishFlavor flavor);

    /**
     * 根据dishId删除口味
     * @param dishId
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long dishId);
}
