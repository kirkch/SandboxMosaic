package com.mosaic.utils.concurrent;


import com.mosaic.lang.Failure;
import com.mosaic.lang.ThreadSafe;
import com.mosaic.lang.Validate;
import com.mosaic.lang.functional.*;
import com.mosaic.utils.concurrent.Future.InternalState;

import java.util.*;


/**
 * As Future&lt;Nullable&lt;T>.
 */
@SuppressWarnings({"unchecked", "Convert2Diamond"})
@ThreadSafe( "state stored in an immutable class and shared via an atomic reference " )
public class FutureNbl<T> implements TryNbl<T> {

    private final Atomic<InternalState<Nullable<T>>>   stateReference;
    private final List<CompletedCallbackNbl<T>>        callbacks      = new ArrayList<CompletedCallbackNbl<T>>();  // NB synchronize on list before reading from or writing to it


    /**
     * A factory method for creating a future that starts life with a result.
     */
    public static <T> FutureNbl<T> successful( Nullable<T> result ) {
        FutureNbl<T> f = new FutureNbl<T>();

        f.completeWithResultNbl(result);

        return f;
    }

    /**
     * A factory method for creating a future that starts life holding the
     * description of why a job failed.
     */
    public static <T> FutureNbl<T> failed( Failure f ) {
        FutureNbl<T> future = new FutureNbl<T>();

        future.completeWithFailure(f);

        return future;
    }

    /**
     * A factory method for creating a future that has not been completed yet.
     * A worker thread 'promises' to complete it as soon as the result is ready
     * or an error is generated trying.
     */
    public static <T> FutureNbl<T> promise() {
        return new FutureNbl<T>();
    }

    private static <T> List<T> createThreadSafeResultsList( int numFutures ) {
        // NB must be thread safe as used in joinAll where each future could
        // complete at any time from different threads.

        Vector<T>  results   = new Vector<T>(numFutures);
        results.setSize(numFutures);

        return results;
    }




    /**
     * Creates an instance of future that has not yet been completed.
     * A worker thread 'promises' to complete it as soon as the result is ready
     * or an error is generated trying.
     */
    public FutureNbl() {
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

        stateReference = new Atomic( InternalState.<Nullable<T>>promise() );
    }

