package com.obby.validation.rule;

import com.obby.validation.Rule;
import org.apache.commons.lang3.ObjectUtils;

/**
 * 必填校验类
 *
 * @author obby-xiang
 * @since 2021-01-28
 */
public class Required extends Rule<Object> {

    /**
     * 默认消息
     *
     * @return 默认消息
     */
    @Override
    public String defaultMessage() {
        return "validation.required";
    }

    /**
     * 测试
     *
     * @param data 测试对象
     * @return 是否通过测试
     */
    @Override
    public boolean test(Object data) {
        return ObjectUtils.isNotEmpty(data);
    }

}
