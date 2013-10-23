package com.mosaic.utils.concurrent;


import com.mosaic.lang.Failure;
import com.mosaic.lang.ThreadSafe;
import com.mosaic.lang.functional.CompletedCallback;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.Try;
import com.mosaic.lang.functional.VoidFunction1;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * A Future presents the result of an asynchronous calculation.  The calculation
 * will usually be carried out in another thread and the Future acts as a container
 * for the result once the result is ready to be shared.<p/>
 *
 * This implementation of Future focuses on offering support for composing futures
 * together and light weight error handling.  The traditional pre-Java 8 Future
 * worked okay for a single asynchronous job, however coordinating multiple asynchronous
 * jobs that also themselves kicked off other asynchronous work quickly became
 * a nested speghetti of code with inconsistent levels of noisey error handling.
 * Java 8's solution offers some of the important parts of a solution, however it
 * lacks the same options for light weight error handling.<p/>
 *
 *
 * A Future has three states:
 *
 * <ul>
 *     <li>INCOMPLETE: also called a promise.  The future has not received the result yet.</li>
 *     <li>HAS_RESULT: the calculation completed successfully and the value that the calculation produced is available.</li>
 *     <li>HAS_FAILURE: the calculation errored. Details of the failure are available via the future.</li>
 * </ul>
 *
 * Upon completing a future the callbacks registered with the future will fire in
 * the same thread that completed the future.  These callbacks may trigger the
 * completion of other futures or they may perform custom work.  To trigger
 * custom work use onComplete, onResult and onFailure.  To chain futures
 * together use mapResult, flatMapResult, mapFailure, flatMapResult, recover
 * and flatRecover.<p/>
 *
 * Factory methods exist to create Futures that are already complete as their
 * result already exists.  Promises that are completed later require the producer
 * to invoke either completeWithResult or completeWithFailure.<p/>
 *
 * The following example demonstrates how a future may be created, and the result
 * processed after another thread has completed generating the promised value.
 *
 * <code>
 *     Future&lt;String> future = Future.promise()
 *         .mapResult( new Function1<String,Integer>() {
 *             public Integer invoke( String v ) {
 *                  return v.length();
 *             }
 *         }).onComplete(new CompletedCallback<String>() {
 *             public void completedWithResult(String result) {
 *                 // do work
 *             }
 *
 *             public void completedWithFailure(Failure f) {
 *                 // report failure
 *             }
 *         });
 *
 *     // schedule job with a worker thread that will complete the future when ready
 *
 *     return future;
 * </code>
 *
 * <h2>Patterns:</h2>
 *
 * <h3>Void</h3>
 *
 * <code>Future&lt;Void></code>
 *
 * Futures that declare a result type of java.lang.Void will, by convention always
 * return null when complete.
 */
@SuppressWarnings({"unchecked", "Convert2Diamond"})
@ThreadSafe( "state stored in an immutable class and shared via an atomic reference " )
public class Future<T> implements Try<T> {

    private final Atomic<InternalState<T>>   stateReference;
    private final List<CompletedCallback<T>> callbacks      = new ArrayList<CompletedCallback<T>>();  // NB synchronize on list before reading from or writing to it


    /**
     * A factory method for creating a future that starts life with a result.
     */
    public static <T> Future<T> successful( T result ) {
        Future<T> f = new Future<T>();

        f.completeWithResult(result);

        return f;
    }

    /**
     * A factory method for creating a future that starts life holding the
     * description of why a job failed.
     */
    public static <T> Future<T> failed( Failure f ) {
        Future<T> future = new Future<T>();

        future.completeWithFailure(f);

        return future;
    }

    /**
     * A factory method for creating a future that has not been completed yet.
     * A worker thread 'promises' to complete it as soon as the result is ready
     * or an error is generated trying.
     */
    public static <T> Future<T> promise() {
        return new Future<T>();
    }

    public static <T> Future<List<T>> joinAll( Future<T>...futures ) {
        return joinAll( Arrays.asList(futures) );
    }

