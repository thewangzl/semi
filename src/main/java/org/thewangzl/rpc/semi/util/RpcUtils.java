package org.thewangzl.rpc.semi.util;

import org.springframework.util.StringUtils;
import org.thewangzl.rpc.semi.Ref;

import java.lang.reflect.Method;
import java.util.*;

public class RpcUtils {

    public static Object[] buildRpcMethodArgs(Ref ref, Object firstValue) {
        Object[] args;
        if(ref.getArgs().length >= 1 && StringUtils.hasText(ref.getArgs()[0])){
            args = new Object[ref.getArgs().length + 1];
            System.arraycopy(ref.getArgs(),0,args,1,ref.getArgs().length);
        }else{
            args = new Object[1];
        }
        args[0] = firstValue;
        return args;
    }
    
    public static Collection<?> instanceCollectionArgument(Method method){
        Class<?>[] types = method.getParameterTypes();
        if(types.length == 0){
            return new HashSet();
        }
        return CollectionUtil.instance(types[0]);
    }
    
}
