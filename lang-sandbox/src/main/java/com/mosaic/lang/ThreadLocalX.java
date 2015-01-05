package com.mosaic.lang;



/**
 * An enhanced extension of ThreadLocal.
 */
public class ThreadLocalX<T> extends ThreadLocal<T> {

    private volatile Factory<T> initialValueFactory;


    public ThreadLocalX() {
        this( () -> null );
    }

    public ThreadLocalX( Factory<T> initialValueFactory ) {
        QA.argNotNull( initialValueFactory, "initialValueFactory" );

        setInitialValueFactory( initialValueFactory );
    }

    protected final T initialValue() {
        return initialValueFactory.create();
    }

    public final void setInitialValueFactory( Factory<T> initialValueFactory ) {
        this.initialValueFactory = initialValueFactory;
    }

}
