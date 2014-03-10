package com.mosaic.lang;

import java.io.IOException;


/**
 * Backs a service by a thread.  This class hooks into the StartStopMixin life
 * cycle and manages the creation and destruction of the thread.  Just implement
 * the loop() method, and be sure to exit from time to time to give this class
 * the chance to check for any possible shutdown requests.
 */
public abstract class ThreadedService<T extends StartStoppable<T>> extends StartStopMixin<T> {

    public static enum ThreadType {
        DAEMON, NON_DAEMON;

        public boolean isDaemon() {
            return this == DAEMON;
        }
    }


    private Thread     thread;
    private ThreadType threadType;


    public ThreadedService( String serviceName, ThreadType threadType ) {
        super( serviceName );

        QA.argNotNull( threadType, "threadType" );

        this.threadType = threadType;
    }


    /**
     * Perform the services work.  Will be called automatically from its
     * own thread when started, and will keep being called until the service
     * is stopped.  Exit this method from time to time to have it check to
     * see whether the service is to shutdown or not.  When close() is called
     * the running thread will be interrupted, so allow the exception to propagate
     * up.
     *
     * @return  The number of milliseconds to yield between invocations.
     */
    protected abstract long loop() throws InterruptedException;


    protected void doStart() throws IOException {
        this.thread = new Thread( getServiceName() ) {
            public void run() {
                while ( isRunning() ) {
                    try {
                        Thread.sleep( loop() );
                    } catch ( InterruptedException e ) {

                    }
                }
            }
        };

        this.thread.setDaemon( threadType.isDaemon() );

        this.thread.start();
    }

    protected void doStop() throws Exception {
        this.thread.interrupt();

        this.thread = null;
    }

}