    private static <T> List<T> createThreadSafeResultsList( int numFutures ) {
        // NB must be thread safe as used in joinAll where each future could
        // complete at any time from different threads.

        Vector<T>  results   = new Vector<T>(numFutures);
        results.setSize(numFutures);

        return results;
    }

    /**
     * Wait for multiple futures to complete.  Creates a new future that completes
     * either when all of the specified futures complete or when the first one of
     * the fails.<p/>
     *
     * When all of the specified futures complete with results, the barrier
     * future returned by this method will complete with a list of all of the
     * results returned by the originating futures in the same order as they
     * were supplied to this method.
     */
    public static <T> Future<List<T>> joinAll( List<Future<T>> futures ) {
        final Future<List<T>> promise = promise();

        int numFutures = futures.size();
        if ( numFutures == 0 ) {
            return successful( Collections.<T>emptyList() );
        }

        final AtomicInteger countDown = new AtomicInteger(numFutures);
        final List<T>       results   = createThreadSafeResultsList(numFutures);

        int i=0;
        for ( Future<T> f : futures ) {
            final int resultsArrayIndex = i;  // NB thread safe copy of i remembered by callback

            f.onComplete( new CompletedCallback<T>() {
                public void completedWithResult( T result ) {
                    results.set(resultsArrayIndex, result);

                    int numIncompleteFutures = countDown.decrementAndGet();
                    if ( numIncompleteFutures == 0 ) {
                        promise.completeWithResult(results);
                    }
                }

                public void completedWithFailure( Failure f ) {
                    promise.completeWithFailure(f);
                }
            });

            i++;
        }

        return promise;
    }

    /**
     * Process asynchronously in parallel each value specified.  The asynchronous
     * work is kicked off by the supplied futureGenerator.<p/>
     *
     * This method creates a new future which completes either when each of the
     * child futures created by futureGenerator completes with a result or when
     * the first one of them fails.<p/>
     *
     * When all of the futures created by futureGenerator complete successfully
     * then the future created by this method will complete with a list of each
     * values returned by each of the child futures in the same order as the
     * values supplied to this method.  That is the first value in the values list
     * will have a future created for it by the futureGenerator and the result
     * from that future will be stored in the first element (index 0) of the list
     * returned by the list returned by this method.<p/>
     *
     * This function reduces the amount of boilerplate code required to respond
     * to asynchronous work without blocking a thread.  For example, given a list
     * of userIds parallelizeEach could be used to kick off an asynchronous request
     * to download each userId from a restful/http endpoint and once complete
     * all of the user objects would be collected and stored in the future
     * returned by this method in the same order as the user ids were passed to
     * this function.
     */
    public static <T,B> Future<List<B>> parallelizeEach( List<T> values, Function1<T, Future<B>> futureGenerator ) {
        final Future<List<B>> promise = promise();

        int numValues = values.size();
        if ( numValues == 0 ) {
            return successful( Collections.<B>emptyList() );
        }

        final AtomicInteger countDown = new AtomicInteger(numValues);
        final List<B>       results   = createThreadSafeResultsList(numValues);

        int i=0;
        for ( T value : values ) {
            final int resultsArrayIndex = i;  // NB thread safe copy of i remembered by callback

            Future<B> childFuture = futureGenerator.invoke(value);

            childFuture.onComplete( new CompletedCallback<B>() {
                public void completedWithResult( B result ) {
                    results.set(resultsArrayIndex, result);

                    int numIncompleteFutures = countDown.decrementAndGet();
                    if ( numIncompleteFutures == 0 ) {
                        promise.completeWithResult(results);
                    }
                }

                public void completedWithFailure( Failure f ) {
                    promise.completeWithFailure(f);
                }
            });

            i++;
        }

        return promise;
    }



