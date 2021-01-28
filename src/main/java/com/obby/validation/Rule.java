package com.obby.validation;

import com.obby.support.App;
import com.samskivert.mustache.Mustache;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 校验规则抽象类
 *
 * @author obby-xiang
 * @since 2021-01-28
 */
public abstract class Rule<T> {

    private static final Logger logger = LoggerFactory.getLogger(Rule.class);

    public static final String DEFAULT_MESSAGE = "validation.invalid";

    /**
     * 是否校验失败
     */
    private boolean failed;

    /**
     * 校验失败消息
     */
    private String failedMessage;

    /**
     * 自定义消息
     */
    private String customMessage;

    /**
     * 默认消息
     *
     * @return 默认消息
     */
    public abstract String defaultMessage();

    /**
     * 测试
     *
     * @param data 测试对象
     * @return 是否通过测试
     */
    public abstract boolean test(T data);

    /**
     * 是否校验失败
     *
     * @return 是否校验失败
     */
    public boolean failed() {
        return this.failed;
    }

    /**
     * 校验失败消息
     *
     * @return 校验失败消息
     */
    public String failedMessage() {
        return this.failedMessage;
    }

    /**
     * 自定义消息
     *
     * @return 自定义消息
     */
    public String customMessage() {
        return this.customMessage;
    }

    /**
     * 设置自定义消息
     *
     * @param customMessage 自定义消息
     * @return 校验规则
     */
    public Rule<T> customMessage(String customMessage) {
        this.customMessage = customMessage;

        return this;
    }

    /**
     * 校验
     *
     * @param data 校验对象
     */
    @SuppressWarnings("unchecked")
    public void validate(Object data) {
        T value = null;

        if (data != null) {
            try {
                value = (T) data;
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "[" + this.getClass() + "] does not support [" + data.getClass() + "]"
                );
            }
        }

        if (this.failed = !this.test(value)) {
            String message = ObjectUtils.defaultIfNull(
                    ObjectUtils.defaultIfNull(this.customMessage(), this.defaultMessage()),
                    DEFAULT_MESSAGE
            );

            message = App.context().getMessage(message, null, message, LocaleContextHolder.getLocale());

            try {
                this.failedMessage = Mustache.compiler()
                        .defaultValue("{{{name}}}").escapeHTML(false)
                        .compile(message).execute(this);
            } catch (Exception e) {
                logger.debug("process message template failed", e);

                this.failedMessage = message;
            }
        } else {
            this.failedMessage = null;
        }
    }

}
