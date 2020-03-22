package com.changgou.entity;

/**
 * 包下建立类Result用于微服务返回结果给前端
 */
public class Result<T> {

    private boolean flag;//是否成功
    private Integer code;//返回码
    private String message;//返回消息
    private T data;//返回数据

    //满参
    public Result(boolean flag, Integer code, String message, Object data) { // 为什么是objectdata
        this.flag = flag;
        this.code = code;
        this.message = message;
        this.data = (T) data;
    }
    //三个参数 flag code message
    public Result(boolean flag, Integer code, String message) {
        this.flag = flag;
        this.code = code;
        this.message = message;
    }
    //无参
    public Result() {
    this.flag = true;
    this.code = StatusCode.OK;
    this.message = "操作成功！";
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
