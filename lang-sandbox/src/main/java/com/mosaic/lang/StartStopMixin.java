package com.mosaic.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * A default implementation of StartStoppable.
 */
@SuppressWarnings("unchecked")
public abstract class StartStopMixin<T extends StartStoppable<T>> implements StartStoppable<T> {

    /**
     * Helper class for starting a service.  Occurred because of type mangling that
     * occurred when wanting another interface to implement the StartStoppable interface
     * AND a implementation of that interface to also extend StartStopMixin.
     * Knickers + Twist -> Static fudge method.
     */
    public static void init( Object s ) {
        if ( s instanceof StartStoppable ) {
            ((StartStoppable) s).init();
        }
    }

    public static void tearDown( Object s ) {
        if ( s instanceof StartStoppable ) {
            ((StartStoppable) s).tearDown();
        }
    }


    private List<StartStoppable> chainedToOthers = new ArrayList<>(3);
    private AtomicInteger        initCounter     = new AtomicInteger(0);
    private String               serviceName;


    public StartStopMixin( String serviceName ) {
        this.serviceName = serviceName;
    }

    protected abstract void doInit();
    protected abstract void doTearDown();


    public String getServiceName() {
        return serviceName;
    }

    public T init() {
        int initCount = initCounter.incrementAndGet();

        if ( initCount == 1 ) {
            broadcastInit();
            doInit();
        }

        return (T) this;
    }

    public T tearDown() {
        int initCount = initCounter.decrementAndGet();

        if ( initCount == 0 ) {
            broadcastTearDown();
            doTearDown();
        }

        return (T) this;
    }

    public boolean isReady() {
        return initCounter.get() > 0;
    }

    public T chainTo( Object... others ) {
        for ( Object o : others ) {
            if ( o instanceof StartStoppable ) {
                chainedToOthers.add( (StartStoppable) o );
            }
        }

        return (T) this;
    }



    protected void throwIfNotReady() {
        if ( SystemX.isDebugRun() ) {
            if ( !isReady() ) {
                throw new IllegalStateException( "'"+serviceName+"' is not running" );
            }
        }
    }

    protected void throwIfReady() {
        if ( SystemX.isDebugRun() ) {
            if ( isReady() ) {
                throw new IllegalStateException( "'"+serviceName+"' is running" );
            }
        }
    }


    private void broadcastInit() {
        for ( StartStoppable o : chainedToOthers ) {
            o.init();
        }
    }

    private void broadcastTearDown() {
        for ( StartStoppable o : chainedToOthers ) {
            o.tearDown();
        }
    }

}
