package com.fyugp.fyugp_attendance_api.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * Bean utils.
 */
@Component
public class BeanUtil implements ApplicationContextAware, ApplicationEventPublisherAware {

    private static ApplicationContext context;
    private static ApplicationEventPublisher eventPublisher;

    public static <T> T getBean(Class<T> cls) {
        return context.getBean(cls);
    }

    public static ApplicationEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanUtil.context = applicationContext;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        BeanUtil.eventPublisher = applicationEventPublisher;
    }
}
