package org.thewangzl.rpc.semi.handle;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.thewangzl.rpc.semi.SemiConfiguration;
import org.thewangzl.rpc.semi.type.TypeHandler;

import java.util.Collection;

@Aspect
public class SemiBeanHandlerAspect {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private SemiConfiguration configuration;

    @Pointcut("@annotation(org.thewangzl.rpc.semi.annotation.SemiBeanHandler)")
    public void pointcut(){}

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        TypeHandler typeHandler = configuration.getTypeHandlerRegistry().getHandler(result);
        Object realData = typeHandler.unwrap(result);
        if(realData instanceof Collection){
            new SemiBean4CollectionProcessor(configuration.getSemiBeanRegistry(), context, conversionService).process((Collection) realData);
        }else{
            new SemiBeanProcessor(configuration.getSemiBeanRegistry(), context, conversionService).process(realData);
        }
        typeHandler.rewrap(result, realData);
        return result;
    }

}
