package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    //设置切入点 限定在mapper包下的带有AutoFill注解的接口或方法 提高扫描效率
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void pt(){}

    //定义通知
    @Before("pt()")
    public void autoFill(JoinPoint joinPoint)  {

        //获取被拦截方法上的注释值
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//获取方法签名对象
        Method method = signature.getMethod(); //获取方法对象
        AutoFill annotation = method.getAnnotation(AutoFill.class);//获取方法上的注解对象
        OperationType value = annotation.value();//获取注解里的值（UPDATE或INSERT）

        //获取被方法的参数对象 默认约定参数列表的第一个为实体
        Object[] args = joinPoint.getArgs();
        if (args==null || args.length==0){
            return;
        }
        Object entity = args[0];

        //准备更新的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //如果是INSERT类型操作 则更新两个字段
        if (value == OperationType.INSERT){
            try {
                //通过与反射获取参数对象的设置方法
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过invoke调用实例对象的方法并传入参数
                setCreateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else if (value == OperationType.UPDATE) {
            try {
                //通过与反射获取参数对象的设置方法
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //通过invoke调用实例对象的方法并传入参数
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
}
