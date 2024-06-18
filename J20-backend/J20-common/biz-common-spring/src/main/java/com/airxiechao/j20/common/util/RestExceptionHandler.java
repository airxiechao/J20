package com.airxiechao.j20.common.util;

import com.airxiechao.j20.common.api.pojo.constant.ConstRespCode;
import com.airxiechao.j20.common.api.pojo.rest.Resp;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;

/**
 * 请求异常处理器
 */
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * 参数验证失败异常处理
     * @param ex 参数验证失败异常
     * @param headers HTTP 头
     * @param status HTTP 响应状态码
     * @param request HTTP 请求
     * @return 响应对象
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream().map(e -> String.format("[%s] %s", e.getField(), e.getDefaultMessage())).collect(Collectors.joining("; "));

        Resp resp = new Resp(ConstRespCode.ERROR, message, null);
        return new ResponseEntity(resp, headers, HttpServletResponse.SC_OK);
    }


}
