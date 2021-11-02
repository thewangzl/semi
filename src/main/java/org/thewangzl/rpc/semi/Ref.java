package org.thewangzl.rpc.semi;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Ref {

    private String field;
    private Class<?> rpcClass;
    private Method method;
    private Method listMethod;
    private List<ArgMapping> args = new ArrayList<>();
    private String refClass;
    private String refKey;
    private int refKeyIndex;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Class<?> getRpcClass() {
        return rpcClass;
    }

    public void setRpcClass(Class<?> rpcClass) {
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

    public List<ArgMapping> getArgs() {
        return args;
    }

    public void setArgs(List<ArgMapping> args) {
        this.args = args;
    }

    public void addArg(ArgMapping arg){
        this.args.add(arg);
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

    public int getRefKeyIndex() {
        return refKeyIndex;
    }
    public void setRefKeyIndex(int refKeyIndex) {
        this.refKeyIndex = refKeyIndex;
    }
}
