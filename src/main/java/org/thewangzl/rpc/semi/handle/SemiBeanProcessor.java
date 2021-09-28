package org.thewangzl.rpc.semi.handle;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.ApplicationContext;
import org.thewangzl.rpc.semi.Ref;
import org.thewangzl.rpc.semi.SemiBeanRegistry;
import org.thewangzl.rpc.semi.util.RpcUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Set;

public class SemiBeanProcessor<T> {
    private SemiBeanRegistry semiBeanRegistry;

    private ApplicationContext context;

    private BeanWrapper beanWrapper;

    private T data;

    public SemiBeanProcessor(SemiBeanRegistry semiBeanRegistry, ApplicationContext context) {
        this.semiBeanRegistry = semiBeanRegistry;
        this.context = context;
    }

    public void process(T data) throws IllegalAccessException, InvocationTargetException {
        Set<Ref> refs = semiBeanRegistry.get(data.getClass());
        this.beanWrapper = new BeanWrapperImpl(data);
        this.data = data;
        for(Ref ref: refs){
            setRef(ref);
        }
    }

    private void setRef(Ref ref) throws IllegalAccessException, InvocationTargetException {
        Object feign = context.getBean(ref.getRpcClass());
        Object value = beanWrapper.getPropertyValue(ref.getField());
        if(value instanceof Collection){
            Collection refKeyValues = RpcUtils.instanceCollectionArgument(ref.getListMethod());
            for(Object o : (Collection) value){
                BeanWrapper refWrapper = new BeanWrapperImpl(o);
                Object v = refWrapper.getPropertyValue(ref.getRefKey());
                if(v != null) {
                    refKeyValues.add(v);
                }
            }
            Object[] args = RpcUtils.buildRpcMethodArgs(ref, refKeyValues);
            Object refVal = ref.getListMethod().invoke(feign, args);
            beanWrapper.setPropertyValue(ref.getField(), refVal);
        }else{
            BeanWrapper keyWrapper = new BeanWrapperImpl(value);
            Object refKeyVal = keyWrapper.getPropertyValue(ref.getRefKey());
            Object[] args = RpcUtils.buildRpcMethodArgs(ref, refKeyVal);
            Object refVal = ref.getMethod().invoke(feign, args);
            beanWrapper.setPropertyValue(ref.getField(), refVal);
        }
    }


}
