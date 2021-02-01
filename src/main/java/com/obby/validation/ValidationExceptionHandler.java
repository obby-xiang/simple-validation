package com.obby.validation;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 数据验证失败异常处理类
 *
 * @author obby-xiang
 * @since 2021-02-01
 */
@ControllerAdvice
public class ValidationExceptionHandler {

    @Autowired
    private Gson gson;

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handle(ValidationException exception) {
        return ResponseEntity.unprocessableEntity().body(
                this.gson.toJson(
                        ImmutableMap.builder()
                                .put("message", exception.getMessage())
                                .put("errors", exception.getErrors())
                                .build()
                )
        );
    }

}
