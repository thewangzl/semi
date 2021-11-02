package org.thewangzl.rpc.semi.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SemiProperty {
    Class<?> rpcClass();
    String rpcMethod();
    String rpcListMethod();

    /**
     * other arguments when invoke rpc, begin with the second argument
     * @return
     */
    String[] args() default {};
}
