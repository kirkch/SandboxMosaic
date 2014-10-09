package com.mosaic.lang;

import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.SystemX;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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
            ((StartStoppable) s).start();
        }
    }

    public static void tearDown( Object s ) {
        if ( s instanceof StartStoppable ) {
            ((StartStoppable) s).stop();
        }
    }


    private List<StartStoppable> chainedToOthers = new ArrayList<>(3);
    private AtomicInteger        initCounter     = new AtomicInteger(0);
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

    // todo improve the error handling of Exceptions

    public final T start() {
        throwIfShuttingDown();

        int initCount = initCounter.incrementAndGet();

        if ( initCount == 1 ) {
            broadcastInit();

            try {
                doStart();
            } catch ( Exception ex ) {
                Backdoor.throwException( ex );
            }
        }

        return (T) this;
    }

    public final T stop() {
        int initCount = initCounter.decrementAndGet();

        if ( initCount == 0 ) {
            this.isShuttingDown.set(true);

            broadcastTearDown();

            try {
                doStop();
            } catch ( Exception ex ) {
                throw new RuntimeException( ex );
            } finally {
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

    /**
     * Connect the life cycles of this service with the specified others.  Thus when this service
     * starts, the other services will be started first and when this service is stopped then the
     * other services will be stopped first.
     */
    public final T serviceDependsUpon( Object... otherServices ) {
        for ( Object o : otherServices ) {
            if ( o instanceof StartStoppable ) {
                StartStoppable ss = (StartStoppable) o;

                chainedToOthers.add( ss );

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

    private final void broadcastInit() {
        for ( StartStoppable o : chainedToOthers ) {
            o.start();
        }
    }


    private final void broadcastTearDown() {
        for ( StartStoppable o : chainedToOthers ) {
            o.stop();
        }
    }

    private final void throwIfShuttingDown() {
        if ( isShuttingDown() ) {
            throw new IllegalStateException( serviceName + " is currently in the process of shutting down" );
        }
    }

}
