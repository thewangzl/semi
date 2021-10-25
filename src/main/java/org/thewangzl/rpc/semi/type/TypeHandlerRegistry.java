package org.thewangzl.rpc.semi.type;

import java.util.HashMap;
import java.util.Map;

public class TypeHandlerRegistry {

    private static final TypeHandler normalTypeHandler = new NormalTypeHandler();

    private final Map<Class<?>, TypeHandler<?>> typeHandlerMap = new HashMap<>();

    public void register(Class<?> clazz, TypeHandler<?> handler){
        typeHandlerMap.put(clazz, handler);
    }


    public void register(Class<?> typeHandlerClass){
        WrappedType wrappedType = typeHandlerClass.getAnnotation(WrappedType.class);
        if(wrappedType != null){
            register(wrappedType.value(),getHandler(typeHandlerClass));
        }
    }

    private TypeHandler<?> getHandler(Class<?> clazz){
        try {
            return (TypeHandler<?>) clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("instance typeHandler error:"+e.getMessage());
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
