package com.mosaic.lang.functional;

import com.mosaic.lang.Failure;

/**
 *
 */
public interface CompletedCallbackNbl<T> {

    public void completedWithNullResult();

    public void completedWithResult( T result );

    public void completedWithFailure( Failure f );

}
