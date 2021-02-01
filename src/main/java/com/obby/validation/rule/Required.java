package com.obby.validation.rule;

import com.obby.validation.Rule;
import org.apache.commons.lang3.ObjectUtils;

/**
 * 必填数据验证规则类
 *
 * @author obby-xiang
 * @since 2021-01-28
 */
public class Required extends Rule<Object, Required> {

    /**
     * 创建必填数据验证规则
     *
     * @return 必填数据验证规则
     */
    public static Required make() {
        return new Required();
    }

    /**
     * 默认验证消息
     *
     * @return 默认验证消息
     */
    @Override
    public String defaultMessage() {
        return "validation.required";
    }

    /**
     * 测试数据
     *
     * @param data 测试对象
     * @return 是否通过测试
     */
    @Override
    public boolean test(Object data) {
        return ObjectUtils.isNotEmpty(data);
    }

}
