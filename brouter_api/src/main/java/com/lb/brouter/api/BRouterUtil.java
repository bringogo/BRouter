package com.lb.brouter.api;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * util
 * <p>
 * Created by lb on 2022/01/01
 */
public class BRouterUtil {
    @Nullable
    public static Class<?> parseClassFromName(@NonNull String className) {
        if ("boolean".equals(className)) {
            return boolean.class;
        } else if ("boolean[]".equals(className)) {
            return boolean[].class;
        } else if ("java.lang.Boolean[]".equals(className)) {
            return Boolean[].class;
        } else if ("byte".equals(className)) {
            return byte.class;
        } else if ("byte[]".equals(className)) {
            return byte[].class;
        } else if ("java.lang.Byte[]".equals(className)) {
            return Byte[].class;
        } else if ("short".equals(className)) {
            return short.class;
        } else if ("short[]".equals(className)) {
            return short[].class;
        } else if ("java.lang.Short[]".equals(className)) {
            return Short[].class;
        } else if ("int".equals(className)) {
            return int.class;
        } else if ("int[]".equals(className)) {
            return int[].class;
        } else if ("java.lang.Integer[]".equals(className)) {
            return Integer[].class;
        } else if ("long".equals(className)) {
            return long.class;
        } else if ("long[]".equals(className)) {
            return long[].class;
        } else if ("java.lang.Long[]".equals(className)) {
            return Long[].class;
        } else if ("float".equals(className)) {
            return float.class;
        } else if ("float[]".equals(className)) {
            return float[].class;
        } else if ("java.lang.Float[]".equals(className)) {
            return Float[].class;
        } else if ("double".equals(className)) {
            return double.class;
        } else if ("double[]".equals(className)) {
            return double[].class;
        } else if ("java.lang.Double[]".equals(className)) {
            return Double[].class;
        } else if ("char".equals(className)) {
            return char.class;
        } else if ("char[]".equals(className)) {
            return char[].class;
        } else if ("java.lang.Character[]".equals(className)) {
            return Character[].class;
        }
        // string数组的特殊处理
        else if ("java.lang.String[]".equals(className)) {
            return String[].class;
        }
        // list列表的统一处理
        else if (className.contains("java.util.ArrayList")) {
            return ArrayList.class;
        } else if (className.contains("java.util.LinkedList")) {
            return LinkedList.class;
        } else if (className.contains("java.util.List")) {
            return List.class;
        }
        return null;
    }

    /**
     * 把潜在的内部类名从'.'表示法转成'$'表示法，不是内部类就不用转换
     */
    @Nullable
    public static String innerClassNameTransfer(String potentialInnerClass) {
        if (potentialInnerClass == null || potentialInnerClass.length() == 0) {
            return potentialInnerClass;
        }
        if (potentialInnerClass.indexOf('.') == -1) {
            return potentialInnerClass;
        }
        try {
            Class.forName(potentialInnerClass);
        } catch (ClassNotFoundException e) {
            int dot = potentialInnerClass.lastIndexOf('.');
            if (dot > 0 && dot < potentialInnerClass.length() - 1) {
                potentialInnerClass =
                        potentialInnerClass.substring(0, dot) + "$" + potentialInnerClass
                                .substring(dot + 1);
                return innerClassNameTransfer(potentialInnerClass);
            }
        }
        return potentialInnerClass;
    }

    public static boolean isCompatibleType(@NonNull Class<?> clazz, @NonNull Object obj) {
        /**
         * 如果clazz是基本类型，就认为与对应的包装类型兼容
         *
         * 兼容判断：例如，如果obj无论boolean还是Boolean，getClass()都是Boolean.class
         */
        if (clazz == boolean.class && obj.getClass() == Boolean.class) {
            return true;
        }
        if (clazz == byte.class && obj.getClass() == Byte.class) {
            return true;
        }
        if (clazz == short.class && obj.getClass() == Short.class) {
            return true;
        }
        if (clazz == int.class && obj.getClass() == Integer.class) {
            return true;
        }
        if (clazz == long.class && obj.getClass() == Long.class) {
            return true;
        }
        if (clazz == float.class && obj.getClass() == Float.class) {
            return true;
        }
        if (clazz == double.class && obj.getClass() == Double.class) {
            return true;
        }
        if (clazz == char.class && obj.getClass() == Character.class) {
            return true;
        }
        /**
         * 如果clazz不是基本类型
         */
        return clazz.isInstance(obj);
    }

