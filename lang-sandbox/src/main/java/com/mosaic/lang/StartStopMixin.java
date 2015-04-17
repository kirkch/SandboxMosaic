package com.mosaic.lang;

import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.SystemX;
import com.mosaic.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * A default implementation of StartStoppable.
 */
@SuppressWarnings("unchecked")
public abstract class StartStopMixin<T extends StartStoppable<T>> implements StartStoppable<T> {


    private List<StartStoppable> dependencies   = new ArrayList<>(3);
    private List<StartStoppable> linkedServices = new ArrayList<>(3);

    private AtomicInteger        initCounter = new AtomicInteger(0);
    private String               serviceName;

    private AtomicBoolean        isShuttingDown  = new AtomicBoolean(false);


    public StartStopMixin( String serviceName ) {
        this.serviceName = serviceName;
    }

    protected void doStart() throws Exception {};
    protected void doStop() throws Exception {};


    public final String getServiceName() {
        return serviceName;
    }


    public final T start() {
        throwIfShuttingDown();

        int initCount = initCounter.incrementAndGet();

        if ( initCount == 1 ) {
            dependencies.forEach( StartStoppable::start );

            try {
                doStart();
            } catch ( Exception ex ) {
                Backdoor.throwException( ex );
            }

            linkedServices.forEach( StartStoppable::start );
        }

        return (T) this;
    }

    public final T stop() {
        int initCount = initCounter.decrementAndGet();

        if ( initCount == 0 ) {
            this.isShuttingDown.set(true);

            ListUtils.forEachReversed( linkedServices, StartStoppable::stop );

            try {
                doStop();
            } catch ( Exception ex ) {
                throw new RuntimeException( ex );
            } finally {
                ListUtils.forEachReversed( dependencies, StartStoppable::stop );

                this.isShuttingDown.set(false);
            }
        }

        return (T) this;
    }

    public final boolean isRunning() {
        return initCounter.get() > 0;
    }

    public final boolean isShuttingDown() {
        return isShuttingDown.get();
    }

    public final T appendServicesToStartBefore( Object... otherServices ) {
        return appendOtherServices( otherServices, dependencies );
    }

    public final T appendServicesToStartAfter( Object... otherServices ) {
        return appendOtherServices( otherServices, linkedServices );
    }

    private T appendOtherServices( Object[] otherServices, List<StartStoppable> targetCollection ) {
        for ( Object o : otherServices ) {
            if ( o instanceof StartStoppable ) {
                StartStoppable ss = (StartStoppable) o;

                targetCollection.add( ss );

                if ( this.isRunning() ) {
                    ss.start();
                }
            }
        }

        return (T) this;
    }


    protected final void throwIfNotReady() {
        if ( SystemX.isDebugRun() ) {
            if ( !isRunning() ) {
                throw new IllegalStateException( "'"+serviceName+"' is not running" );
            }
        }
    }

    protected final void throwIfReady() {
        if ( SystemX.isDebugRun() ) {
            if ( isRunning() ) {
                throw new IllegalStateException( "'"+serviceName+"' is running" );
            }
        }
    }

    private final void throwIfShuttingDown() {
        if ( isShuttingDown() ) {
            throw new IllegalStateException( serviceName + " is currently in the process of shutting down" );
        }
    }

}
