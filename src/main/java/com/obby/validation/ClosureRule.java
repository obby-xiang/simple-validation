package com.obby.validation;

import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * 闭包数据验证规则类
 *
 * @param <T> 验证对象类型
 * @author obby-xiang
 * @since 2021-01-28
 */
public class ClosureRule<T> extends Rule<T, ClosureRule<T>> {

    /**
     * 验证规则闭包
     */
    private final RuleClosure<T> closure;

    /**
     * 规则闭包是否验证失败
     */
    private boolean closureFailed;

    /**
     * 构造
     *
     * @param closure 验证规则闭包
     */
    public ClosureRule(@NonNull RuleClosure<T> closure) {
        Assert.notNull(closure, "[closure] must not be null");

        this.closure = closure;
    }

    /**
     * 创建闭包数据验证规则
     *
     * @param closure 验证规则闭包
     * @param <T>     验证对象类型
     * @return 包数据验证规则
     */
    public static <T> ClosureRule<T> make(@NonNull RuleClosure<T> closure) {
        return new ClosureRule<>(closure);
    }

    /**
     * 默认验证消息
     *
     * @return 默认验证消息
     */
    @Override
    public String defaultMessage() {
        return null;
    }

    /**
     * 测试数据
     *
     * @param data 测试对象
     * @return 是否通过测试
     */
    @Override
    public boolean test(T data) {
        this.closureFailed = false;

        this.closure.validate(data, (message) -> this.customMessage(message).closureFailed = true);

        return !this.closureFailed;
    }

    /**
     * 数据验证规则闭包接口
     *
     * @param <T> 验证对象类型
     */
    @FunctionalInterface
    public interface RuleClosure<T> {

        /**
         * 验证数据
         *
         * @param data 验证对象
         * @param fail 验证失败闭包
         */
        void validate(T data, FailClosure fail);

    }

    /**
     * 数据验证失败闭包接口
     */
    @FunctionalInterface
    public interface FailClosure {

        /**
         * 设置验证失败消息
         *
         * @param message 验证失败消息
         */
        void message(String message);

    }

}