    /**
     * Creates an instance of future that has not yet been completed.
     * A worker thread 'promises' to complete it as soon as the result is ready
     * or an error is generated trying.
     */
    public Future() {
        // NB lazySet here will be visible to the thread that created the future,
        // however other threads will see null until the cpu memory ordered buffers
        // clear (x86)

        // NB no check for null checks for currentState have been included.
        // The logic being that, either the promise is being completed by the
        // constructing thread (inwhich case the state will be visible) or
        // the constructing thread has shared the future with another thread.
        // In such an event it is expected that the state will also have become
        // visible to the other thread as well.  This should always be true
        // on x86, I am unclear about machines with weaker memory models
        // thus we should be on the look out for null pointer exceptions from
        // this class.

//        stateReference.lazySet( InternalState.<T>promise() );  todo benchmark this idea

        stateReference = new Atomic( InternalState.<T>promise() );
    }

    public boolean isComplete() {
        InternalState<T> state = stateReference.get();

        return state.isComplete();
    }

    /**
     * Spin the CPU until this future is complete.  Has lower latency than
     * spinUntilComplete(maxWaitMillis) as it does not need to invoke
     * System.currentTimeMillis().  However this method will be an
     * infinite loop if this future never completes.  Use with care.
     *
     * @return returns itself for convenience of chaining calls
     */
    @SuppressWarnings("StatementWithEmptyBody")
    public Future<T> spinUntilComplete() {
        while ( !isComplete() ) {}

        return this;
    }

    /**
     * Spin the CPU until this future is complete.  On each iteration the
     * maxWaitMillis condition is tested, if more millis has passed since the
     * method was called then an IllegalStateException will be thrown.
     *
     * @return returns itself for convenience of chaining calls
     */
    public Future<T> spinUntilComplete( long maxWaitMillis ) {
        long startMillis = System.currentTimeMillis();

        while ( !isComplete() ) {
            long waitedSoFarMillis = System.currentTimeMillis() - startMillis;
            if ( waitedSoFarMillis > maxWaitMillis ) {
                throw new IllegalStateException("spinning aborted after "+waitedSoFarMillis+"millis");
            }
        }

        return this;
    }

    public boolean hasResult() {
        InternalState<T> state = stateReference.get();

        return state.hasResult();
    }

    public boolean hasFailure() {
        InternalState<T> state = stateReference.get();

        return state.hasFailure();
    }

    public T getResultNoBlock() {
        InternalState<T> state = stateReference.get();

        if ( state.hasFailure() ) {
            throw new IllegalStateException( "Unable to retrieve result as future has failed: '"+state.failure.getMessage()+"'");
        }

        return state.result;
    }

    public Failure getFailureNoBlock() {
        InternalState<T> state = stateReference.get();

        return state.failure;
    }

    public boolean completeWithResult( T result ) {
        InternalState<T> newState = InternalState.success(result);

        boolean didStateChange = completeWith( newState );
        if ( didStateChange ) {
            sendCompletedWithResultNotifications(result);
        }

        return didStateChange;
    }

    /**
     * Stores a description of why a job failed within this future.  If this future
     * has already been completed then the failure will be ignored and this method
     * will return false.  Successful storage of the failure in this future will
     * result in this method returning true.
     */
    public boolean completeWithFailure( Failure f ) {
        InternalState<T> newState = InternalState.failed(f);

        boolean didStateChange = completeWith( newState );
        if ( didStateChange ) {
            sendCompletedWithFailureNotifications(f);
        }

        return didStateChange;
    }

    public void completeWithTry(Try<T> otherFuture) {
        otherFuture.onComplete(
                new CompletedCallback<T>() {
                    public void completedWithResult( T result ) {
                        Future.this.completeWithResult( result );
                    }

                    public void completedWithFailure( Failure f ) {
                        Future.this.completeWithFailure( f );
                    }
                }
        );
    }

    public <B> Future<B> mapResult( final Function1<T,B> mappingFunction ) {
        InternalState<T> state = stateReference.get();

        switch (state.stateEnum) {
            case PROMISE:
                final Future<B> mappedFuture = new Future<B>();

                this.onComplete( new CompletedCallback<T>() {
                    public void completedWithResult( T result ) {
                        handleMapResult( mappedFuture, result, mappingFunction );
                    }

                    public void completedWithFailure( Failure f ) {
                        mappedFuture.completeWithFailure( f );
                    }
                });

                return mappedFuture;
            case HAS_RESULT:
                return handleMapResult( Future.<B>promise(), getResultNoBlock(), mappingFunction );
            case HAS_FAILURE:
                return (Future<B>) this;  // NB future contains a failure, and so will never hold a result; thus the cast is safe
            default:
                throw new IllegalStateException("unknown state: " + state.stateEnum.name() );
        }
    }

