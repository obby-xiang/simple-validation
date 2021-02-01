package com.obby.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据验证器类
 *
 * @author obby-xiang
 * @since 2021-01-28
 */
public class Validator {

    private static final Logger logger = LoggerFactory.getLogger(Validator.class);

    public static final String ATTRIBUTE_OF_DATA = "*";

    /**
     * 字段级验证器列表
     */
    private final List<FieldValidator<?>> fieldValidators;

    /**
     * 验证回调
     */
    private CallbackClosure callback;

    /**
     * 验证条件
     */
    private ConditionClosure<Object> condition;

    /**
     * 是否在首次验证失败后停止验证
     */
    private boolean bail;

    /**
     * 是否在验证失败后抛出异常
     */
    private boolean abort;

    /**
     * 验证失败消息
     */
    private final Map<String, List<String>> errors;

    /**
     * 验证对象
     */
    private Object data;

    /**
     * 构造
     */
    public Validator() {
        this.fieldValidators = new ArrayList<>();
        this.errors = new HashMap<>();
    }

    /**
     * 创建数据验证器
     *
     * @return 数据验证器
     */
    public static Validator create() {
        return new Validator();
    }

    /**
     * 设置字段级验证器
     *
     * @param fieldValidator 字段级验证器
     * @return 数据验证器
     */
    public Validator fieldValidator(@NonNull FieldValidator<?> fieldValidator) {
        Assert.notNull(fieldValidator, "[fieldValidator] must not be null");

        this.fieldValidators.add(fieldValidator);

        return this;
    }

    /**
     * 设置字段级验证器列表
     *
     * @param fieldValidators 字段级验证器列表
     * @return 数据验证器
     */
    public Validator fieldValidators(@NonNull List<FieldValidator<?>> fieldValidators) {
        Assert.notEmpty(fieldValidators, "[fieldValidators] must not be empty");
        Assert.noNullElements(fieldValidators, "[fieldValidators] must not contain any null elements");

        this.fieldValidators.addAll(fieldValidators);

        return this;
    }

    /**
     * 设置验证回调
     *
     * @param callback 验证回调
     * @return 数据验证器
     */
    public Validator callback(@Nullable CallbackClosure callback) {
        this.callback = callback;

        return this;
    }

    /**
     * 设置验证条件
     *
     * @param condition 验证条件
     * @return 数据验证器
     */
    public Validator condition(@Nullable ConditionClosure<Object> condition) {
        this.condition = condition;

        return this;
    }

    /**
     * 设置是否在首次验证失败后停止验证
     *
     * @param bail 是否在首次验证失败后停止验证
     * @return 数据验证器
     */
    public Validator bail(boolean bail) {
        this.bail = bail;

        return this;
    }

    /**
     * 设置是否在验证失败后抛出异常
     *
     * @param abort 是否在验证失败后抛出异常
     * @return 数据验证器
     */
    public Validator abort(boolean abort) {
        this.abort = abort;

        return this;
    }

    /**
     * 字段级验证器列表
     *
     * @return 字段级验证器列表
     */
    public List<FieldValidator<?>> fieldValidators() {
        return this.fieldValidators;
    }

    /**
     * 验证回调
     *
     * @return 验证回调
     */
    public CallbackClosure callback() {
        return this.callback;
    }

    /**
     * 验证条件
     *
     * @return 验证条件
     */
    public ConditionClosure<Object> condition() {
        return this.condition;
    }

    /**
     * 是否在首次验证失败后停止验证
     *
     * @return 是否在首次验证失败后停止验证
     */
    public boolean bail() {
        return this.bail;
    }

    /**
     * 是否在验证失败后抛出异常
     *
     * @return 是否在验证失败后抛出异常
     */
    public boolean abort() {
        return this.abort;
    }

    /**
     * 验证失败消息
     *
     * @return 验证失败消息
     */
    public Map<String, List<String>> errors() {
        return this.errors;
    }

