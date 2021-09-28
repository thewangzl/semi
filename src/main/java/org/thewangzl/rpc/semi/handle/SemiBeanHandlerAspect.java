package org.thewangzl.rpc.semi.handle;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.thewangzl.rpc.semi.SemiBeanRegistry;

import java.util.Collection;


@Aspect
@Component
public class SemiBeanHandlerAspect {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private SemiBeanRegistry semiBeanRegistry;

    @Pointcut("@annotation(org.thewangzl.rpc.semi.annotation.SemiBeanHandler)")
    public void pointcut(){}

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        if(result instanceof Collection){
            new SemiBean4CollectionProcessor(semiBeanRegistry, context).process((Collection) result);
        }else{
            new SemiBeanProcessor(semiBeanRegistry, context).process(result);
        }
        return result;
    }

}
