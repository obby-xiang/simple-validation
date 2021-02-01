package com.obby.validation;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * 数据验证失败异常类
 *
 * @author obby-xiang
 * @since 2021-02-01
 */
public class ValidationException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "The given data is invalid.";

    /**
     * 验证失败消息
     */
    @Getter
    private final Map<String, List<String>> errors;

    /**
     * 构造
     *
     * @param errors 验证失败消息
     */
    public ValidationException(Map<String, List<String>> errors) {
        this(null, errors);
    }

    /**
     * 构造
     *
     * @param message 异常消息
     * @param errors  验证失败消息
     */
    public ValidationException(String message, Map<String, List<String>> errors) {
        super(ObjectUtils.defaultIfNull(message, DEFAULT_MESSAGE));

        this.errors = errors;
    }

}
