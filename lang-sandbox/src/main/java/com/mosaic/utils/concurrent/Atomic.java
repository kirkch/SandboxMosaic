package com.mosaic.utils.concurrent;

import com.mosaic.lang.functional.Function1;

import java.util.concurrent.atomic.AtomicReference;

/**
 * A functional version of AtomicReference.
 */
public class Atomic<T> {

    private final AtomicReference<T> ref;

    public Atomic() {
        this( null );
    }

    public Atomic( T initialValue ) {
        this.ref = new AtomicReference<>(initialValue);
    }


    public T get() {
        return ref.get();
    }

    public void lazySet( T value ) {
        ref.lazySet( value );
    }

    public T update( Function1<T,T> updateOp ) {
        boolean wasSuccessful;
        T       originalValue;
        T       updatedValue;

        do {
            originalValue = get();
            updatedValue  = updateOp.invoke(originalValue);

            if ( originalValue == updatedValue ) {
                return updatedValue;
            }

            wasSuccessful = ref.compareAndSet( originalValue, updatedValue );
        } while ( !wasSuccessful );

        return updatedValue;
    }

}
