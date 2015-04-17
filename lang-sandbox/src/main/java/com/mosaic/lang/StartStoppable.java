package com.mosaic.lang;

/**
 * Interface for classes that need to be started/stopped.  Supports chaining services together.
 */
public interface StartStoppable<T extends StartStoppable<T>> {

    /**
     * Helper method for starting a service.  Occurred because of type mangling that
     * occurred when wanting another interface to implement the StartStoppable interface
     * AND a implementation of that interface to also extend StartStopMixin.
     *
     * Knickers + Twist -> Static fudge method.
     */
    public static <T> T startService( T s ) {
        if ( s instanceof StartStoppable ) {
            ((StartStoppable) s).start();
        }

        return s;
    }

    public static <T> T stopService( T s ) {
        if ( s instanceof StartStoppable ) {
            ((StartStoppable) s).stop();
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
     * Before this service is started, ensure that otherServices is started first.  This models
     * the situation where this object depends on the services of another service.
     */
    public T appendServicesToStartBefore( Object... others );


    /**
     * After this service has been started, then also start the specified services.  This method
     * is not for when otherServices depends on this service, but it is for linking together the
     * life cycle of several objects to ensure that they start and stop together.  For example,
     * a common scenario is to link all services to SystemX.  Even though SystemX does not depend on
     * those services (those services may in-fact depend on SystemX), it means that SystemX can
     * be used to start/stop all services together from one call.  Which is very convenient.
     */
    public T appendServicesToStartAfter( Object... otherServices );

}
