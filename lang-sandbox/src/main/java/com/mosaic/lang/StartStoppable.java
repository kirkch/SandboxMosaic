package com.mosaic.lang;

/**
 * Interface for classes that need to be started/stopped.
 */
public interface StartStoppable<T extends StartStoppable<T>> {

    /**
     * Tell this object to allocateNewRecord any resources that it needs.  It may be
     * called multiple times, so only do the work if it is not already ready.
     */
    public T start();

    /**
     * Tell this object to free its resources.  It must be called exactly
     * the same number of times as start() before it will free resources.
     */
    public T stop();

    /**
     * Is this object running?  Turns true after start() has been called for
     * the first time, and false after stop() has been called for the
     * last time.
     */
    public boolean isRunning();

    /**
     * Chain the calls of start() and stop() from this object to the
     * specified objects.  That is if the specified object is an instance
     * of StartStoppable.
     */
    public T appendDependency( Object... others );

}
