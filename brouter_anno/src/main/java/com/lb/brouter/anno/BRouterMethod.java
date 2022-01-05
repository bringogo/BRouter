package com.lb.brouter.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解路由方法
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface BRouterMethod {
}
