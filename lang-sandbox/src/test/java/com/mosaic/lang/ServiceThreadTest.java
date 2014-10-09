package com.mosaic.lang;

import com.softwaremosaic.junit.JUnitMosaic;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class ServiceThreadTest {


    @Test
    public void startAndStopAService() {
        String serviceName = "ThreadedServiceTest.startAndStopAService";

        final MyServiceThread service = new MyServiceThread( serviceName );

        assertEquals( 0, service.workCounter.get() );

        service.start();

        JUnitMosaic.spinUntilTrue( new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return service.workCounter.get() > 0;
            }
        } );

        service.stop();

        JUnitMosaic.spinUntilAllThreadsComplete( serviceName );
    }

    @Test
    public void givenAThreadThatGoesToSleepForever_callStop_expectTheThreadToStopRegardless() {
        String serviceName = "ThreadedServiceTest.givenAThreadThatGoesToSleepForever_callStop_expectTheThreadToStopRegardless";

        final SleepyServiceThread service = new SleepyServiceThread( serviceName );

        assertEquals( 0, service.workCounter.get() );

        service.start();

        JUnitMosaic.spinUntilTrue( new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return service.workCounter.get() > 0;
            }
        } );

        service.stop();

        JUnitMosaic.spinUntilAllThreadsComplete( serviceName );
    }





    private static class MyServiceThread extends ServiceThread<MyServiceThread> {
        public final AtomicLong workCounter = new AtomicLong( 0L );

        public MyServiceThread( String serviceName ) {
            super( serviceName, ThreadType.DAEMON );
        }

        protected long loop() throws InterruptedException {
            workCounter.incrementAndGet();

            return 10;
        }
    }

    private static class SleepyServiceThread extends ServiceThread<MyServiceThread> {
        public final AtomicLong workCounter = new AtomicLong( 0L );

        public SleepyServiceThread( String serviceName ) {
            super( serviceName, ThreadType.DAEMON );
        }

        protected long loop() throws InterruptedException {
            while ( true ) {
                workCounter.incrementAndGet();

                Thread.sleep(1);
            }
        }
    }

}
