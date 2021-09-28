package org.thewangzl.rpc.semi.handle;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.ApplicationContext;
import org.thewangzl.rpc.semi.Ref;
import org.thewangzl.rpc.semi.SemiBeanRegistry;
import org.thewangzl.rpc.semi.util.CollectionUtil;
import org.thewangzl.rpc.semi.util.RpcUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class SemiBean4CollectionProcessor {
    private SemiBeanRegistry semiBeanRegistry;

    private ApplicationContext context;

    public SemiBean4CollectionProcessor(SemiBeanRegistry semiBeanRegistry, ApplicationContext context) {
        this.semiBeanRegistry = semiBeanRegistry;
        this.context = context;
    }

    public void process(Collection list) throws IllegalAccessException, InvocationTargetException {
        Map<Ref,Collection> ids = new HashMap<>();
        for(Object data : list) {
            Set<Ref> refs = semiBeanRegistry.get(data.getClass());
            BeanWrapper beanWrapper = new BeanWrapperImpl(data);
            for (Ref ref : refs) {
                Object key = beanWrapper.getPropertyValue(ref.getField());
                if (key instanceof Collection) {
                    Collection refKeyValues =RpcUtils.instanceCollectionArgument(ref.getListMethod());
                    for (Object o : (Collection) key) {
                        BeanWrapper keyWrapper = new BeanWrapperImpl(o);
                        Object v = keyWrapper.getPropertyValue(ref.getRefKey());
                        refKeyValues.add(v);
                    }
                    ids.putIfAbsent(ref,new HashSet());
                    ids.get(ref).addAll(refKeyValues);
                } else {
                    BeanWrapper keyWrapper = new BeanWrapperImpl(key);
                    Object refKeyVal = keyWrapper.getPropertyValue(ref.getRefKey());
                    ids.putIfAbsent(ref,RpcUtils.instanceCollectionArgument(ref.getListMethod()));
                    ids.get(ref).add(refKeyVal);
                }
            }
        }
        for(Map.Entry<Ref,Collection> entry: ids.entrySet()){
            Object feign = context.getBean(entry.getKey().getRpcClass());
            Object[] args = RpcUtils.buildRpcMethodArgs(entry.getKey(), entry.getValue());
            Collection values = (Collection) entry.getKey().getListMethod().invoke(feign, args);
            Map resultMap = CollectionUtil.convert2Map(values, entry.getKey().getRefKey());
            String field = entry.getKey().getField();
            for(Object data : list) {
                BeanWrapper wrapper = new BeanWrapperImpl(data);
                Object propertyValue = wrapper.getPropertyValue(entry.getKey().getField());
                if (propertyValue instanceof Collection) {
                    Collection newValues = CollectionUtil.instance(propertyValue.getClass());
                    for(Object obj : (Collection) propertyValue){
                        BeanWrapper propertyValueWrapper = new BeanWrapperImpl(obj);
                        Object v = resultMap.get(propertyValueWrapper.getPropertyValue(entry.getKey().getRefKey()));
                        if(v != null) {
                            newValues.add(v);
                        }
                    }
                    wrapper.setPropertyValue(field,newValues);
                }else if(propertyValue.getClass().getName().equals(entry.getKey().getRefClass())){
                    BeanWrapper propertyValueWrapper = new BeanWrapperImpl(propertyValue);
                    wrapper.setPropertyValue(field, resultMap.get(propertyValueWrapper.getPropertyValue(entry.getKey().getRefKey())));
                }
            }
        }
    }

}
