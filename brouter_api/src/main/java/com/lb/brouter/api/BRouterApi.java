package com.lb.brouter.api;


import com.lb.brouter.api.internal.BRouterIndex;
import com.lb.brouter.api.internal.BRouterInfo;

import java.lang.reflect.Method;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * 路由单例api
 *
 * <p> 主要功能：
 * <ul>
 *   <li> 注册路由表
 *   <li> 路由到目标页面的的目标方法
 * </ul>
 *
 * <p>
 * Created by lb on 2022/01/01
 */
public class BRouterApi {
    public static final String TAG = "[BRouterApi]";

    private BRouterInfo allRouterInfo = new BRouterInfo();
    private long routerStart;

    private BRouterApi() {
    }

    private static class InstanceHolder {
        private static final BRouterApi instance = new BRouterApi();
    }

    public static BRouterApi i() {
        return InstanceHolder.instance;
    }

    /**
     * 注册路由表
     *
     * @param indexStrings
     */
    public void registerRouterIndex(String... indexStrings) {
        if (indexStrings == null || indexStrings.length == 0) {
            return;
        }
        // 清空已经注册的所有理由表
        allRouterInfo = new BRouterInfo();
        // 收集所有路由表信息，会检测重复path
        for (String index : indexStrings) {
            BRouterIndex routerIndex = getRouterInfo(index);
            if (routerIndex != null) {
                String duplicatedPath = allRouterInfo.isDuplicateIndex(routerIndex);
                if (duplicatedPath == null) {
                    if (routerIndex.getRouterInfo() != null) {
                        allRouterInfo.add(routerIndex.getRouterInfo());
                    }
                } else {
                    throw new RuntimeException(
                            TAG + " #register  Duplicated path: " + duplicatedPath);
                }
            }
        }
    }

    @Nullable
    private BRouterIndex getRouterInfo(String indexFullClassName) {
        try {
            Class<?> routerIndexClass = Class.forName(indexFullClassName);
            return (BRouterIndex) routerIndexClass.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 路由
     *
     * @param path
     * @param params
     * @return 如果路由成功，返回对应的路由方法的返回值，否则返回部分异常
     * @throws BRouterException 异常，需要catch并处理
     */
    public Object route(String path, Object... params) throws BRouterException {
        routerStart = System.currentTimeMillis();
        /**
         * 获取path对应的具体类的路由信息
         */
        BRouterInfo.RouterMeta routerMeta = allRouterInfo.getRouterMeta(path.toLowerCase());
        if (routerMeta == null) {
            return new BRouterException(BRouterException.ExceptionType.PATH_NOT_MATCH,
                    TAG + "  #Route  Err, no router page matches this path: " + path);
        }

        /**
         * 获取类里所有被 @BRouterMethod 注解的路由方法
         */
        List<BRouterInfo.MethodMeta> methodMetas = routerMeta.getMethodMetas();
        if (methodMetas.isEmpty()) {
            return new BRouterException(BRouterException.ExceptionType.NO_ANNO_METHOD,
                    TAG + "  #Route  Err, no router method found in the page with path: " + path);
        }

        /**
         * 查找对应的方法
         * <P>
         * 遍历目标页面内所有路由方法，根据实参类型与顺序匹配路由方法
         */
        String methodStr = null;
        int routeParamsLen = params == null ? 0 : params.length;
        List<Class<?>> targetParamsFullClasses = null;
        for (BRouterInfo.MethodMeta methodMeta : methodMetas) {
            List<Class<?>> paramsFullClasses = methodMeta.getParamsFullClasses();
            if (paramsFullClasses == null) {
                if (routeParamsLen == 0) {
                    methodStr = methodMeta.getMethod();
                    break;
                } else {
                    continue;
                }
            }
            if (routeParamsLen != paramsFullClasses.size()) {
                continue;
            }
            int i = 0;
            for (; i < routeParamsLen; i++) {
                if (!BRouterUtil.isCompatibleType(paramsFullClasses.get(i), params[i])) {
                    break;
                }
            }
            if (i == routeParamsLen) { // found
                methodStr = methodMeta.getMethod();
                targetParamsFullClasses = paramsFullClasses;
                break;
            }
        }

        if (methodStr == null || methodStr.length() == 0) {
            throw new BRouterException(BRouterException.ExceptionType.NO_MATCH_METHOD,
                    TAG + " #Route  Err, no match method found");
        }

        Class<?> targetClazz = null;
        Method method = null;
        try {
            targetClazz = Class.forName(routerMeta.getFullClassName());
            if (targetParamsFullClasses == null) {
                method = targetClazz.getMethod(methodStr);
            } else {
                method = targetClazz.getMethod(methodStr,
                        targetParamsFullClasses.toArray(new Class[targetParamsFullClasses.size()]));
            }
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new BRouterException(BRouterException.ExceptionType.SYSTEM,
                    TAG + " #Route  Err, ClassNotFound or NoSuchMethod", e);
        }

        /**
         * invoke静态方法
         *
         * 因为invoke的第2个参数是可变数量参数，所以需要根据实际参数个数分别invoke
         */
        Object result = BRouterUtil.invoke(method, targetClazz, params);
        BRouterLog.d(TAG, "#Route  Route costs time: " + (System.currentTimeMillis() - routerStart));
        return result;
    }
}
