package com.rudecrab.demo.config;

import com.rudecrab.demo.annotation.ExceptionCode;
import com.rudecrab.demo.enums.ResultCode;
import com.rudecrab.demo.exception.APIException;
import com.rudecrab.demo.vo.ResultVO;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.Field;

/**
 * @author RudeCrab
 * @description 全局异常处理
 */
@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(APIException.class)
    public ResultVO<String> APIExceptionHandler(APIException e) {
        return new ResultVO<>(ResultCode.FAILED, e.getMsg());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVO<String> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) throws NoSuchFieldException {
        // 从异常对象中拿到错误信息
        String defaultMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        // 入参Class对象
        Class<?> parameterType = e.getParameter().getParameterType();
        // 拿到错误的参数名称
        FieldError fieldError = e.getBindingResult().getFieldError();
        Field field = parameterType.getDeclaredField(fieldError.getField());
        // 获取参数上的自定义注解
        ExceptionCode annotation = field.getAnnotation(ExceptionCode.class);

        if (annotation != null) {
            return new ResultVO<>(annotation, defaultMessage);
        }


        // 然后提取错误提示信息进行返回
        return new ResultVO<>(ResultCode.VALIDATE_FAILED, defaultMessage);
    }

}
