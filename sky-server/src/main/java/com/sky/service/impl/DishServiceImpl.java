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

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Override
    public DishVO getById(Long id) {
        //根据id查询菜品数据
        DishVO dishVO = dishMapper.getById(id);
        //根据菜品id查询口味
        List<DishFlavor> flavors = flavorMapper.getByDishId(id);
        //将查询回来的口味封装给DishVo对象
        dishVO.setFlavors(flavors);

        return dishVO;
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    @Override
    @Transactional
    public void update(DishDTO dishDTO) {
        List<DishFlavor> flavors = dishDTO.getFlavors();
        Dish dish = new Dish();

        //属性拷贝
        BeanUtils.copyProperties(dishDTO,dish);
        //更新dish表中的数据
        dishMapper.update(dish);

        //删除原有口味表中的所有数据
        flavorMapper.deleteByDishId(dishDTO.getId());

        if (!flavors.isEmpty()){
            //设置dishId
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            //批量插入
            flavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> getByCategoryId(Long categoryId) {
        List<Dish> dishList = dishMapper.getListByCategoryId(categoryId);
        return dishList;
    }

    /**
     * 菜品起售停售
     * @param status
     * @param id
     */
    @Override
    public void enableOrDisable(Integer status, Long id) {
        Dish dish = Dish.builder().id(id).status(status).build();
        dishMapper.update(dish);
    }
}