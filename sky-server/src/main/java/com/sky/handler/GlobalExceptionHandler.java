package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获sql异常
     * @return
     */
    @ExceptionHandler
    public Result sqlExceptionHandler(SQLIntegrityConstraintViolationException ex){
        //获取异常信息
        String message = ex.getMessage();

        //判断异常是否为唯一性冲突
        if (message.contains("Duplicate entry")){
            //分割信息
            String[] split = message.split(" ");
            //拿到用户名
            String username = split[2];
            //拼接返回信息
            String msg = String.join(username, MessageConstant.ACCOUNT_ALREADY_EXIST);
            //返回错误信息
            return Result.error(msg);
        }else {
            //否则 返回未知错误
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }
}
