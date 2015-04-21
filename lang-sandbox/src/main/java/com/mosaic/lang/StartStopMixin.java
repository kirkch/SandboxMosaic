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


    private List<StartStoppable> servicesBefore = new ArrayList<>(3);
    private List<StartStoppable> servicesAfter  = new ArrayList<>(3);

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
            servicesBefore.forEach( StartStoppable::start );

            try {
                doStart();
            } catch ( Exception ex ) {
                Backdoor.throwException( ex );
            }

            servicesAfter.forEach( StartStoppable::start );
        }

        return (T) this;
    }

    public final T stop() {
        int initCount = initCounter.decrementAndGet();

        if ( initCount == 0 ) {
            this.isShuttingDown.set(true);

            ListUtils.forEachReversed( servicesAfter, StartStoppable::stop );

            try {
                doStop();
            } catch ( Exception ex ) {
                throw new RuntimeException( ex );
            } finally {
                ListUtils.forEachReversed( servicesBefore, StartStoppable::stop );

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


    public Subscription registerServicesBefore( StartStoppable... otherServices ) {
        return appendServices( otherServices, servicesBefore );
    }

    public Subscription registerServicesAfter( StartStoppable... otherServices ) {
        return appendServices( otherServices, servicesAfter );
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


    private Subscription appendServices( StartStoppable[] otherServices, List<StartStoppable> targetCollection ) {
        QA.argHasNoNullElements( otherServices, "otherServices" );

        Subscription compositeSub = null;
        for ( StartStoppable newService : otherServices ) {
            Subscription sub = new Subscription( () -> targetCollection.remove(newService) );

            compositeSub = sub.and(compositeSub);

            targetCollection.add( newService );

            if ( this.isRunning() ) {
                newService.start();
            }
        }

        return compositeSub;
    }

}
