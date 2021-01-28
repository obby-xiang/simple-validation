package com.obby.validation;

import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * 闭包校验规则类
 *
 * @author obby-xiang
 * @since 2021-01-28
 */
public class ClosureRule<T> extends Rule<T> {

    /**
     * 校验规则闭包
     */
    private final RuleClosure<T> closure;

    /**
     * 闭包是否校验失败
     */
    private boolean closureFailed;

    /**
     * 构造
     *
     * @param closure 校验规则闭包
     */
    public ClosureRule(@NonNull RuleClosure<T> closure) {
        Assert.notNull(closure, "[closure] must not be null");

        this.closure = closure;
    }

    /**
     * 默认消息
     *
     * @return 默认消息
     */
    @Override
    public String defaultMessage() {
        return null;
    }

    /**
     * 测试
     *
     * @param data 测试对象
     * @return 是否通过测试
     */
    @Override
    public boolean test(T data) {
        this.closureFailed = false;

        this.closure.validate(data, (message) -> {
            this.closureFailed = true;
            this.customMessage(message);
        });

        return !this.closureFailed;
    }

    /**
     * 校验规则闭包接口
     *
     * @param <T> 校验对象类型
     */
    @FunctionalInterface
    public interface RuleClosure<T> {

        /**
         * 校验
         *
         * @param data 校验对象
         * @param fail 校验失败闭包
         */
        void validate(T data, FailClosure fail);

    }

    /**
     * 校验失败闭包接口
     */
    @FunctionalInterface
    public interface FailClosure {

        /**
         * 设置校验失败消息
         *
         * @param message 校验失败消息
         */
        void message(String message);

    }

}
