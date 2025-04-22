package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐数量
     * @param categoryId
     * @return
     */
    @Select("select count(*) from setmeal where category_id = #{categoryId}")
    Integer countByType(Long categoryId);

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 新增套餐
     * @param setmeal
     */
    @AutoFill(OperationType.INSERT)
    void save(Setmeal setmeal);

    /**
     * 动态更新套餐
     * @param setmeal
     */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    SetmealVO getById(Long id);
}