    public static Object invoke(Method method, Class<?> targetClazz, Object... params)
            throws BRouterException {
        int routeParamsLen = params == null ? 0 : params.length;
        try {
            Object result;
            if (routeParamsLen == 1) {
                result = method.invoke(targetClazz, params[0]);
            } else if (routeParamsLen == 2) {
                result = method.invoke(targetClazz, params[0], params[1]);
            } else if (routeParamsLen == 3) {
                result = method.invoke(targetClazz, params[0], params[1], params[2]);
            } else if (routeParamsLen == 4) {
                result = method.invoke(targetClazz, params[0], params[1], params[2], params[3]);
            } else if (routeParamsLen == 5) {
                result = method
                        .invoke(targetClazz, params[0], params[1], params[2], params[3], params[4]);
            } else if (routeParamsLen == 6) {
                result = method
                        .invoke(targetClazz, params[0], params[1], params[2], params[3], params[4],
                                params[5]);
            } else if (routeParamsLen == 7) {
                result = method
                        .invoke(targetClazz, params[0], params[1], params[2], params[3], params[4],
                                params[5], params[6]);
            } else if (routeParamsLen == 8) {
                result = method
                        .invoke(targetClazz, params[0], params[1], params[2], params[3], params[4],
                                params[5], params[6], params[7]);
            } else if (routeParamsLen == 9) {
                result = method
                        .invoke(targetClazz, params[0], params[1], params[2], params[3], params[4],
                                params[5], params[6], params[7], params[8]);
            } else if (routeParamsLen == 10) {
                result = method
                        .invoke(targetClazz, params[0], params[1], params[2], params[3], params[4],
                                params[5], params[6], params[7], params[8], params[9]);
            } else if (routeParamsLen == 11) {
                result = method
                        .invoke(targetClazz, params[0], params[1], params[2], params[3], params[4],
                                params[5], params[6], params[7], params[8], params[9], params[10]);
            } else if (routeParamsLen == 12) {
                result = method
                        .invoke(targetClazz, params[0], params[1], params[2], params[3], params[4],
                                params[5], params[6], params[7], params[8], params[9], params[10],
                                params[11]);
            } else if (routeParamsLen == 13) {
                result = method
                        .invoke(targetClazz, params[0], params[1], params[2], params[3], params[4],
                                params[5], params[6], params[7], params[8], params[9], params[10],
                                params[11], params[12]);
            } else if (routeParamsLen == 14) {
                result = method
                        .invoke(targetClazz, params[0], params[1], params[2], params[3], params[4],
                                params[5], params[6], params[7], params[8], params[9], params[10],
                                params[11], params[12], params[13]);
            } else if (routeParamsLen == 15) {
                result = method
                        .invoke(targetClazz, params[0], params[1], params[2], params[3], params[4],
                                params[5], params[6], params[7], params[8], params[9], params[10],
                                params[11], params[12], params[13], params[14]);
            } else if (routeParamsLen == 16) {
                result = method
                        .invoke(targetClazz, params[0], params[1], params[2], params[3], params[4],
                                params[5], params[6], params[7], params[8], params[9], params[10],
                                params[11], params[12], params[13], params[14], params[15]);
            } else if (routeParamsLen == 17) {
                result = method
                        .invoke(targetClazz, params[0], params[1], params[2], params[3], params[4],
                                params[5], params[6], params[7], params[8], params[9], params[10],
                                params[11], params[12], params[13], params[14], params[15], params[16]);
            } else if (routeParamsLen == 18) {
                result = method
                        .invoke(targetClazz, params[0], params[1], params[2], params[3], params[4],
                                params[5], params[6], params[7], params[8], params[9], params[10],
                                params[11], params[12], params[13], params[14], params[15], params[16],
                                params[17]);
            } else if (routeParamsLen == 19) {
                result = method
                        .invoke(targetClazz, params[0], params[1], params[2], params[3], params[4],
                                params[5], params[6], params[7], params[8], params[9], params[10],
                                params[11], params[12], params[13], params[14], params[15], params[16],
                                params[17], params[18]);
            } else if (routeParamsLen == 20) {
                result = method
                        .invoke(targetClazz, params[0], params[1], params[2], params[3], params[4],
                                params[5], params[6], params[7], params[8], params[9], params[10],
                                params[11], params[12], params[13], params[14], params[15], params[16],
                                params[17], params[18], params[19]);
            }
            // 兜底
            else {
                result = method.invoke(targetClazz);
            }
            return result;
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            e.printStackTrace();
            throw new BRouterException(BRouterException.ExceptionType.SYSTEM, " #invoke  Err in method invoke", e);
        }
    }
}