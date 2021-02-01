package com.obby.validation;

import com.obby.support.App;
import com.samskivert.mustache.Mustache;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 数据验证规则抽象类
 *
 * @param <T> 验证对象类型
 * @param <B> 验证规则子类类型
 * @author obby-xiang
 * @since 2021-01-28
 */
public abstract class Rule<T, B extends Rule<T, B>> {

    private static final Logger logger = LoggerFactory.getLogger(Rule.class);

    public static final String DEFAULT_MESSAGE = "validation.invalid";

    /**
     * 验证对象
     */
    private T data;

    /**
     * 是否验证失败
     */
    private boolean failed;

    /**
     * 验证失败消息
     */
    private String failedMessage;

    /**
     * 自定义验证消息
     */
    private String customMessage;

    /**
     * 默认验证消息
     *
     * @return 默认验证消息
     */
    public abstract String defaultMessage();

    /**
     * 测试数据
     *
     * @param data 测试对象
     * @return 是否通过测试
     */
    public abstract boolean test(T data);

    /**
     * 设置自定义验证消息
     *
     * @param customMessage 自定义验证消息
     * @return 数据验证规则
     */
    @SuppressWarnings("unchecked")
    public B customMessage(String customMessage) {
        this.customMessage = customMessage;

        return (B) this;
    }

    /**
     * 验证对象
     *
     * @return 验证对象
     */
    public T data() {
        return this.data;
    }

    /**
     * 是否验证失败
     *
     * @return 是否验证失败
     */
    public boolean failed() {
        return this.failed;
    }

    /**
     * 验证失败消息
     *
     * @return 验证失败消息
     */
    public String failedMessage() {
        return this.failedMessage;
    }

    /**
     * 自定义验证消息
     *
     * @return 自定义验证消息
     */
    public String customMessage() {
        return this.customMessage;
    }

    /**
     * 验证数据
     *
     * @param data 验证对象
     */
    public void validate(T data) {
        this.data = data;

        this.validate();
    }

    /**
     * 验证数据
     */
    private void validate() {
        if (this.failed = !this.test(this.data)) {
            String message = ObjectUtils.defaultIfNull(
                    ObjectUtils.defaultIfNull(this.customMessage(), this.defaultMessage()),
                    DEFAULT_MESSAGE
            );

            message = App.context().getMessage(message, null, message, LocaleContextHolder.getLocale());

            try {
                this.failedMessage = Mustache.compiler().defaultValue("{{{name}}}").compile(message).execute(this);
            } catch (Exception e) {
                logger.debug("process message template failed", e);

                this.failedMessage = message;
            }
        } else {
            this.failedMessage = null;
        }
    }

}
