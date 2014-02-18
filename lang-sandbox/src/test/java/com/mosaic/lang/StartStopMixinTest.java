package com.mosaic.lang;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;


/**
 *
 */
public class StartStopMixinTest {

    @Test
    public void givenNewService_callIsRunning_expectFalse() {
        FakeService s1 = new FakeService( "s1" );

        List<String> expectedAudit = asList();

        assertFalse( s1.isRunning() );
        assertEquals( expectedAudit, s1.audit );
    }

    @Test
    public void givenNewService_callStart_expectServiceToStart() {
        FakeService s1 = new FakeService( "s1" );

        s1.start();


        List<String> expectedAudit = asList(
            "s1.doStart()"
        );

        assertTrue( s1.isRunning() );
        assertEquals( expectedAudit, s1.audit );
    }

    @Test
    public void givenNewService_callStartTwice_expectServiceToStartOnce() {
        FakeService s1 = new FakeService( "s1" );

        s1.start();
        s1.start();


        List<String> expectedAudit = asList(
            "s1.doStart()"
        );

        assertTrue( s1.isRunning() );
        assertEquals( expectedAudit, s1.audit );
    }

    @Test
    public void givenRunningService_callStop_expectServiceToShutdown() {
        FakeService s1 = new FakeService( "s1" );

        s1.start();
        s1.stop();


        List<String> expectedAudit = asList(
            "s1.doStart()",
            "s1.doStop()"
        );

        assertFalse( s1.isRunning() );
        assertEquals( expectedAudit, s1.audit );
    }

    @Test
    public void givenChainedService_callStart_expectStartupToCascade() {
        FakeService s3  = new FakeService( "s3" );
        FakeService s2a = new FakeService( "s2a", s3 );
        FakeService s2b = new FakeService( "s2b" );
        FakeService s1  = new FakeService( "s1", s2a, s2b );

        s1.start();


        assertTrue( s1.isRunning() );
        assertTrue( s2a.isRunning() );
        assertTrue( s2b.isRunning() );
        assertTrue( s3.isRunning() );

        assertEquals( asList( "s1.doStart()" ), s1.audit );
        assertEquals( asList( "s2a.doStart()" ), s2a.audit );
        assertEquals( asList( "s2b.doStart()" ), s2b.audit );
        assertEquals( asList( "s3.doStart()" ), s3.audit );
    }

    @Test
    public void givenChainOfStartedServices_callStop_expectStopToCascade() {
        FakeService s3  = new FakeService( "s3" );
        FakeService s2a = new FakeService( "s2a", s3 );
        FakeService s2b = new FakeService( "s2b" );
        FakeService s1  = new FakeService( "s1", s2a, s2b );

        s1.start();
        s1.stop();


        assertFalse( s1.isRunning() );
        assertFalse( s2a.isRunning() );
        assertFalse( s2b.isRunning() );
        assertFalse( s3.isRunning() );

        assertEquals( asList( "s1.doStart()", "s1.doStop()" ), s1.audit );
        assertEquals( asList( "s2a.doStart()", "s2a.doStop()" ), s2a.audit );
        assertEquals( asList( "s2b.doStart()", "s2b.doStop()" ), s2b.audit );
        assertEquals( asList( "s3.doStart()", "s3.doStop()" ), s3.audit );
    }

    @Test
    public void givenChainOfServices_callStartAndStopTwice_expectStartStopToOnlyBeCalledOnce() {
        FakeService s3  = new FakeService( "s3" );
        FakeService s2a = new FakeService( "s2a", s3 );
        FakeService s2b = new FakeService( "s2b" );
        FakeService s1  = new FakeService( "s1", s2a, s2b );

        s1.start();
        s1.start();
        s1.stop();
        s1.stop();


        assertFalse( s1.isRunning() );
        assertFalse( s2a.isRunning() );
        assertFalse( s2b.isRunning() );
        assertFalse( s3.isRunning() );

        assertEquals( asList( "s1.doStart()", "s1.doStop()" ), s1.audit );
        assertEquals( asList( "s2a.doStart()", "s2a.doStop()" ), s2a.audit );
        assertEquals( asList( "s2b.doStart()", "s2b.doStop()" ), s2b.audit );
        assertEquals( asList( "s3.doStart()", "s3.doStop()" ), s3.audit );
    }



    @SuppressWarnings("unchecked")
    private static class FakeService extends StartStopMixin<FakeService> {
        public List<String> audit = new ArrayList();

        public FakeService( String serviceName, Object...dependsOn ) {
            super(serviceName);

            chainTo( dependsOn );
        }


        protected void doStart() {
            audit.add( getServiceName()+".doStart()" );
        }

        protected void doStop() {
            audit.add( getServiceName()+".doStop()" );
        }
    }
}
