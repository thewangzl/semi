package org.thewangzl.rpc.semi.util;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Method;
import java.util.*;

/**
 *
 */
public class CollectionUtil {

    public static Map convert2Map(Collection<?> collection,String keyProperty){
        Map resultMap = new HashMap();
        if(collection != null) {
            BeanWrapper wrapper;
            Object key;
            for (Object value : collection) {
                wrapper = new BeanWrapperImpl(value);
                key = wrapper.getPropertyValue(keyProperty);
                resultMap.put(key, value);
            }
        }
        return resultMap;
    }

    public static Collection instance(Class clazz){
        if(clazz.equals(List.class)){
            return new ArrayList<>();
        }else if (clazz.equals(Set.class)){
            return new HashSet();
        }else {
            try {
                return (Collection)clazz.newInstance();
            } catch (InstantiationException e) {

            } catch (IllegalAccessException e) {
            }
            return null;
        }
    }

    public static Collection<?> instanceCollectionArgument(Method method){
        Class<?>[] types = method.getParameterTypes();
        if(types.length == 0){
            return new HashSet();
        }
        return CollectionUtil.instance(types[0]);
    }
}
