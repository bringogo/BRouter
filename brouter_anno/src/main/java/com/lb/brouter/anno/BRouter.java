package com.lb.brouter.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解路由目标
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface BRouter {
    /**
     * 页面路径。必须设置，必须全工程唯一，大小写不敏感。
     * @return
     */
    String path() default  "";

    /**
     * 页面描述。可选。
     * @return
     */
    String desc() default "";
}
