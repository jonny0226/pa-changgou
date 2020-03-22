package com.changgou.framework.exception;

import ch.qos.logback.core.status.Status;
import com.changgou.entity.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 为了使我们的代码更容易维护，我们创建一个类集中处理异常,该异常类可以创建在changgou-common工程中
 */

@ControllerAdvice//全局捕获异常类，只要作用在@RequestMapping上，所有的异常都会被捕获。
public class BaseExceptionHandler {

    /**
     * 异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return new Result(false, Status.ERROR,e.getMessage());//e.getMessage() e就是传进来的这个参数 需要获取到它带过来的异常
    }

}
