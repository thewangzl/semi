package org.thewangzl.rpc.semi.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RefKey {

    /**
     * id index in rpc method
     * @return
     */
    int index() default 0;
}
