package com.mosaic.collections.concurrent;

import com.mosaic.lang.functional.VoidFunctionLong1;


/**
 *
 */
public interface ThreadPool {

    public void executeInParallel( long from, long toExc, VoidFunctionLong1 job );

}
