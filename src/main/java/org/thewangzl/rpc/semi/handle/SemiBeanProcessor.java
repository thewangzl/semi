package org.thewangzl.rpc.semi.handle;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.thewangzl.rpc.semi.Ref;
import org.thewangzl.rpc.semi.SemiBeanRegistry;
import org.thewangzl.rpc.semi.util.CollectionUtil;
import org.thewangzl.rpc.semi.util.RpcUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Set;

/**
 *
 */
public class SemiBeanProcessor {

    private SemiBeanRegistry semiBeanRegistry;

    private ApplicationContext context;

    private ConversionService conversionService;

    public SemiBeanProcessor(SemiBeanRegistry semiBeanRegistry, ApplicationContext context, ConversionService conversionService) {
        this.semiBeanRegistry = semiBeanRegistry;
        this.context = context;
        this.conversionService = conversionService;
    }
    /**
     *
     * @param data
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
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
        Object value = beanWrapper.getPropertyValue(ref.getField());
        if(value == null){
            return;
        }
        Object refVal = getRpcValue(ref, value);
        beanWrapper.setPropertyValue(ref.getField(), refVal);
    }

    private Object getRpcValue(Ref ref, Object value) throws IllegalAccessException, InvocationTargetException {
        Object refVal;
        Object feign = context.getBean(ref.getRpcClass());
        if(value instanceof Collection){
            Collection refKeyValues = getRefIds(ref, (Collection) value);
            Object[] args = RpcUtils.buildRpcMethodArgs(conversionService, ref.getArgs(), ref.getListMethod().getParameterTypes(), ref.getRefKeyIndex(), refKeyValues);
            refVal = ref.getListMethod().invoke(feign, args);
        }else{
            BeanWrapper keyWrapper = new BeanWrapperImpl(value);
            Object refId = keyWrapper.getPropertyValue(ref.getRefKey());
            Object[] args = RpcUtils.buildRpcMethodArgs(conversionService, ref.getArgs(), ref.getMethod().getParameterTypes(), ref.getRefKeyIndex(), refId);
            refVal = ref.getMethod().invoke(feign, args);
        }
        return refVal;
    }

    private Collection getRefIds(Ref ref, Collection value) {
        Collection refIds = CollectionUtil.instanceCollectionArgument(ref.getListMethod());
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
