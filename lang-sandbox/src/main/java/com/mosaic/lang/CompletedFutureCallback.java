package com.mosaic.lang;

/**
 *
 */
public interface CompletedFutureCallback<T> {

    public void completedWithResult( T result );

    public void completedWithFailure( Failure f );

}
