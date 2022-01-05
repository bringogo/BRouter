package com.lb.brouter.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import androidx.annotation.IntDef;

/**
 * 路由异常
 * <p>
 * Created by lb on 2022/01/01
 */
public class BRouterException extends Exception {

    @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD,
            ElementType.LOCAL_VARIABLE})
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {ExceptionType.DUPLICATE_PATH, ExceptionType.PATH_NOT_MATCH,
            ExceptionType.NO_ANNO_METHOD,
            ExceptionType.NO_MATCH_METHOD, ExceptionType.FOR_CLASS_NAME, ExceptionType.SYSTEM})
    public @interface ExceptionType {
        int DUPLICATE_PATH = 1;
        /**
         * path不match任何路由页面
         */
        int PATH_NOT_MATCH = 2;
        /**
         * 路由页面里没有注册路由方法
         */
        int NO_ANNO_METHOD = 3;
        /**
         * 没做匹配到路由方法
         */
        int NO_MATCH_METHOD = 4;
        /**
         * 在用Class.forName匹配class时异常
         */
        int FOR_CLASS_NAME = 5;
        /**
         * 系统api调用异常
         */
        int SYSTEM = 100;
    }

    @ExceptionType
    private int type = ExceptionType.SYSTEM;
    private Exception warpedException;

    public BRouterException(@ExceptionType int type, String message) {
        super(message);
        this.type = type;
    }

    public BRouterException(@ExceptionType int type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public BRouterException(@ExceptionType int type, String message, Exception exception) {
        super(message, exception);
        this.type = type;
        warpedException = exception;
    }

    @ExceptionType
    public int getType() {
        return type;
    }

    public Exception getWarpedException() {
        return warpedException;
    }

    @Override
    public String toString() {
        return "BRouterException{" +
                "type=" + type +
                ", message=" + getMessage() +
                ", warpedException=" + warpedException +
                '}';
    }
}