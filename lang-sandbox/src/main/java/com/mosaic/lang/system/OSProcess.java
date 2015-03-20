package com.mosaic.lang.system;

import com.mosaic.collections.concurrent.Future;
import com.mosaic.collections.concurrent.FutureWrapper;
import com.mosaic.lang.Failure;
import com.mosaic.lang.functional.CompletedCallback;
import com.mosaic.lang.reflect.ReflectionUtils;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;


/**
 *
 */
public class OSProcess extends FutureWrapper<Integer> {

    private final int                          pid;
    private final AtomicReference<PrintWriter> writer = new AtomicReference<>();


    public OSProcess( int pid, Future<Integer> promise, PrintWriter writer ) {
        super( promise );

        this.pid    = pid;
        this.writer.set(writer);

        onComplete( new CompletedCallback<Integer>() {
            public void completedWithResult( Integer processExitStatus ) {
                OSProcess.this.writer.set( null );
            }

            public void completedWithFailure( Failure f ) {
                OSProcess.this.writer.set(null);
            }
        });
    }

    public int getPid() {
        return pid;
    }

    /**
     * Any text written to this writer will find its way into the input of the child process.
     *
     * @return could be null
     */
    public PrintWriter getPipedWriter() {
        return writer.get();
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
