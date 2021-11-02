package org.thewangzl.rpc.semi.util;

import org.springframework.core.convert.ConversionService;
import org.thewangzl.rpc.semi.ArgMapping;

import java.util.*;

public class RpcUtils {

    public static Object[] buildRpcMethodArgs(ConversionService conversionService, List<ArgMapping> argMappings, Class<?>[] parameterTypes, int keyIndex, Object refKey) {
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
