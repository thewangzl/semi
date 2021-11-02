package org.thewangzl.rpc.semi.handle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.thewangzl.rpc.semi.ArgMapping;
import org.thewangzl.rpc.semi.Ref;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

/**
 * @author thewangzl
 * @version 1.0
 * @date 2021-11-01 11:56
 * @description
 */
@Component
public class RpcService {

    @Autowired
    private ConversionService conversionService;

    public Object invoke(Ref ref, Object feign, Object id) throws InvocationTargetException, IllegalAccessException {
        Object[] args = buildArgs(ref.getArgs(),ref.getMethod().getParameterTypes(), ref.getRefKeyIndex(), id);
        return ref.getMethod().invoke(feign, args);
    }

    public <K,E> Collection<E> invokeList(Ref ref, Object feign, Collection<K> ids) throws InvocationTargetException, IllegalAccessException {
        Object[] args = buildArgs(ref.getArgs(), ref.getListMethod().getParameterTypes(), ref.getRefKeyIndex(), ids);
        return (Collection)ref.getListMethod().invoke(feign, args);
    }

    private Object[] buildArgs(List<ArgMapping> argMappings,Class<?>[] parameterTypes, int keyIndex,Object refKey) {
        Object[] result;
        if(argMappings.size() >= 1){
            result = new Object[argMappings.size() + 1];
            for (ArgMapping argMapping : argMappings){
                result[argMapping.getIndex()] = conversionService.convert(argMapping.getValue(),parameterTypes[argMapping.getIndex()]);
            }
            result[keyIndex] = refKey;
        }else{
            result = new Object[1];
            result[0] = refKey;
        }
        return result;
    }
}
