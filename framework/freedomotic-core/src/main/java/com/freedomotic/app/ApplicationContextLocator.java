/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.app;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 *
 * @author nicoletti
 */
@Component
public class ApplicationContextLocator {

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public ApplicationContextLocator() {
        super();
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextLocator.applicationContext = applicationContext;
    }

    public static void forceInjection(Object object) {
        AutowireCapableBeanFactory factory = getApplicationContext().getAutowireCapableBeanFactory();
        factory.autowireBean(object);
    }

}