    public <B> Future<B> flatMapResult( final Function1<T,Try<B>> mappingFunction ) {
        InternalState<T> state = stateReference.get();

        switch (state.stateEnum) {
            case PROMISE:
                final Future<B> mappedFuture = new Future<B>();

                this.onComplete( new CompletedCallback<T>() {
                    public void completedWithResult( T result ) {
                        handleFlatMapResult(mappedFuture, result, mappingFunction);
                    }

                    public void completedWithFailure( Failure f ) {
                        mappedFuture.completeWithFailure( f );
                    }
                });

                return mappedFuture;
            case HAS_RESULT:
                return handleFlatMapResult(Future.<B>promise(), getResultNoBlock(), mappingFunction);
            case HAS_FAILURE:
                return (Future<B>) this;  // NB future contains a failure, and so will never hold a result; thus the cast is safe
            default:
                throw new IllegalStateException("unknown state: " + state.stateEnum.name() );
        }
    }

    public Future<T> recover( final Function1<Failure,T> recoveryFunction ) {
        InternalState<T> state = stateReference.get();

        switch (state.stateEnum) {
            case PROMISE:
                final Future<T> mappedFuture = new Future<T>();

                this.onComplete( new CompletedCallback<T>() {
                    public void completedWithResult( T result ) {
                        mappedFuture.completeWithResult(result);
                    }

                    public void completedWithFailure( Failure f ) {
                        handleRecovery(mappedFuture, f, recoveryFunction);
                    }
                });

                return mappedFuture;
            case HAS_RESULT:
                return this;
            case HAS_FAILURE:
                return handleRecovery( Future.<T>promise(), getFailureNoBlock(), recoveryFunction );
            default:
                throw new IllegalStateException("unknown state: " + state.stateEnum.name() );
        }
    }

    public Future<T> flatRecover( final Function1<Failure,Try<T>> recoveryFunction ) {
        InternalState<T> state = stateReference.get();

        switch (state.stateEnum) {
            case PROMISE:
                final Future<T> mappedFuture = new Future<T>();

                this.onComplete( new CompletedCallback<T>() {
                    public void completedWithResult( T result ) {
                        mappedFuture.completeWithResult(result);
                    }

                    public void completedWithFailure( Failure f ) {
                        handleFlatRecovery(mappedFuture, f, recoveryFunction);
                    }
                });

                return mappedFuture;
            case HAS_RESULT:
                return this;
            case HAS_FAILURE:
                return handleFlatRecovery(Future.<T>promise(), getFailureNoBlock(), recoveryFunction);
            default:
                throw new IllegalStateException("unknown state: " + state.stateEnum.name() );
        }
    }

    public Future<T> mapFailure( final Function1<Failure,Failure> mappingFunction ) {
        InternalState<T> state = stateReference.get();

        switch (state.stateEnum) {
            case PROMISE:
                final Future<T> mappedFuture = new Future<T>();

                this.onComplete( new CompletedCallback<T>() {
                    public void completedWithResult( T result ) {
                        mappedFuture.completeWithResult( result );
                    }

                    public void completedWithFailure( Failure failure ) {
                        handleMapFailure(mappedFuture, failure, mappingFunction);
                    }
                });

                return mappedFuture;
            case HAS_RESULT:
                return this;
            case HAS_FAILURE:
                return handleMapFailure(Future.<T>promise(), getFailureNoBlock(), mappingFunction);
            default:
                throw new IllegalStateException("unknown state: " + state.stateEnum.name() );
        }
    }

