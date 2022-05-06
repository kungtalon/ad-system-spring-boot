package org.talon.ad.index;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Zelong
 * On 2022/5/3
 * Implements ApplicationContextAware and PriorityOrdered interfaces
 * An encapsulation of all IndexAware beans with cache
 * PriorityOrdered beans are initialized before other beans
 **/
@Component
public class DataTable implements ApplicationContextAware, PriorityOrdered {

    private static ApplicationContext context;

    private static final Map<Class, Object> dataTableMap = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DataTable.context = applicationContext;
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }

    public static <T> T of(Class<T> clasz) {
        T instance = (T) dataTableMap.get(clasz);
        if (null != instance) {
            return instance;
        }

        // initialize the corresponding bean with its class
        dataTableMap.put(clasz, bean(clasz));
        return (T) dataTableMap.get(clasz);
    }


    private static <T> T bean(String beanName) {
        return (T) context.getBean(beanName);
    }

    private static <T> T bean(Class clasz) {
        return (T) context.getBean(clasz);
    }


}