    /**
     * 验证失败消息
     *
     * @param attribute 验证字段属性
     * @return 验证失败消息
     */
    public List<String> errors(String attribute) {
        return this.errors.getOrDefault(attribute, new ArrayList<>());
    }

    /**
     * 是否验证失败
     *
     * @return 是否验证失败
     */
    public boolean failed() {
        return !this.errors().isEmpty();
    }

    /**
     * 是否验证失败
     *
     * @param attribute 验证字段属性
     * @return 是否验证失败
     */
    public boolean failed(String attribute) {
        return !this.errors(attribute).isEmpty();
    }

    /**
     * 验证对象
     *
     * @return 验证对象
     */
    public Object data() {
        return this.data;
    }

    /**
     * 验证数据
     *
     * @param data 验证对象
     */
    public void validate(Object data) {
        this.data = data;

        this.validate();
    }

    /**
     * 验证数据
     */
    private void validate() {
        this.errors.clear();

        if (this.condition == null || this.condition.accept(this.data)) {
            for (FieldValidator<?> validator : this.fieldValidators) {
                validator.validate(this.getValue(validator.attribute()));

                if (validator.failed()) {
                    String attribute = ObjectUtils.defaultIfNull(validator.customAttribute(), validator.attribute());

                    if (!this.errors.containsKey(attribute)) {
                        this.errors.put(attribute, new ArrayList<>());
                    }

                    this.errors.get(attribute).addAll(validator.errors());

                    if (this.bail) {
                        break;
                    }
                }
            }

            if (this.callback != null) {
                this.callback.call(this);
            }

            if (this.abort && this.failed()) {
                throw new ValidationException(this.errors());
            }
        }
    }

    /**
     * 获取验证字段值
     *
     * @param attribute 验证字段属性
     * @return 验证字段值
     */
    private Object getValue(String attribute) {
        if (ATTRIBUTE_OF_DATA.equals(attribute) || this.data == null) {
            return this.data;
        }

        if (this.data instanceof Map) {
            return ((Map<?, ?>) this.data).get(attribute);
        } else {
            try {
                return new DirectFieldAccessor(this.data).getPropertyValue(attribute);
            } catch (Exception e) {
                logger.debug("get value failed", e);

                return null;
            }
        }
    }

    /**
     * 数据验证回调接口
     */
    @FunctionalInterface
    public interface CallbackClosure {

        /**
         * 数据验证回调
         *
         * @param validator 数据验证器
         */
        void call(Validator validator);

    }

    /**
     * 数据验证条件接口
     *
     * @param <T> 验证对象类型
     */
    @FunctionalInterface
    public interface ConditionClosure<T> {

        /**
         * 是否符合验证条件
         *
         * @param data 验证对象
         * @return 是否符合验证条件
         */
        boolean accept(T data);

    }

    /**
     * 字段级数据验证器
     *
     * @param <T> 验证字段类型
     */
    public static class FieldValidator<T> {

        private static final String DEFAULT_ATTRIBUTE = ATTRIBUTE_OF_DATA;

        /**
         * 验证字段属性
         */
        private String attribute;

        /**
         * 自定义验证字段属性
         */
        private String customAttribute;

        /**
         * 验证规则
         */
        private final List<Rule<? super T, ?>> rules;

        /**
         * 验证条件
         */
        private ConditionClosure<T> condition;

        /**
         * 是否在首次验证失败后停止验证
         */
        private boolean bail;

        /**
         * 验证失败消息
         */
        private final List<String> errors;

        /**
         * 验证字段值
         */
        private T value;

        /**
         * 构造
         */
        public FieldValidator() {
            this.attribute = DEFAULT_ATTRIBUTE;
            this.rules = new ArrayList<>();
            this.errors = new ArrayList<>();
        }

        /**
         * 创建字段级数据验证器
         *
         * @param <T> 验证字段类型
         * @return 字段级数据验证器
         */
        public static <T> FieldValidator<T> create() {
            return new FieldValidator<>();
        }