    public Future<T> flatMapFailure( final Function1<Failure,Try<Failure>> mappingFunction ) {
        InternalState<T> state = stateReference.get();

        switch (state.stateEnum) {
            case PROMISE:
                final Future<T> mappedFuture = new Future<T>();

                this.onComplete( new CompletedCallback<T>() {
                    public void completedWithResult( T result ) {
                        mappedFuture.completeWithResult( result );
                    }

                    public void completedWithFailure( Failure failure ) {
                        handleFlatMapFailure(mappedFuture, failure, mappingFunction);
                    }
                });

                return mappedFuture;
            case HAS_RESULT:
                return this;
            case HAS_FAILURE:
                return handleFlatMapFailure(Future.<T>promise(), getFailureNoBlock(), mappingFunction);
            default:
                throw new IllegalStateException("unknown state: " + state.stateEnum.name() );
        }
    }

    /**
     * Registers a callback to be called when this future is completed with a result.<p/>
     *
     * If registered before this future is completed, then the callback will be
     * called from the same thread that completed the future.  If the future is
     * already completed then the callback will be invoked immediately from this
     * thread before onResult returns.
     */
    public void onResult( final VoidFunction1<T> callback ) {
        this.onComplete( new CompletedCallback<T>() {
            public void completedWithResult(T result) {
                callback.invoke( result );
            }

            public void completedWithFailure(Failure f) {}
        });
    }

    /**
     * Registers a callback to be called when this future is completed with a failure.<p/>
     *
     * If registered before this future is completed, then the callback will be
     * called from the same thread that completed the future.  If the future is
     * already completed then the callback will be invoked immediately from this
     * thread before onResult returns.
     */
    public void onFailure( final VoidFunction1<Failure> callback ) {
        this.onComplete( new CompletedCallback<T>() {
            public void completedWithResult(T result) {}

            public void completedWithFailure(Failure f) {
                callback.invoke( f );
            }
        });
    }

    /**
     * Registers a callback to be called when this future is completed either
     * by a result or a failure.<p/>
     *
     * If registered before this future is completed, then the callback will be
     * called from the same thread that completed the future.  If the future is
     * already completed then the callback will be invoked immediately from this
     * thread before onResult returns.
     */
    public void onComplete( CompletedCallback<T> callback ) {
        synchronized (callbacks) {
            InternalState<T> currentState = stateReference.get();

            switch ( currentState.stateEnum ) {
                case PROMISE:
                    callbacks.add( callback );
                    break;
                case HAS_RESULT:
                    callback.completedWithResult( currentState.result );
                    break;
                case HAS_FAILURE:
                    callback.completedWithFailure(currentState.failure);
                    break;
                default:
                    throw new IllegalStateException("unknown state: " + currentState.stateEnum.name() );
            }
        }
    }



    private <B> Future<B> handleMapResult( Future<B> promise, T preMappedResult, Function1<T,B> mappingFunction ) {
        try {
            promise.completeWithResult( mappingFunction.invoke(preMappedResult) );
        } catch ( Exception e ) {
            promise.completeWithFailure( new Failure(e) );
        }

        return promise;
    }

    private <B> Future<B> handleFlatMapResult( final Future<B> promise, T preMappedResult, Function1<T,Try<B>> mappingFunction ) {
        try {
            Try<B> childFuture = mappingFunction.invoke(preMappedResult);

            childFuture.onComplete(new CompletedCallback<B>() {
                @Override
                public void completedWithResult(B result) {
                    promise.completeWithResult(result);
                }

                @Override
                public void completedWithFailure(Failure f) {
                    promise.completeWithFailure(f);
                }
            });
        } catch ( Exception e ) {
            promise.completeWithFailure( new Failure(e) );
        }

        return promise;
    }

    private Future<T> handleRecovery( Future<T> promise, Failure failureToRecoverFrom, Function1<Failure,T> recoveryFunction ) {
        try {
            promise.completeWithResult(recoveryFunction.invoke(failureToRecoverFrom));
        } catch ( Exception e ) {
            promise.completeWithFailure( new Failure(failureToRecoverFrom,e) );
        }

        return promise;
    }

