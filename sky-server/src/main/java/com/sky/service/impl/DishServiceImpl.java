package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private FlavorMapper flavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Transactional
    @Override
    public void save(DishDTO dishDTO) {
        Dish dish = new Dish();
        List<DishFlavor> flavors = dishDTO.getFlavors();

        //属性拷贝
        BeanUtils.copyProperties(dishDTO,dish);

        //向dish表插入一条数据
        dishMapper.insert(dish);

        //向flavor表插入多条数据
        if (flavors!=null && flavors.size()>0){

            //设置flavor列表中每个对象的菜品id
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dish.getId());
            });

            flavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {

        //开始分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());

        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 菜品批量删除
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //遍历id 查询有无起售中的菜品
        ids.forEach(id ->{
            DishVO dishVO = dishMapper.getById(id);
            //如果该菜品起售中 则抛出异常 不允许删除
            if (dishVO.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });

        //查询有无管理套餐的菜品
        if(setmealDishMapper.countByDishId(ids)>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        };

        //从菜品表和口味表删除对应数据
        ids.forEach(id ->{
            dishMapper.deletById(id);
            dishFlavorMapper.deleteByDishId(id);
        });
    }
}