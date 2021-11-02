package org.thewangzl.rpc.semi.type;

/**
 *
 * @param <S>
 * @param <T>
 */
public interface TypeHandler<S,T> {
     T unwrap(S wrapper);

     void rewrap(S wrapper, T fullBean);
}