    private Future<T> handleFlatRecovery( final Future<T> promise, final Failure failureToRecoverFrom, Function1<Failure,Try<T>> recoveryFunction ) {
        try {
            Try<T> recoveredFuture = recoveryFunction.invoke(failureToRecoverFrom);

            recoveredFuture.onComplete( new CompletedCallback<T>() {
                public void completedWithResult(T recoveredResult) {
                    promise.completeWithResult(recoveredResult);
                }

                public void completedWithFailure(Failure recoveryFailure) {
                    promise.completeWithFailure( new Failure(failureToRecoverFrom,recoveryFailure) );
                }
            });
        } catch ( Exception e ) {
            promise.completeWithFailure( new Failure(failureToRecoverFrom,e) );
        }

        return promise;
    }

    private Future<T> handleMapFailure( Future<T> promise, Failure originalFailure, Function1<Failure,Failure> mappingFunction ) {
        try {
            Failure mappedFailure   = mappingFunction.invoke(originalFailure);
            Failure chainedFailure  = new Failure(originalFailure, mappedFailure);

            promise.completeWithFailure(chainedFailure);
        } catch ( Exception e ) {
            promise.completeWithFailure(new Failure(originalFailure, e));
        }

        return promise;
    }

    private Future<T> handleFlatMapFailure( final Future<T> promise, final Failure originalFailure, Function1<Failure,Try<Failure>> mappingFunction ) {
        try {
            Try<Failure> mappedFailureFuture = mappingFunction.invoke(originalFailure);

            mappedFailureFuture.onComplete( new CompletedCallback<Failure>() {
                @Override
                public void completedWithResult( Failure mappedFailure ) {
                    Failure chainedFailure  = new Failure(originalFailure, mappedFailure);

                    promise.completeWithFailure(chainedFailure);
                }

                @Override
                public void completedWithFailure( Failure f ) {
                    promise.completeWithFailure( new Failure(originalFailure,f) );
                }
            });
        } catch ( Exception e ) {
            promise.completeWithFailure(new Failure(originalFailure, e));
        }

        return promise;
    }

    private boolean completeWith( final InternalState<T> newState ) {
        InternalState<T> updatedState = stateReference.update( new Function1<InternalState<T>,InternalState<T>>() {
            public InternalState<T> invoke( InternalState<T> currentState ) {
                if ( currentState.isComplete() ) {
                    return currentState;
                }

                return newState;
            }
        });

        return updatedState == newState; // didStateChange?
    }

    private void sendCompletedWithResultNotifications( T result ) {
        synchronized (callbacks) {
            for ( CompletedCallback<T> callback : callbacks ) {
                callback.completedWithResult(result);
            }

            callbacks.clear(); // NB frees up memory earlier and prevents double dispatches by accident
        }
    }

    private void sendCompletedWithFailureNotifications( Failure f ) {
        synchronized (callbacks) {
            for ( CompletedCallback<T> callback : callbacks ) {
                callback.completedWithFailure(f);
            }

            callbacks.clear(); // NB frees up memory earlier and prevents double dispatches by accident
        }
    }




    static enum FutureStateEnum {
        PROMISE, HAS_RESULT, HAS_FAILURE
    }

    @SuppressWarnings("unchecked")
    static class InternalState<T> {

        private static final InternalState PROMISE_STATE = new InternalState(FutureStateEnum.PROMISE,null,null);

        public static <T> InternalState<T> promise() {
            return (InternalState<T>) PROMISE_STATE;
        }

        public static <T> InternalState<T> success( T result ) {
            return new InternalState<T>( FutureStateEnum.HAS_RESULT, result, null );
        }

        public static <T> InternalState<T> failed( Failure f ) {
            return new InternalState<T>( FutureStateEnum.HAS_FAILURE, null, f );
        }


        public final FutureStateEnum stateEnum;
        public final T               result;
        public final Failure         failure;

        private InternalState( FutureStateEnum stateEnum, T result, Failure failure ) {
            this.stateEnum = stateEnum;
            this.result    = result;
            this.failure   = failure;
        }

        public boolean isComplete() {
            return stateEnum != FutureStateEnum.PROMISE;
        }

        public boolean hasResult() {
            return stateEnum == FutureStateEnum.HAS_RESULT;
        }

        public boolean hasFailure() {
            return stateEnum == FutureStateEnum.HAS_FAILURE;
        }
    }

}
