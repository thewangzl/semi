package org.thewangzl.rpc.semi;

import org.thewangzl.rpc.semi.type.TypeHandlerRegistry;

public class SemiConfiguration {

    private SemiBeanRegistry semiBeanRegistry = new SemiBeanRegistry();

    private TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();

    public SemiBeanRegistry getSemiBeanRegistry() {
        return semiBeanRegistry;
    }

    public TypeHandlerRegistry getTypeHandlerRegistry() {
        return typeHandlerRegistry;
    }

}
