package com.atguigu.yygh.common.result;


public enum  ResultCode {

    SUCCESS(true,20000,"成功"),
    ERROR(false,20001,"失败")
    ;
    private boolean success;
    private Integer code;
    private String message;

    ResultCode(boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
