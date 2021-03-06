package com.example.flashsale.Exception;

import com.example.flashsale.result.CodeMsg;
import com.example.flashsale.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(value=Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest request, Exception exception){
        exception.printStackTrace();

        if (exception instanceof GlobalException){
            GlobalException ex = (GlobalException) exception;
            return  Result.error(ex.getCm());
        } else if (exception instanceof BindException){
            BindException ex = (BindException) exception;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);

            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        } else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }

}
