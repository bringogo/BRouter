package com.lb.brouter.api.internal;

import com.lb.brouter.api.BRouterLog;
import com.lb.brouter.api.BRouterUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 路由信息
 *
 * <p>
 * 包括路由目标、路由方法详细信息
 * <p>
 * Created by lb on 2022/01/01
 */
public class BRouterInfo {
    private static final String TAG = "[BRouterInfo]";

    private Map<String, RouterMeta> routerMap = new HashMap<>();

    @NonNull
    public Map<String, RouterMeta> getRouterMap() {
        return routerMap;
    }

    @Nullable
    public RouterMeta getRouterMeta(String path) {
        return routerMap.get(path);
    }

    public void add(@NonNull BRouterInfo info) {
        if (info != null) {
            try {
                routerMap.putAll(info.routerMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addRouter(@NonNull String path, @NonNull String fullClassName,
                          @NonNull String method, String... paramsFullClassNames) {
        if (routerMap.containsKey(path)) {
            RouterMeta rm = routerMap.get(path);
            if (rm != null) {
                if (fullClassName.equals(rm.fullClassName)) {
                    if (rm.methodMetas == null) {
                        rm.methodMetas = new ArrayList<>();
                    }
                    rm.methodMetas.add(new MethodMeta(method, paramsFullClassNames));
                } else {
                    // 不会发生。在BRouterHandler.collectBRouter里已经处理重复path
                }
            }
        } else {
            routerMap.put(path, new RouterMeta(fullClassName)
                    .methodMeta(new MethodMeta(method, paramsFullClassNames)));
        }
    }

    /**
     * 判断路由是否重复
     *
     * @param index 新路由表
     * @return String 路由重复的key
     */
    public String isDuplicateIndex(@NonNull BRouterIndex index) {
        BRouterInfo info = index.getRouterInfo();
        if (info == null || info.getRouterMap().isEmpty()) {
            return null;
        }
        for (String key : info.getRouterMap().keySet()) {
            if (this.routerMap.containsKey(key)) {
                return key;
            }
        }
        return null;
    }


    /**
     * 代表一个路由类及其他辅助路由信息如路由方法信息
     */
    public static class RouterMeta {
        private final String fullClassName;
        private List<MethodMeta> methodMetas;

        RouterMeta(@NonNull String fullClassName) {
            this.fullClassName = fullClassName;
            methodMetas = new ArrayList<>();
        }

        RouterMeta methodMeta(MethodMeta meta) {
            methodMetas.add(meta);
            return this;
        }

        @NonNull
        public String getFullClassName() {
            return fullClassName;
        }

        @NonNull
        public List<MethodMeta> getMethodMetas() {
            return methodMetas;
        }
    }

    public static class MethodMeta {
        private final String method;
        private List<Class<?>> paramsFullClasses;

        MethodMeta(String method, String... paramsFullClassNames) {
            this.method = method;
            /**
             * 把类全名转成对应的class名。
             * <p>
             * 例如 com.lb.legor.brouter.fragment.MyFragment1Param 转成 class com.lb.legor.brouter.fragment.MyFragment1Param
             * com.lb.legor.brouter.fragment.MyFragment1Param.InnerParam.Inner2Param 转成 class com.lb.legor.brouter.fragment.MyFragment1Param$InnerParam$Inner2Param
             */
            if (paramsFullClassNames != null && paramsFullClassNames.length > 0) {
                this.paramsFullClasses = new ArrayList<>();
                for (String name : paramsFullClassNames) {
                    // 对基本类型特殊处理，否则Class.forName时出现错误，如"java.lang.ClassNotFoundException: int"
                    Class<?> clazz = BRouterUtil.parseClassFromName(name);
                    if (clazz != null) {
                        paramsFullClasses.add(clazz);
                    }
                    // 检验全名称类名是否可成功匹配到class，包含多内部类的处理
                    else {
                        try {
                            String className = BRouterUtil.innerClassNameTransfer(name);
                            if (className != null) {
                                paramsFullClasses.add(Class.forName(className));
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (Error e) {
                            e.printStackTrace();
                        }
                    }
                }
                printFullClasses();
            }
        }

        @Nullable
        public String getMethod() {
            return method;
        }

        @Nullable
        public List<Class<?>> getParamsFullClasses() {
            return paramsFullClasses;
        }

        private void printFullClasses() {
            BRouterLog.d(TAG, "printFullClasses  size:" + paramsFullClasses.size());
            for (Class<?> clazz : paramsFullClasses) {
                BRouterLog.d(TAG, "    clazz:" + clazz);
            }
        }
    }
}
