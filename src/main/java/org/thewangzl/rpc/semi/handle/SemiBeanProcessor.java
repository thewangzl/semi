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

public class SemiBeanProcessor {
    private SemiBeanRegistry semiBeanRegistry;

    private ApplicationContext context;

    public SemiBeanProcessor(SemiBeanRegistry semiBeanRegistry, ApplicationContext context) {
        this.semiBeanRegistry = semiBeanRegistry;
        this.context = context;
    }

    public void process(Object data) throws IllegalAccessException, InvocationTargetException {
        Set<Ref> refs = semiBeanRegistry.get(data.getClass().getName());
        if(refs== null){
            return;
        }
        BeanWrapper beanWrapper = new BeanWrapperImpl(data);
        for(Ref ref: refs){
            setRef(beanWrapper,ref);
        }
    }

    private void setRef(BeanWrapper beanWrapper,Ref ref) throws IllegalAccessException, InvocationTargetException {
        Object feign = context.getBean(ref.getRpcClass());
        Object value = beanWrapper.getPropertyValue(ref.getField());
        if(value instanceof Collection){
            Collection refKeyValues = getRefIds(ref, (Collection) value);
            Object[] args = RpcUtils.buildRpcMethodArgs(ref, refKeyValues);
            Object refVal = ref.getListMethod().invoke(feign, args);
            beanWrapper.setPropertyValue(ref.getField(), refVal);
        }else{
            BeanWrapper keyWrapper = new BeanWrapperImpl(value);
            Object refId = keyWrapper.getPropertyValue(ref.getRefKey());
            Object[] args = RpcUtils.buildRpcMethodArgs(ref, refId);
            Object refVal = ref.getMethod().invoke(feign, args);
            beanWrapper.setPropertyValue(ref.getField(), refVal);
        }
    }

    private Collection getRefIds(Ref ref, Collection value) {
        Collection refIds = RpcUtils.instanceCollectionArgument(ref.getListMethod());
        for(Object o : value){
            BeanWrapper refWrapper = new BeanWrapperImpl(o);
            Object v = refWrapper.getPropertyValue(ref.getRefKey());
            if(v != null) {
                refIds.add(v);
            }
        }
        return refIds;
    }


}