        /**
         * 设置验证字段属性
         *
         * @param attribute 验证字段属性
         * @return 字段级数据验证器
         */
        public FieldValidator<T> attribute(@NonNull String attribute) {
            Assert.notNull(attribute, "[attribute] must not be null");

            this.attribute = attribute;

            return this;
        }

        /**
         * 设置自定义验证字段属性
         *
         * @param customAttribute 自定义验证字段属性
         * @return 字段级数据验证器
         */
        public FieldValidator<T> customAttribute(@Nullable String customAttribute) {
            this.customAttribute = customAttribute;

            return this;
        }

        /**
         * 设置验证规则
         *
         * @param rule 验证规则
         * @return 字段级数据验证器
         */
        public FieldValidator<T> rule(@NonNull Rule<? super T, ?> rule) {
            Assert.notNull(rule, "[rule] must not be null");

            this.rules.add(rule);

            return this;
        }

        /**
         * 设置验证规则列表
         *
         * @param rules 验证规则列表
         * @return 字段级数据验证器
         */
        public FieldValidator<T> rules(@NonNull List<Rule<? super T, ?>> rules) {
            Assert.notEmpty(rules, "[rules] must not be empty");
            Assert.noNullElements(rules, "[rules] must not contain any null elements");

            this.rules.addAll(rules);

            return this;
        }

        /**
         * 设置验证条件
         *
         * @param condition 验证条件
         * @return 字段级数据验证器
         */
        public FieldValidator<T> condition(@Nullable ConditionClosure<T> condition) {
            this.condition = condition;

            return this;
        }

        /**
         * 设置是否在首次验证失败后停止验证
         *
         * @param bail 是否在首次验证失败后停止验证
         * @return 字段级数据验证器
         */
        public FieldValidator<T> bail(boolean bail) {
            this.bail = bail;

            return this;
        }

        /**
         * 验证字段属性
         *
         * @return 验证字段属性
         */
        public String attribute() {
            return this.attribute;
        }

        /**
         * 自定义验证字段属性
         *
         * @return 自定义验证字段属性
         */
        public String customAttribute() {
            return this.customAttribute;
        }

        /**
         * 验证规则
         *
         * @return 验证规则
         */
        public List<Rule<? super T, ?>> rules() {
            return this.rules;
        }

        /**
         * 验证条件
         *
         * @return 验证条件
         */
        public ConditionClosure<T> condition() {
            return this.condition;
        }

        /**
         * 是否在首次验证失败后停止验证
         *
         * @return 是否在首次验证失败后停止验证
         */
        public boolean bail() {
            return this.bail;
        }

        /**
         * 验证失败消息
         *
         * @return 验证失败消息
         */
        public List<String> errors() {
            return this.errors;
        }

        /**
         * 验证字段值
         *
         * @return 验证字段值
         */
        public T value() {
            return this.value;
        }

        /**
         * 是否验证失败
         *
         * @return 是否验证失败
         */
        public boolean failed() {
            return !this.errors.isEmpty();
        }

        /**
         * 验证数据
         *
         * @param value 验证字段值
         */
        @SuppressWarnings("unchecked")
        public void validate(Object value) {
            try {
                this.value = (T) value;
            } catch (Exception e) {
                logger.debug("cast value failed", e);

                throw new IllegalArgumentException(
                        "[" + this.getClass() + "] may not support [" + value.getClass() + "]"
                );
            }

            this.validate();
        }

        /**
         * 验证数据
         */
        private void validate() {
            this.errors.clear();

            if (this.condition == null || this.condition.accept(this.value)) {
                for (Rule<? super T, ?> rule : this.rules) {
                    rule.validate(this.value);

                    if (rule.failed()) {
                        this.errors.add(rule.failedMessage());

                        if (this.bail) {
                            break;
                        }
                    }
                }
            }
        }

    }

}
