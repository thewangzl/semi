package org.thewangzl.rpc.semi.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(ExtArgs.class)
public @interface ExtArg {

    int index() default 1;

    String value();

}
