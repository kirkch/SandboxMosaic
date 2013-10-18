package com.mosaic.lang.functional;

import com.mosaic.lang.Failure;

/**
 *
 */
public interface CompletedCallback<T> {

    public void completedWithResult( T result );

    public void completedWithFailure( Failure f );

}
