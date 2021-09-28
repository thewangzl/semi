package org.thewangzl.rpc.semi.handle;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

public class ProcessorFactory implements ApplicationContextAware {

    Map<String, Processor> map = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Processor> beanNamesForType = applicationContext.getBeansOfType(Processor.class);
    }

    Processor getProcessor(Object o){
        return null;
    }
}