    public boolean isComplete() {
        InternalState<Nullable<T>> state = stateReference.get();

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
    public FutureNbl<T> spinUntilComplete() {
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
    public FutureNbl<T> spinUntilComplete( long maxWaitMillis ) {
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
        InternalState<Nullable<T>> state = stateReference.get();

        return state.hasResult();
    }

    public boolean hasFailure() {
        InternalState<Nullable<T>> state = stateReference.get();

        return state.hasFailure();
    }

    public Nullable<T> getResultNoBlock() {
        InternalState<Nullable<T>> state = stateReference.get();

        return state.result;
    }

    public Failure getFailureNoBlock() {
        InternalState<Nullable<T>> state = stateReference.get();

        return state.failure;
    }

    public boolean completeWithResult( T result ) {
        Validate.notNull(result, "result");

        return completeWithResultNbl( Nullable.createNullable(result) );
    }

    public boolean completeWithResultNbl( Nullable<T> result ) {
        InternalState<Nullable<T>> newState = InternalState.success(result);

        boolean didStateChange = completeWith( newState );
        if ( didStateChange ) {
            sendCompletedWithResultNotificationsNbl(result);
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
        InternalState<Nullable<T>> newState = InternalState.failed(f);

        boolean didStateChange = completeWith( newState );
        if ( didStateChange ) {
            sendCompletedWithFailureNotifications(f);
        }

        return didStateChange;
    }


    public <B> FutureNbl<B> mapResult( final Function1<T,B> mappingFunction ) {
        InternalState<Nullable<T>> state = stateReference.get();

        switch (state.stateEnum) {
            case PROMISE:
                final FutureNbl<B> mappedFuture = new FutureNbl<B>();

                this.onComplete( new CompletedCallbackNbl<T>() {
                    public void completedWithNullResult() {
                        mappedFuture.completeWithResultNbl(Nullable.NULL);
                    }

                    public void completedWithResult( T result ) {
                        handleMapResult( mappedFuture, result, mappingFunction );
                    }

                    public void completedWithFailure( Failure f ) {
                        mappedFuture.completeWithFailure( f );
                    }
                });

                return mappedFuture;
            case HAS_RESULT:
                Nullable<T> resultNbl = state.result;

                if ( resultNbl.isNull() ) {
                    return (FutureNbl<B>) this;
                } else {
                    return handleMapResult( FutureNbl.<B>promise(), resultNbl.getValue(), mappingFunction );
                }
            case HAS_FAILURE:
                return (FutureNbl<B>) this;  // NB future contains a failure, and so will never hold a result; thus the cast is safe
            default:
                throw new IllegalStateException("unknown state: " + state.stateEnum.name() );
        }
    }

    public <B> FutureNbl<B> flatMapResult( final Function1<T,TryNbl<B>> mappingFunction ) {
        InternalState<Nullable<T>> state = stateReference.get();

        switch (state.stateEnum) {
            case PROMISE:
                final FutureNbl<B> mappedFuture = new FutureNbl<B>();

                this.onComplete( new CompletedCallbackNbl<T>() {
                    public void completedWithNullResult() {
                        mappedFuture.completeWithResultNbl(Nullable.NULL);
                    }

                    public void completedWithResult( T result ) {
                        handleFlatMapResult(mappedFuture, result, mappingFunction);
                    }

                    public void completedWithFailure( Failure f ) {
                        mappedFuture.completeWithFailure( f );
                    }
                });

                return mappedFuture;
            case HAS_RESULT:
                Nullable<T> result = getResultNoBlock();
                if ( result.isNull() ) {
                    return (FutureNbl<B>) this;
                }

                return handleFlatMapResult(FutureNbl.<B>promise(), result.getValue(), mappingFunction);
            case HAS_FAILURE:
                return (FutureNbl<B>) this;  // NB future contains a failure, and so will never hold a result; thus the cast is safe
            default:
                throw new IllegalStateException("unknown state: " + state.stateEnum.name() );
        }
    }

    public Future<T> replaceNull( final Function0<T> mappingFunction ) {
        final InternalState<Nullable<T>> state   = stateReference.get();
        final Future<T>                  promise = new Future<T>();

        switch (state.stateEnum) {
            case PROMISE:
                this.onComplete( new CompletedCallbackNbl<T>() {
                    public void completedWithNullResult() {
                        try {
                            promise.completeWithResult( mappingFunction.invoke() );
                        } catch ( Exception e ) {
                            promise.completeWithFailure( new Failure(e) );
                        }
                    }

                    public void completedWithResult( T result ) {
                        promise.completeWithResult(result);
                    }

                    public void completedWithFailure( Failure f ) {
                        promise.completeWithFailure(f);
                    }
                });

                break;
            case HAS_RESULT:
                try {
                    T result = state.result.getValueNbl();

                    if ( result == null ) {
                        result = mappingFunction.invoke();
                    }

                    promise.completeWithResult( result );
                } catch ( Exception e ) {
                    promise.completeWithFailure( new Failure(e) );
                }

                break;
            case HAS_FAILURE:
                promise.completeWithFailure( state.failure );
                break;
            default:
                throw new IllegalStateException("unknown state: " + state.stateEnum.name() );
        }

        return promise;
    }

    public Future<T> flatReplaceNull( final Function0<Try<T>> mappingFunction ) {
        final InternalState<Nullable<T>> state   = stateReference.get();
        final Future<T>                  promise = new Future<T>();

        switch (state.stateEnum) {
            case PROMISE:
                this.onComplete( new CompletedCallbackNbl<T>() {
                    public void completedWithNullResult() {
                        try {
                            promise.completeWithTry( mappingFunction.invoke() );
                        } catch ( Exception ex ) {
                            promise.completeWithFailure( new Failure(ex) );
                        }
                    }

                    public void completedWithResult( T result ) {
                        promise.completeWithResult(result);
                    }

                    public void completedWithFailure( Failure f ) {
                        promise.completeWithFailure( f );
                    }
                });

                break;
            case HAS_RESULT:
                T result = state.result.getValueNbl();

                if ( result == null ) {
                    try {
                        promise.completeWithTry(mappingFunction.invoke());
                    } catch ( Exception e ) {
                        promise.completeWithFailure(new Failure(e));
                    }
                } else {
                    promise.completeWithResult( result );
                }

                break;
            case HAS_FAILURE:
                promise.completeWithFailure( state.failure );

                break;
            default:
                throw new IllegalStateException("unknown state: " + state.stateEnum.name() );
        }

        return promise;
    }

    public FutureNbl<T> recover( final Function1<Failure,T> recoveryFunction ) {
        InternalState<Nullable<T>> state = stateReference.get();

        switch (state.stateEnum) {
            case PROMISE:
                final FutureNbl<T> mappedFuture = new FutureNbl<T>();

                this.onComplete( new CompletedCallbackNbl<T>() {
                    public void completedWithNullResult() {
                        mappedFuture.completeWithResultNbl(Nullable.NULL);
                    }

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
                return handleRecovery( FutureNbl.<T>promise(), getFailureNoBlock(), recoveryFunction );
            default:
                throw new IllegalStateException("unknown state: " + state.stateEnum.name() );
        }
    }

    public FutureNbl<T> flatRecover( final Function1<Failure,TryNbl<T>> recoveryFunction ) {
        InternalState<Nullable<T>> state = stateReference.get();

        switch (state.stateEnum) {
            case PROMISE:
                final FutureNbl<T> mappedFuture = new FutureNbl<T>();

                this.onComplete( new CompletedCallbackNbl<T>() {
                    public void completedWithNullResult() {
                        mappedFuture.completeWithResultNbl(Nullable.NULL);
                    }

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
                return handleFlatRecovery(FutureNbl.<T>promise(), getFailureNoBlock(), recoveryFunction);
            default:
                throw new IllegalStateException("unknown state: " + state.stateEnum.name() );
        }
    }

    public FutureNbl<T> mapFailure( final Function1<Failure,Failure> mappingFunction ) {
        InternalState<Nullable<T>> state = stateReference.get();

        switch (state.stateEnum) {
            case PROMISE:
                final FutureNbl<T> mappedFuture = new FutureNbl<T>();

                this.onComplete( new CompletedCallbackNbl<T>() {
                    public void completedWithNullResult() {
                        mappedFuture.completeWithResultNbl(Nullable.NULL);
                    }

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
                return handleMapFailure(FutureNbl.<T>promise(), getFailureNoBlock(), mappingFunction);
            default:
                throw new IllegalStateException("unknown state: " + state.stateEnum.name() );
        }
    }

    public FutureNbl<T> flatMapFailure( final Function1<Failure,TryNbl<Failure>> mappingFunction ) {
        InternalState<Nullable<T>> state = stateReference.get();

        switch (state.stateEnum) {
            case PROMISE:
                final FutureNbl<T> mappedFuture = new FutureNbl<T>();

                this.onComplete( new CompletedCallbackNbl<T>() {
                    public void completedWithNullResult() {
                        mappedFuture.completeWithResultNbl(Nullable.NULL);
                    }

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
                return handleFlatMapFailure(FutureNbl.<T>promise(), getFailureNoBlock(), mappingFunction);
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
    public void onResult( final VoidFunction1<Nullable<T>> callback ) {
        this.onComplete( new CompletedCallbackNbl<T>() {
            public void completedWithNullResult() {
                callback.invoke( Nullable.NULL );
            }

            public void completedWithResult(T result) {
                callback.invoke( Nullable.createNullable(result) );
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
        this.onComplete( new CompletedCallbackNbl<T>() {
            public void completedWithNullResult() {}

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
    public void onComplete( CompletedCallbackNbl<T> callback ) {
        synchronized (callbacks) {
            InternalState<Nullable<T>> currentState = stateReference.get();

            switch ( currentState.stateEnum ) {
                case PROMISE:
                    callbacks.add( callback );
                    break;
                case HAS_RESULT:
                    T v = currentState.result.getValueNbl();

                    if ( v == null ) {
                        callback.completedWithNullResult();
                    } else {
                        callback.completedWithResult( v );
                    }

                    break;
                case HAS_FAILURE:
                    callback.completedWithFailure(currentState.failure);
                    break;
                default:
                    throw new IllegalStateException("unknown state: " + currentState.stateEnum.name() );
            }
        }
    }



    private <B> FutureNbl<B> handleMapResult( FutureNbl<B> promise, T preMappedResult, Function1<T,B> mappingFunction ) {
        try {
            promise.completeWithResult( mappingFunction.invoke(preMappedResult) );
        } catch ( Exception e ) {
            promise.completeWithFailure( new Failure(e) );
        }

        return promise;
    }


    private <B> FutureNbl<B> handleFlatMapResult( final FutureNbl<B> promise, T preMappedResult, Function1<T,TryNbl<B>> mappingFunction ) {
        try {
            TryNbl<B> childFuture = mappingFunction.invoke(preMappedResult);

            childFuture.onComplete(new CompletedCallbackNbl<B>() {
                public void completedWithNullResult() {
                    promise.completeWithResultNbl(Nullable.NULL);
                }

                public void completedWithResult(B result) {
                    promise.completeWithResult(result);
                }

                public void completedWithFailure(Failure f) {
                    promise.completeWithFailure(f);
                }
            });
        } catch ( Exception e ) {
            promise.completeWithFailure( new Failure(e) );
        }

        return promise;
    }

    private FutureNbl<T> handleRecovery( FutureNbl<T> promise, Failure failureToRecoverFrom, Function1<Failure,T> recoveryFunction ) {
        try {
            promise.completeWithResult(recoveryFunction.invoke(failureToRecoverFrom));
        } catch ( Exception e ) {
            promise.completeWithFailure( new Failure(failureToRecoverFrom,e) );
        }

        return promise;
    }

    private FutureNbl<T> handleFlatRecovery( final FutureNbl<T> promise, final Failure failureToRecoverFrom, Function1<Failure,TryNbl<T>> recoveryFunction ) {
        try {
            TryNbl<T> recoveredFuture = recoveryFunction.invoke(failureToRecoverFrom);

            recoveredFuture.onComplete( new CompletedCallbackNbl<T>() {
                public void completedWithNullResult() {
                    promise.completeWithResultNbl(Nullable.NULL);
                }

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

    private FutureNbl<T> handleMapFailure( FutureNbl<T> promise, Failure originalFailure, Function1<Failure,Failure> mappingFunction ) {
        try {
            Failure mappedFailure   = mappingFunction.invoke(originalFailure);
            Failure chainedFailure  = new Failure(originalFailure, mappedFailure);

            promise.completeWithFailure(chainedFailure);
        } catch ( Exception e ) {
            promise.completeWithFailure(new Failure(originalFailure, e));
        }

        return promise;
    }

    private FutureNbl<T> handleFlatMapFailure( final FutureNbl<T> promise, final Failure originalFailure, Function1<Failure,TryNbl<Failure>> mappingFunction ) {
        try {
            TryNbl<Failure> mappedFailureFuture = mappingFunction.invoke(originalFailure);

            mappedFailureFuture.onComplete( new CompletedCallbackNbl<Failure>() {
                public void completedWithNullResult() {
                    promise.completeWithResultNbl(Nullable.NULL);
                }

                public void completedWithResult( Failure mappedFailure ) {
                    Failure chainedFailure  = new Failure(originalFailure, mappedFailure);

                    promise.completeWithFailure(chainedFailure);
                }

                public void completedWithFailure( Failure f ) {
                    promise.completeWithFailure( new Failure(originalFailure,f) );
                }
            });
        } catch ( Exception e ) {
            promise.completeWithFailure(new Failure(originalFailure, e));
        }

        return promise;
    }

    public void completeWithTry( Try<T> otherFuture ) {
        otherFuture.onComplete(
                new CompletedCallback<T>() {
                    public void completedWithResult( T result ) {
                        FutureNbl.this.completeWithResult( result );
                    }

                    public void completedWithFailure( Failure f ) {
                        FutureNbl.this.completeWithFailure( f );
                    }
                }
        );
    }

    private boolean completeWith( final InternalState<Nullable<T>> newState ) {
        InternalState<Nullable<T>> updatedState = stateReference.update( new Function1<InternalState<Nullable<T>>,InternalState<Nullable<T>>>() {
            public InternalState<Nullable<T>> invoke( InternalState<Nullable<T>> currentState ) {
                if ( currentState.isComplete() ) {
                    return currentState;
                }

                return newState;
            }
        });

        return updatedState == newState; // didStateChange?
    }

    private void sendCompletedWithResultNotificationsNbl(Nullable<T> result) {
        if ( result.isNull() ) {
            sendCompletedWithNullResultNotifications();
        } else {
            sendCompletedWithResultNotifications(result.getValue());
        }
    }

    private void sendCompletedWithNullResultNotifications() {
        synchronized (callbacks) {
            for ( CompletedCallbackNbl<T> callback : callbacks ) {
                callback.completedWithNullResult();
            }

            callbacks.clear(); // NB frees up memory earlier and prevents double dispatches by accident
        }
    }

    private void sendCompletedWithResultNotifications(T result) {
        synchronized (callbacks) {
            for ( CompletedCallbackNbl<T> callback : callbacks ) {
                callback.completedWithResult(result);
            }

            callbacks.clear(); // NB frees up memory earlier and prevents double dispatches by accident
        }
    }

    private void sendCompletedWithFailureNotifications( Failure f ) {
        synchronized (callbacks) {
            for ( CompletedCallbackNbl<T> callback : callbacks ) {
                callback.completedWithFailure(f);
            }

            callbacks.clear(); // NB frees up memory earlier and prevents double dispatches by accident
        }
    }

}
