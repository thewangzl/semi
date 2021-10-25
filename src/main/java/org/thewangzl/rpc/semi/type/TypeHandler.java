package org.thewangzl.rpc.semi.type;

/**
 *
 * @param <S>
 */
public interface TypeHandler<S> {
     Object getData(S wrapper);

      default void setData(S wrapper, Object fullBean){

      };
}
