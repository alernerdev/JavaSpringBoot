package com.pragmaticbitbucket.app.ws;


import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.beans.BeansException;

/* this class is for those cases where I need to autowire something in a non-component class --
in those cases, you need to manually get the bean
 */
public class SpringApplicationContext implements ApplicationContextAware {
    private static ApplicationContext CONTEXT;

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        CONTEXT = context;
    }

    public static Object getBean(String beanName) {
        return CONTEXT.getBean(beanName);
    }
}
