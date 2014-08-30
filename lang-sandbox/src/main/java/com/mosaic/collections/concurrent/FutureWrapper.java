package com.mosaic.collections.concurrent;

import com.mosaic.lang.Failure;
import com.mosaic.lang.QA;
import com.mosaic.lang.functional.CompletedCallback;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.Try;
import com.mosaic.lang.functional.VoidFunction1;


/**
 * A mixin trait that supports classes that want to add fields to a Future while still looking
 * like a future itself.  The wrapping class contains a future, and implements this adaptor that
 * redirects all future like calls to the underlying future.
 */
public abstract class FutureWrapper<T> {

    private final Future<T> future;

    protected FutureWrapper( Future<T> future ) {
        QA.argNotNull( future, "future" );

        this.future = future;
    }

    public Future<T> getFuture() {
        return future;
    }

    public boolean isComplete() {
        return future.isComplete();
    }

    public Future<T> spinUntilComplete() {
        return future.spinUntilComplete();
    }

    public Future<T> spinUntilComplete( long maxWaitMillis ) {
        return future.spinUntilComplete( maxWaitMillis );
    }

    public boolean hasResult() {
        return future.hasResult();
    }

    public boolean hasFailure() {
        return future.hasFailure();
    }

    public T getResultNoBlock() {
        return future.getResultNoBlock();
    }

    public Failure getFailureNoBlock() {
        return future.getFailureNoBlock();
    }

    public boolean completeWithResult( T result ) {
        return future.completeWithResult( result );
    }

    public boolean completeWithFailure( Failure f ) {
        return future.completeWithFailure( f );
    }

    public void completeWithTry(Try<T> otherFuture) {
        future.completeWithTry( otherFuture );
    }

    public <B> Future<B> mapResult( final Function1<T,B> mappingFunction ) {
        return future.mapResult( mappingFunction );
    }

    public <B> Future<B> flatMapResult( final Function1<T,Try<B>> mappingFunction ) {
        return future.flatMapResult( mappingFunction );
    }

    public Future<T> recover( final Function1<Failure,T> recoveryFunction ) {
        return future.recover( recoveryFunction );
    }

    public Future<T> flatRecover( final Function1<Failure,Try<T>> recoveryFunction ) {
        return future.flatRecover( recoveryFunction );
    }

    public Future<T> mapFailure( final Function1<Failure,Failure> mappingFunction ) {
        return future.mapFailure( mappingFunction );
    }

    public Future<T> flatMapFailure( final Function1<Failure,Try<Failure>> mappingFunction ) {
        return future.flatMapFailure( mappingFunction );
    }

    public void onResult( final VoidFunction1<T> callback ) {
        future.onResult( callback );
    }

    public void onFailure( final VoidFunction1<Failure> callback ) {
        future.onFailure( callback );
    }

    public void onComplete( CompletedCallback<T> callback ) {
        future.onComplete( callback );
    }

    public FutureNbl<T> toTryNbl() {
        return future.toTryNbl();
    }

}
