package org.thewangzl.rpc.semi;

import java.lang.reflect.Method;

public class Ref {

    private String field;
    private String rpcClass;
    private Method method;
    private Method listMethod;
    private String[] args;
    private String refClass;
    private String refKey;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getRpcClass() {
        return rpcClass;
    }

    public void setRpcClass(String rpcClass) {
        this.rpcClass = rpcClass;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Method getListMethod() {
        return listMethod;
    }

    public void setListMethod(Method listMethod) {
        this.listMethod = listMethod;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String getRefClass() {
        return refClass;
    }

    public void setRefClass(String refClass) {
        this.refClass = refClass;
    }

    public String getRefKey() {
        return refKey;
    }

    public void setRefKey(String refKey) {
        this.refKey = refKey;
    }
}
