package com.hx.repository.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;

/**
 * SpringContext
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-15 11:27
 */
@ComponentScan(basePackages = {"com.hx"})
@EnableAutoConfiguration
@PropertySource(value = "classpath:application.properties")
public class SpringContext {

    /** context */
    private static ApplicationContext context;

    /**
     * init 初始化 SpringContext
     * 备注 : 请确保使用 EquinoxClassLoader 来加载, 而不是 AppClassloader, 否则 e4 中未必能够使用
     *
     * @return void
     * @author Jerry.X.He
     * @date 2021-01-15 11:49
     */
    public static void init() {
        if (context != null) {
            return;
        }

        context = new AnnotationConfigApplicationContext(SpringContext.class);
    }

    /**
     * 获取 springContext
     *
     * @return org.springframework.context.ApplicationContext
     * @author Jerry.X.He
     * @date 2021-01-15 11:29
     */
    public static ApplicationContext get() {
        return context;
    }

    /**
     * 从 applicationContext 里面复制出来的一系列方法
     *
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-15 11:49
     */
    public static String getId() {
        return context.getId();
    }

    public static String getApplicationName() {
        return context.getApplicationName();
    }

    public static String getDisplayName() {
        return context.getDisplayName();
    }

    public static long getStartupDate() {
        return context.getStartupDate();
    }

    public static ApplicationContext getParent() {
        return context.getParent();
    }

    public static AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        return context.getAutowireCapableBeanFactory();
    }

    public static BeanFactory getParentBeanFactory() {
        return context.getParentBeanFactory();
    }

    public static boolean containsLocalBean(String name) {
        return context.containsLocalBean(name);
    }

    public static boolean containsBeanDefinition(String beanName) {
        return context.containsBeanDefinition(beanName);
    }

    public static int getBeanDefinitionCount() {
        return context.getBeanDefinitionCount();
    }

    public static String[] getBeanDefinitionNames() {
        return context.getBeanDefinitionNames();
    }

    public static String[] getBeanNamesForType(ResolvableType type) {
        return context.getBeanNamesForType(type);
    }

    public static String[] getBeanNamesForType(ResolvableType type, boolean includeNonSingletons,
                                               boolean allowEagerInit) {
        return context.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    public static String[] getBeanNamesForType(Class<?> type) {
        return context.getBeanNamesForType(type);
    }

    public static String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        return context.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return context.getBeansOfType(type);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
            throws BeansException {
        return context.getBeansOfType(type, includeNonSingletons, allowEagerInit);
    }

    public static String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        return context.getBeanNamesForAnnotation(annotationType);
    }

    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType)
            throws BeansException {
        return context.getBeansWithAnnotation(annotationType);
    }

    public static <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
            throws NoSuchBeanDefinitionException {
        return context.findAnnotationOnBean(beanName, annotationType);
    }

    public static Object getBean(String name) throws BeansException {
        return context.getBean(name);
    }

    public static <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return context.getBean(name, requiredType);
    }

    public static Object getBean(String name, Object... args) throws BeansException {
        return context.getBean(name, args);
    }

    public static <T> T getBean(Class<T> requiredType) throws BeansException {
        return context.getBean(requiredType);
    }

    public static <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        return context.getBean(requiredType, args);
    }

    public static <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
        return context.getBeanProvider(requiredType);
    }

    public static <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
        return context.getBeanProvider(requiredType);
    }

    public static boolean containsBean(String name) {
        return context.containsBean(name);
    }

    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return context.isSingleton(name);
    }

    public static boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        return context.isPrototype(name);
    }

    public static boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        return context.isTypeMatch(name, typeToMatch);
    }

    public static boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        return context.isTypeMatch(name, typeToMatch);
    }

    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return context.getType(name);
    }

    public static Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        return context.getType(name, allowFactoryBeanInit);
    }

    public static String[] getAliases(String name) {
        return context.getAliases(name);
    }

    public static void publishEvent(Object event) {
        context.publishEvent(event);
    }

    public static String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return context.getMessage(code, args, defaultMessage, locale);
    }

    public static String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        return context.getMessage(code, args, locale);
    }

    public static String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return context.getMessage(resolvable, locale);
    }

    public static Environment getEnvironment() {
        return context.getEnvironment();
    }

    public static Resource[] getResources(String locationPattern) throws IOException {
        return context.getResources(locationPattern);
    }

    public static Resource getResource(String location) {
        return context.getResource(location);
    }

    public static ClassLoader getClassLoader() {
        return context.getClassLoader();
    }

}
