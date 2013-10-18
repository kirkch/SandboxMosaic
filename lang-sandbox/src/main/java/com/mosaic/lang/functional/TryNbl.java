package com.mosaic.lang.functional;

import com.mosaic.lang.Failure;


/**
 * Represents either a successful result or a failure.  The result of a try
 * may or may not be available immediately, in other words the work may be
 * carrying on asynchronously in the background and the result will become
 * available later.  Otherwise known as a Promise, or a Future.
 */
public interface TryNbl<T> {

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
    public Nullable<T> getResultNoBlock();


    /**
     * Returns the description of why the job failed.  This method will return null
     * when the job has not failed.
     */
    public Failure getFailureNoBlock();

    /**
     * Creates a new Try that will contain the mapped result of this try.  If this
     * try has failed then this method will return this try unmodified.
     */
    public <B> TryNbl<B> mapResult( final Function1<T,B> mappingFunction );

    /**
     * Creates a new Try that will contain the mapped result of this try.  If this
     * try has failed then this method will return this try unmodified.<p/>
     *
     * This method differs from mapResult only in that the mapping function returns
     * another Try.  This method will return that Try directly and will not
     * wrap it with another Try the way that mapResult would.
     */
    public <B> TryNbl<B> flatMapResult( final Function1<T,TryNbl<B>> mappingFunction );


    /**
     * Creates a new try that cannot be null.  If this TryNbl holds a null value
     * then the mapping function will be invoked and its result will be returned.
     * However if the result is not null then the result of this TryNbl will be
     * wrapped in a Try.
     */
    public Try<T> replaceNull(Function0<T> mappingFunction);

    /**
     * Creates a new try that cannot be null.  If this TryNbl holds a null value
     * then the mapping function will be invoked and its result will be returned.
     * However if the result is not null then the result of this TryNbl will be
     * wrapped in a Try.<p/>
     *
     * This method differs from replaceNull only in that the mapping function returns
     * another Try.  This method will return that Try directly and will not
     * wrap it with another Try the way that mapResult would.
     */
    public Try<T> flatReplaceNull(Function0<Try<T>> mappingFunction);

    /**
     * Offers the opportunity to recover from a failure. If this Try contains
     * a result then this instance will be returned unmodified.  Otherwise
     * on a failure the recovery function will be invoked and a new Try
     * created containing its result will be returned.
     */
    public TryNbl<T> recover( final Function1<Failure,T> recoveryFunction );


    /**
     * Offers the opportunity to recover from a failure. If this Try contains
     * a result then this instance will be returned unmodified.  Otherwise
     * on a failure the recovery function will be invoked and its result returned
     * unmodified.
     */
    public TryNbl<T> flatRecover( final Function1<Failure,TryNbl<T>> recoveryFunction );


    /**
     * Offers the opportunity to modify the description of a failed task. If this
     * Try contains a result, then this instance will be returned unmodified.
     * On the other hand if the Try contains a failure, then the mappingFunction
     * will be invoked and its result will be wrapped in a new Try and returned.
     */
    public TryNbl<T> mapFailure( final Function1<Failure,Failure> mappingFunction );


    /**
     * Offers the opportunity to modify the description of a failed task. If this
     * Try contains a result, then this instance will be returned unmodified.
     * On the other hand if the Try contains a failure, then the mappingFunction
     * will be invoked and its result will be returned unmodified.
     */
    public TryNbl<T> flatMapFailure( final Function1<Failure,TryNbl<Failure>> mappingFunction );



    /**
     * Registers a callback to be called when this future is completed either
     * by a result or a failure.<p/>
     *
     * If registered before this future is completed, then the callback will be
     * called from the same thread that completed the future.  If the future is
     * already completed then the callback will be invoked immediately from this
     * thread before onResult returns.
     */
    public void onComplete( CompletedCallbackNbl<T> callback );

}
