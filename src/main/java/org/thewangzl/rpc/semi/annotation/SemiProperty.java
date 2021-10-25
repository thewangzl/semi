package org.thewangzl.rpc.semi.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SemiProperty {
    String rpcClass();
    String rpcMethod();
    String rpcListMethod();

    /**
     * RPC调用时需要的其他参数，从第二个参数开始
     * @return
     */
    String[] args() default "";
}
