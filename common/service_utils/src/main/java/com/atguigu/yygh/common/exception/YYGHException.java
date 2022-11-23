package com.atguigu.yygh.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class YYGHException extends RuntimeException{
    private Integer code;
    private String message;
}
