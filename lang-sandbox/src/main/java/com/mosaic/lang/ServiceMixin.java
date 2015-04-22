package com.mosaic.lang;

import com.mosaic.lang.functional.VoidFunction0;
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
public abstract class ServiceMixin<T extends Service<T>> implements Service<T> {


    private List<Service> servicesBefore = new ArrayList<>(3);
    private List<Service> servicesAfter  = new ArrayList<>(3);

    private VoidFunction0 onStartBeforeFunc = VoidFunction0.NO_OP;
    private VoidFunction0 onStartAfterFunc  = VoidFunction0.NO_OP;
    private VoidFunction0 onStopBeforeFunc  = VoidFunction0.NO_OP;
    private VoidFunction0 onStopAfterFunc   = VoidFunction0.NO_OP;

    private AtomicInteger        initCounter = new AtomicInteger(0);
    private String               serviceName;

    private AtomicBoolean        isShuttingDown  = new AtomicBoolean(false);


    public ServiceMixin( String serviceName ) {
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
            servicesBefore.forEach( Service::start );

            try {
                onStartBeforeFunc.invoke();

                doStart();

                onStartAfterFunc.invoke();
            } catch ( Exception ex ) {
                Backdoor.throwException( ex );
            }

            servicesAfter.forEach( Service::start );
        }

        return (T) this;
    }

    public final T stop() {
        int initCount = initCounter.decrementAndGet();

        if ( initCount == 0 ) {
            this.isShuttingDown.set(true);

            ListUtils.forEachReversed( servicesAfter, Service::stop );

            try {
                onStopBeforeFunc.invoke();

                doStop();

                onStopAfterFunc.invoke();
            } catch ( Exception ex ) {
                Backdoor.throwException( ex );
            } finally {
                ListUtils.forEachReversed( servicesBefore, Service::stop );

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


    public Subscription registerServicesBefore( Service... otherServices ) {
        return appendServices( otherServices, servicesBefore );
    }

    public Subscription registerServicesAfter( Service... otherServices ) {
        return appendServices( otherServices, servicesAfter );
    }

    public Service<T> onStartBefore( VoidFunction0 callback ) {
        onStartBeforeFunc = onStartBeforeFunc.and( callback );

        return this;
    }

    public Service<T> onStartAfter( VoidFunction0 callback ) {
        onStartAfterFunc = onStartAfterFunc.and( callback );

        return this;
    }

    public Service<T> onStopBefore( VoidFunction0 callback ) {
        onStopBeforeFunc = onStopBeforeFunc.and( callback );

        return this;
    }

    public Service<T> onStopAfter( VoidFunction0 callback ) {
        onStopAfterFunc = onStopAfterFunc.and( callback );

        return this;
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


    private Subscription appendServices( Service[] otherServices, List<Service> targetCollection ) {
        QA.argHasNoNullElements( otherServices, "otherServices" );

        Subscription compositeSub = null;
        for ( Service newService : otherServices ) {
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
