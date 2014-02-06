package com.mosaic.lang;

/**
 * Interface for classes that need to be started/stopped.
 */
public interface StartStoppable<T extends StartStoppable<T>> {

    /**
     * Tell this object to allocate any resources that it needs.  It may be
     * called multiple times, so only do the work if it is not already ready.
     */
    public T init();

    /**
     * Tell this object to release its resources.  It must be called exactly
     * the same number of times as init() before it will release resources.
     */
    public T tearDown();

    /**
     * Is this object running?  Turns true after init() has been called for
     * the first time, and false after tearDown() has been called for the
     * last time.
     */
    public boolean isReady();

    /**
     * Chain the calls of init() and tearDown() from this object to the
     * specified objects.  That is if the specified object is an instance
     * of StartStoppable.
     */
    public T chainTo( Object...others );

}
