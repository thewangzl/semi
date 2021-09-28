package org.thewangzl.rpc.semi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface RefBean {
    String feign();
    String method();
}
