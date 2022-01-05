package com.lb.brouter.api.internal;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

/**
 * 路由表接口
 *
 * <p>
 * 不同Module必须设置不同的全路径的路由表名称，在build.gradle里annotationProcessorOptions的用BRouterIndex参数设置名称。
 *
 * Created by lb on 2022/01/01
 */
@Keep
public interface BRouterIndex {
    @Keep
    @Nullable
    BRouterInfo getRouterInfo();
}
