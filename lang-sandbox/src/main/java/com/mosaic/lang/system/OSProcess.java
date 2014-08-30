package com.mosaic.lang.system;

import com.mosaic.collections.concurrent.Future;
import com.mosaic.collections.concurrent.FutureWrapper;


/**
 *
 */
public class OSProcess extends FutureWrapper<Integer> {
    private final int             pid;

    public OSProcess( int pid, Future<Integer> promise ) {
        super( promise );
        this.pid = pid;
    }

    public int getPid() {
        return pid;
    }
}
