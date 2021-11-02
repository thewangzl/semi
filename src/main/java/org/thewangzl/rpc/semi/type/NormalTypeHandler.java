package org.thewangzl.rpc.semi.type;

/**
 *
 */
public class NormalTypeHandler implements TypeHandler {

    @Override
    public Object unwrap(Object wrapper) {
        return wrapper;
    }

    @Override
    public void rewrap(Object wrapper, Object fullBean) {

    }

}
