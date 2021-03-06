package com.mybatishelper.demo.common.module;

import java.beans.Transient;
import java.io.Serializable;

/**
 * creator : whh-lether
 * date    : 2019/6/20 9:12
 * desc    :
 **/
public final class Result<T> implements Serializable{
    private static final long serialVersionUID = 1L;
    private boolean succeed;
    private String code;
    private String message;
    private T content;

    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
    public T getContent() {
        return content;
    }

    private Result<T> setSucceed(boolean succeed) {
        this.succeed = succeed;
        return this;
    }

    private Result<T> setCode(String code) {
        this.code = code;
        return this;
    }

    private Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    private Result<T> setContent(T content) {
        this.content = content;
        return this;
    }

    private Result() {}
    @Transient
    public final boolean isSucceed(){
        return succeed;
    }
    @Transient
    public final boolean sad(){
        return !succeed;
    }

    public static Result success(){
        return success(null);
    }
    public static <T> Result<T> success(T content){
        return success(content,"success");
    }
    public static <T> Result<T> success(T content,String message){
        Result<T> result = new Result<>();
        result.setSucceed(true)
                .setCode("200")
                .setMessage(message)
                .setContent(content);
        return result;
    }

    public static <T> Result<T> miss(String code,String message,T content){
        Result<T> result = new Result<>();
        result
                .setSucceed(false)
                .setCode(code)
                .setMessage(message)
                .setContent(content);
        return result;
    }
}
