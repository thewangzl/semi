package org.thewangzl.rpc.semi.handle;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.thewangzl.rpc.semi.Ref;
import org.thewangzl.rpc.semi.SemiBeanRegistry;
import org.thewangzl.rpc.semi.util.CollectionUtil;
import org.thewangzl.rpc.semi.util.RpcUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 *
 */
public class SemiBean4CollectionProcessor {

    private SemiBeanRegistry semiBeanRegistry;

    private ApplicationContext context;

    private ConversionService conversionService;

    public SemiBean4CollectionProcessor(SemiBeanRegistry semiBeanRegistry, ApplicationContext context, ConversionService conversionService) {
        this.semiBeanRegistry = semiBeanRegistry;
        this.context = context;
        this.conversionService = conversionService;
    }

    /**
     *
     * @param domains
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public void process(Collection<?> domains) throws IllegalAccessException, InvocationTargetException {
        Set<Ref> refs = semiBeanRegistry.get(domains.iterator().next().getClass().getName());
        if(refs== null){
            return;
        }
        for (Ref ref : refs) {
            Collection ids = getRefIds(domains, ref);
            Object feign = context.getBean(ref.getRpcClass());
            Object[] args = RpcUtils.buildRpcMethodArgs(conversionService,ref.getArgs(), ref.getListMethod().getParameterTypes(), ref.getRefKeyIndex() , ids);
            Collection refValues = (Collection) ref.getListMethod().invoke(feign, args);
            this.setRef(domains,ref, refValues);
        }
    }

    private Collection getRefIds(Collection<?> domains, Ref ref) {
        Collection ids = CollectionUtil.instanceCollectionArgument(ref.getListMethod());
        for(Object domain : domains) {
            BeanWrapper beanWrapper = new BeanWrapperImpl(domain);
            Object key = beanWrapper.getPropertyValue(ref.getField());
            if (key instanceof Collection) {
                for (Object o : (Collection) key) {
                    BeanWrapper refWrapper = new BeanWrapperImpl(o);
                    Object v = refWrapper.getPropertyValue(ref.getRefKey());
                    ids.add(v);
                }
            } else {
                BeanWrapper refWrapper = new BeanWrapperImpl(key);
                Object refKeyVal = refWrapper.getPropertyValue(ref.getRefKey());
                ids.add(refKeyVal);
            }
        }
        return ids;
    }

    private void setRef(Collection domains, Ref ref,Collection refValues){
        Map<?,?> refMap = CollectionUtil.convert2Map(refValues, ref.getRefKey());
        String field = ref.getField();
        for(Object domain : domains) {
            BeanWrapper wrapper = new BeanWrapperImpl(domain);
            Object propertyValue = wrapper.getPropertyValue(ref.getField());
            if(propertyValue == null){
                continue;
            }
            if (propertyValue instanceof Collection) {
                Collection newValues = CollectionUtil.instance(propertyValue.getClass());
                for(Object obj : (Collection) propertyValue){
                    BeanWrapper propertyValueWrapper = new BeanWrapperImpl(obj);
                    Object v = refMap.get(propertyValueWrapper.getPropertyValue(ref.getRefKey()));
                    if(v != null){
                        newValues.add(v);
                    }
                }
                wrapper.setPropertyValue(field,newValues);
            }else if(propertyValue.getClass().getName().equals(ref.getRefClass())){
                BeanWrapper propertyValueWrapper = new BeanWrapperImpl(propertyValue);
                Object refId = propertyValueWrapper.getPropertyValue(ref.getRefKey());
                wrapper.setPropertyValue(field, refMap.get(refId));
            }
        }
    }

}
