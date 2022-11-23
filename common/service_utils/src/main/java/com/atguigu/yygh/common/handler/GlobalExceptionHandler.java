package com.atguigu.yygh.common.handler;

import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.sql.SQLException;

//@ControllerAdvice //表示这是一个全局异常处理类,可以处理全局controller的异常
@Slf4j
@RestControllerAdvice //@ControllerAdvice + @ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class) //统一异常处理:相当于兜底的
    public R handlerException(Exception e){
        //实际会记录到日志文件
        e.printStackTrace();
        return R.error().message("统一异常");
    }

    @ExceptionHandler(RuntimeException.class) //统一异常处理:相当于兜底的
    public R handlerRuntimeException(RuntimeException e){
        //实际会记录到日志文件
        e.printStackTrace();
        return R.error().message("运行时异常");
    }


    @ExceptionHandler(IOException.class) //统一异常处理:相当于兜底的
    public R handlerIOException(IOException e){
        //实际会记录到日志文件
        e.printStackTrace();
        return R.error().message("IO异常");
    }

    @ExceptionHandler(ClassNotFoundException.class) //统一异常处理:相当于兜底的
    public R handlerClassNotFoundException(ClassNotFoundException e){
        //实际会记录到日志文件
        e.printStackTrace();
        return R.error().message("对应的class找不到异常");
    }

    //特定异常处理
    @ExceptionHandler(ArithmeticException.class) //统一异常处理:相当于兜底的
    public R handlerArithmeticException(ArithmeticException e){
        //实际会记录到日志文件
        e.printStackTrace();
        log.error(e.getMessage());
        return R.error().message("数学异常");
    }


    @ExceptionHandler(SQLException.class) //统一异常处理:相当于兜底的
    public R handlerSQLException(SQLException e){
        //实际会记录到日志文件
        e.printStackTrace();
        return R.error().message("sql异常");
    }


    @ExceptionHandler(YYGHException.class) //统一异常处理:相当于兜底的
    public R handlerYYGHException(YYGHException e){
        //实际会记录到日志文件
        e.printStackTrace();
        return R.error().message(e.getMessage()).code(e.getCode());
    }



}
