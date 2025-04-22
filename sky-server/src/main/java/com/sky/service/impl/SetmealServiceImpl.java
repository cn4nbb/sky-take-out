package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;
    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());

        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        //属性拷贝
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //获取套餐菜品关系列表
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();

        //向setmeal表插入数据
        setmealMapper.save(setmeal);
        //循环list 设置套餐id
        setmealDishList.forEach(sd->{
            sd.setSetmealId(setmeal.getId());
        });
        //向setmeal_dish表插入多条数据
        setmealDishMapper.insertBatch(setmealDishList);
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        //属性拷贝
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //获取套餐菜品关系列表
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();

        //更新setmeal表
        setmealMapper.update(setmeal);

        //删除原有setmeal_dish对应数据
        setmealDishMapper.deleteBySetmealId(setmealDTO.getId());
        setmealDishList.forEach(sd->{
            sd.setSetmealId(setmealDTO.getId());
        });
        //向setmeal_dish表插入数据
        setmealDishMapper.insertBatch(setmealDishList);
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    @Transactional
    public SetmealVO getById(Long id) {
        //获取setmeal及category对应数据
        SetmealVO setmealVO = setmealMapper.getById(id);
        //获取setmeal和dish的关系
        List<SetmealDish>  setmealDishList = setmealDishMapper.getListBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishList);
        return setmealVO;
    }

    /**
     * 套餐起售停售
     * @param status
     * @param id
     */
    @Override
    public void enableOrDisable(Integer status, Long id) {
        //如果要执行起售操作
        if (status == StatusConstant.ENABLE){
            //获取该套餐的套餐菜品关系列表
            List<SetmealDish> setmealDishes = setmealDishMapper.getListBySetmealId(id);
            //遍历列表 通过菜品id查询每个菜品的状态
            setmealDishes.forEach(sd ->{
                DishVO dish = dishMapper.getById(sd.getDishId());
                //如果套餐内有未起售的菜品 则抛出异常 不允许起售
                if (dish.getStatus() == StatusConstant.DISABLE){
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            });
        }

        setmealMapper.update(Setmeal.builder().id(id).status(status).build());
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //查询有无起售的套餐 有则抛出异常 不能删除
        ids.forEach(id ->{
            SetmealVO setmealVO = setmealMapper.getById(id);
            if (setmealVO.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });

        //删除setmeal表中数据
        setmealMapper.deleteBatch(ids);
        //删除setmeal_dish中对应的套餐菜品关系
        ids.forEach(id ->{
            setmealDishMapper.deleteBySetmealId(id);
        });
    }

}
