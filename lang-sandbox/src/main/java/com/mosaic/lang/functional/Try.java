package com.mosaic.lang.functional;

import com.mosaic.lang.Failure;


/**
 * Represents either a successful result or a failure.
 */
public interface Try<T> {

    /**
     * Returns true if the try contains the successful result.  False means
     * that the try contains a failure of some kind.
     */
    public boolean hasResult();


    /**
     * Returns true if the try contains a failure.  A failure represents
     * that something went wrong, which may or may not be exceptional.
     */
    public boolean hasFailure();


    /**
     * Returns the result of the job if available.  This method will return null
     * when the try contains a failure or a null result. Thus confirming the state of the
     * future before calling this method will avoid ambiguity of what null means.
     */
    public T getResult();


    /**
     * Returns the description of why the job failed.  This method will return null
     * when the job has not failed.
     */
    public Failure getFailure();

    /**
     * Creates a new Try that will contain the mapped result of this try.  If this
     * try has failed then this method will return this try unmodified.
     */
    public <B> Try<B> mapResult( final Function1<T,B> mappingFunction );

    /**
     * Creates a new Try that will contain the mapped result of this try.  If this
     * try has failed then this method will return this try unmodified.<p/>
     *
     * This method differs from mapResult in that the mapping function returns
     * another Try.  This method will return that Try directly and will not
     * wrap it with another Try the way that mapResult would.
     */
    public <B> Try<B> flatMapResult( final Function1<T,Try<B>> mappingFunction );


    /**
     * Offers the opportunity to recover from a failure. If this Try contains
     * a result then this instance will be returned unmodified.  Otherwise
     * on a failure the recovery function will be invoked and a new Try
     * created containing its result will be returned.
     */
    public Try<T> recover( final Function1<Failure,T> recoveryFunction );


    /**
     * Offers the opportunity to recover from a failure. If this Try contains
     * a result then this instance will be returned unmodified.  Otherwise
     * on a failure the recovery function will be invoked and its result returned
     * unmodified.
     */
    public Try<T> flatRecover( final Function1<Failure,Try<T>> recoveryFunction );


    /**
     * Offers the opportunity to modify the description of a failed task. If this
     * Try contains a result, then this instance will be returned unmodified.
     * On the other hand if the Try contains a failure, then the mappingFunction
     * will be invoked and its result will be wrapped in a new Try and returned.
     */
    public Try<T> mapFailure( final Function1<Failure,Failure> mappingFunction );


    /**
     * Offers the opportunity to modify the description of a failed task. If this
     * Try contains a result, then this instance will be returned unmodified.
     * On the other hand if the Try contains a failure, then the mappingFunction
     * will be invoked and its result will be returned unmodified.
     */
    public Try<T> flatMapFailure( final Function1<Failure,Try<Failure>> mappingFunction );

}
