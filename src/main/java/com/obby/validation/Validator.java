package com.obby.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据校验类
 *
 * @author obby-xiang
 * @since 2021-01-28
 */
public class Validator {

    private static final Logger logger = LoggerFactory.getLogger(Validator.class);

    public static final String ATTRIBUTE_OF_DATA = "*";

    /**
     * 校验对象属性
     */
    private final List<String> attributes;

    /**
     * 自定义属性
     */
    private final Map<String, String> customAttributes;

    /**
     * 校验规则
     */
    private final Map<String, List<Rule<?>>> rules;

    /**
     * 校验失败消息
     */
    private final Map<String, List<String>> errors;

    /**
     * 自定义属性校验失败消息
     */
    private final Map<String, List<String>> failedMessages;

    /**
     * 校验对象属性值
     */
    private final Map<String, Object> values;

    /**
     * 校验对象
     */
    private Object data;

    /**
     * 构造
     */
    public Validator() {
        this.attributes = new ArrayList<>();
        this.customAttributes = new HashMap<>();
        this.rules = new HashMap<>();
        this.errors = new HashMap<>();
        this.failedMessages = new HashMap<>();
        this.values = new HashMap<>();
    }

    /**
     * 校验对象属性
     *
     * @return 校验对象属性
     */
    public List<String> attributes() {
        return this.attributes;
    }

    /**
     * 自定义属性
     *
     * @return 自定义属性
     */
    public Map<String, String> customAttributes() {
        return this.customAttributes;
    }

    /**
     * 校验规则
     *
     * @return 校验规则
     */
    public Map<String, List<Rule<?>>> rules() {
        return this.rules;
    }

    /**
     * 属性校验规则
     *
     * @param attribute 属性
     * @return 校验规则
     */
    public List<Rule<?>> rules(String attribute) {
        return this.rules.getOrDefault(attribute, new ArrayList<>());
    }

    /**
     * 校验失败消息
     *
     * @return 校验失败消息
     */
    public Map<String, List<String>> errors() {
        return this.errors;
    }

    /**
     * 属性校验失败消息
     *
     * @param attribute 属性
     * @return 校验失败消息
     */
    public List<String> errors(String attribute) {
        return this.errors.getOrDefault(attribute, new ArrayList<>());
    }

    /**
     * 自定义属性校验失败消息
     *
     * @return 自定义属性校验失败消息
     */
    public Map<String, List<String>> failedMessages() {
        return this.failedMessages;
    }

    /**
     * 校验对象
     *
     * @return 校验对象
     */
    public Object data() {
        return this.data;
    }

    /**
     * 设置校验对象
     *
     * @param data 校验对象
     * @return 数据校验
     */
    public Validator data(Object data) {
        Assert.notNull(data, "[data] must not be null");

        this.data = data;

        return this;
    }

    /**
     * 增加校验规则
     *
     * @param attribute 属性
     * @param rules     规则
     * @return 数据校验
     */
    public Validator rule(String attribute, Rule<?>... rules) {
        return this.rule(attribute, null, rules);
    }

    /**
     * 增加校验规则
     *
     * @param attribute       属性
     * @param customAttribute 自定义属性
     * @param rules           规则
     * @return 数据校验
     */
    public Validator rule(String attribute, @Nullable String customAttribute, Rule<?>... rules) {
        Assert.notNull(attribute, "[attribute] must not be null");
        Assert.notEmpty(rules, "[rules] must not be empty");
        Assert.noNullElements(rules, "[rules] must not contain any null elements");

        this.attributes.remove(attribute);
        this.attributes.add(attribute);

        if (customAttribute == null) {
            this.customAttributes.remove(attribute);
        } else {
            this.customAttributes.put(attribute, customAttribute);
        }

        this.rules.put(attribute, Arrays.stream(rules).collect(Collectors.toList()));

        return this;
    }

    /**
     * 移除校验规则
     */
    public void removeRules() {
        this.attributes.clear();
        this.customAttributes.clear();
        this.rules.clear();
    }

    /**
     * 属性移除校验规则
     *
     * @param attribute 属性
     * @return 是否移除成功
     */
    public boolean removeRules(String attribute) {
        if (this.attributes.contains(attribute)) {
            this.attributes.remove(attribute);
            this.customAttributes.remove(attribute);
            this.rules.remove(attribute);

            return true;
        }

        return false;
    }

    public void validate() {
        Assert.notNull(this.data, "[data] must not be null");
        Assert.notEmpty(this.rules, "[rules] must not be empty");

        this.errors.clear();
        this.failedMessages.clear();
        this.values.clear();

        this.fillValues();

        for (String attr : this.attributes) {
            Object value = this.values.get(attr);
            List<String> messages = new ArrayList<>();

            for (Rule<?> rule : this.rules(attr)) {
                rule.validate(value);

                if (rule.failed()) {
                    messages.add(rule.failedMessage());
                }
            }

            if (!messages.isEmpty()) {
                this.errors.put(attr, messages);

                String customAttribute = this.customAttributes.getOrDefault(attr, attr);

                if (this.failedMessages.containsKey(customAttribute)) {
                    this.failedMessages.get(customAttribute).addAll(messages);
                } else {
                    this.failedMessages.put(customAttribute, new ArrayList<>(messages));
                }
            }
        }
    }

    /**
     * 是否校验失败
     *
     * @return 是否校验失败
     */
    public boolean failed() {
        return !this.errors().isEmpty();
    }

    /**
     * 属性是否校验失败
     *
     * @param attribute 属性
     * @return 是否校验失败
     */
    public boolean failed(String attribute) {
        return !this.errors(attribute).isEmpty();
    }

    /**
     * 填充属性值
     */
    private void fillValues() {
        if (this.data instanceof Map) {
            Map<?, ?> model = (Map<?, ?>) this.data;

            for (String attr : this.attributes) {
                if (!ATTRIBUTE_OF_DATA.equals(attr)) {
                    this.values.put(attr, model.get(attr));
                }
            }
        } else {
            BeanWrapper model = new BeanWrapperImpl(this.data);

            for (String attr : this.attributes) {
                if (!ATTRIBUTE_OF_DATA.equals(attr)) {
                    this.values.put(attr, model.getPropertyValue(attr));
                }
            }
        }

        if (this.attributes.contains(ATTRIBUTE_OF_DATA)) {
            this.values.put(ATTRIBUTE_OF_DATA, this.data);
        }
    }

}
