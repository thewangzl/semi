package org.thewangzl.rpc.semi.util;

import java.lang.reflect.*;

public class ReflectUtil {

    public static Type getActualType(Type type) {
        if (type instanceof Class) {
            return type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType t = (ParameterizedType) type;
            Type[] actualTypeArguments = t.getActualTypeArguments();
            return actualTypeArguments[0];
        }
        return null;
    }
}
