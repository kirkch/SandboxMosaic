package com.mosaic.lang.system;

import com.mosaic.collections.concurrent.Future;
import com.mosaic.collections.concurrent.FutureWrapper;
import com.mosaic.lang.Failure;
import com.mosaic.lang.reflect.ReflectionUtils;
import sun.misc.Signal;

import java.lang.reflect.Method;


/**
 *
 */
public class OSProcess extends FutureWrapper<Integer> {

    private final int pid;


    public OSProcess( int pid, Future<Integer> promise ) {
        super( promise );
        this.pid = pid;
    }

    public int getPid() {
        return pid;
    }

    /**
     * Request that the OS asks the process to shut itself down cleanly.  Identical to invoking
     * 'ctrl-c' on the terminal of the process.
     */
    public void abort() {
        completeWithFailure( new Failure("process was aborted") );
    }

    /**
     * Request that the OS kills the process immediately.  The process will not get a chance to run
     * shutdown hooks.  This is similar to 'kill -9 pid' under Unix.
     */
    public void killImmediately() {
        Class  unixProcess    = ReflectionUtils.findClass( "java.lang.UNIXProcess" ); // NB not a public API
        Method destroyProcess = ReflectionUtils.getPrivateMethod( unixProcess, "destroyProcess", Integer.TYPE, Boolean.TYPE );

        ReflectionUtils.invokeStaticMethod( destroyProcess, pid, true );
    }

}
