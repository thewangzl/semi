package org.thewangzl.rpc.semi.type;

import java.lang.annotation.*;

/**
 *
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WrapperType {
    Class<?> value();
}
