package com.mosaic.lang;

import com.mosaic.lang.functional.VoidFunction0;


/**
 * Interface for classes that need to be started/stopped.  Supports chaining services together.
 */
public interface Service<T extends Service<T>> {

    /**
     * Helper method for starting a service.  Occurred because of type mangling that
     * occurred when wanting another interface to implement the StartStoppable interface
     * AND a implementation of that interface to also extend StartStopMixin.
     *
     * Knickers + Twist -> Static fudge method.
     */
    public static <T> T startService( T s ) {
        if ( s instanceof Service ) {
            ((Service) s).start();
        }

        return s;
    }

    public static <T> T stopService( T s ) {
        if ( s instanceof Service ) {
            ((Service) s).stop();
        }

        return s;
    }





    /**
     * Tell this object to allocate any new resources that it needs.  It may be
     * called multiple times, so only do the work if it is not already ready.
     *
     * @throws Throwable in the event of any exception then the service will be in an an
     *         unknown state with regard to any other services that it depends upon.  The
     *         typical behaviour will be to terminate the application and rely on the
     *         application being restarted later.
     */
    public T start();

    /**
     * Tell this object to free its resources.  It must be called exactly
     * the same number of times as start() before it will free resources.
     *
     * @throws Throwable in the event of an exception then the service did not shut down cleanly.
     *                   Do not attempt to restart it.  The shutdown will have been propagated to
     *                   all dependencies, but the state of them will be unclear.
     */
    public T stop();

    /**
     * Is this service running?  Turns true after start() has been called for
     * the first time, and false after stop() has been called for the
     * last time.
     */
    public boolean isRunning();



    /**
     * Before this service is started, ensure that otherService is started first.  This models
     * the situation where this object depends on another service, and it will fail if that
     * service is not up and running before this one.
     *
     * @return a token that can be used to check whether 'otherService' is still subscribed to
     *     this service and to unregister the service if required.  Unregistering the service
     *     will not alter the state of either service.
     */
    public Subscription registerServicesBefore( Service... otherServices );

    /**
     * After this service has been started, then also start the specified service.  This is
     * a convenience method for services that we want to start at the same time as this service
     * but this service does not require that service to be up and running before this one is
     * running.
     *
     * @return a token that can be used to check whether 'otherService' is still subscribed to
     *     this service and to unregister the service if required.  Unregistering the service
     *     will not alter the state of either service.
     */
    public Subscription registerServicesAfter( Service... otherServices );


    /**
     * The supplied callback will be invoked every time this service is started.  When the callback
     * is called, this service will not yet have started.<p/>
     *
     * Callbacks are not expected to throw any exception.  If they do, then the call to start() will
     * propagate the exception up the stack and leave the service(s) in an undefined state.
     */
    public Service<T> onStartBefore( VoidFunction0 callback );

    /**
     * The supplied callback will be invoked every time this service is started.  When the callback
     * is called, this service will already be up and running.<p/>
     *
     * Callbacks are not expected to throw any exception.  If they do, then the call to start() will
     * propagate the exception up the stack and leave the service(s) in an undefined state.
     */
    public Service<T> onStartAfter( VoidFunction0 callback );

    /**
     * The supplied callback will be invoked every time this service is stopped.  When the callback
     * is called, this service will still be running.<p/>
     *
     * Callbacks are not expected to throw any exception.  If they do, then the call to start() will
     * propagate the exception up the stack and leave the service(s) in an undefined state.
     */
    public Service<T> onStopBefore( VoidFunction0 callback );

    /**
     * The supplied callback will be invoked every time this service is stopped.  When the callback
     * is called, this service will have already been stopped.<p/>
     *
     * Callbacks are not expected to throw any exception.  If they do, then the call to start() will
     * propagate the exception up the stack and leave the service(s) in an undefined state.
     */
    public Service<T> onStopAfter( VoidFunction0 callback );

}
