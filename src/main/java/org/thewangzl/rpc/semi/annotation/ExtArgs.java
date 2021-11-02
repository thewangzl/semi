package org.thewangzl.rpc.semi.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtArgs {
    ExtArg[] value() default {};

}
