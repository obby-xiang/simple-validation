package com.obby.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * 应用工具类
 *
 * @author obby-xiang
 * @since 2021-01-28
 */
@Component
public final class App implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 应用上下文
     *
     * @return 应用上下文
     */
    public static ApplicationContext context() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

}
