package org.thewangzl.rpc.semi.type;

/**
 *
 */
public class NormalTypeHandler implements TypeHandler {

    @Override
    public Object getData(Object wrapper) {
        return wrapper;
    }

}
