package org.thewangzl.rpc.semi.type;

import java.util.HashMap;
import java.util.Map;

/**
 * @author thewangzl
 * @date 2021-10-28 12:24
 * @version 1.0
 * @description
 */
public class TypeHandlerRegistry {

    private static final TypeHandler normalTypeHandler = new NormalTypeHandler();

    private final Map<Class<?>, TypeHandler<?,?>> typeHandlerMap = new HashMap<>();

    public void register(Class<?> clazz, TypeHandler<?,?> handler){
        typeHandlerMap.put(clazz, handler);
    }

    public void register(Class<?> typeHandlerClass){
        WrapperType wrapperType = typeHandlerClass.getAnnotation(WrapperType.class);
        if(wrapperType != null){
            register(wrapperType.value(),instanceHandler(typeHandlerClass));
        }
    }

    private TypeHandler<?,?> instanceHandler(Class<?> clazz){
        try {
            return (TypeHandler<?,?>) clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("instance typeHandler "+clazz.getName()+" error:"+e.getMessage());
        }
    }

    public TypeHandler getHandler(Object obj){
        Class<?> clazz = obj.getClass();
        if(typeHandlerMap.containsKey(clazz)){
            return typeHandlerMap.get(clazz);
        }
        return normalTypeHandler;
    }
}
