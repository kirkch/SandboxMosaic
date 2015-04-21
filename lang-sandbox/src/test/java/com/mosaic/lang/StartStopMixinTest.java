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

    private final List<String> audit = new ArrayList<>();


    @Test
    public void givenNewService_callIsRunning_expectFalse() {
        FakeService s1 = new FakeService( "s1" );

        List<String> expectedAudit = asList();

        assertFalse( s1.isRunning() );
        assertEquals( expectedAudit, audit );
    }

    @Test
    public void givenNewService_callStart_expectServiceToStart() {
        FakeService s1 = new FakeService( "s1" );

        s1.start();


        List<String> expectedAudit = asList(
            "s1.doStart()"
        );

        assertTrue( s1.isRunning() );
        assertEquals( expectedAudit, audit );
    }

    @Test
    public void givenNewService_callStartTwice_expectServiceToStartOnce() {
        FakeService s1 = new FakeService( "s1" );

        s1.start();
        s1.start();


        List<String> expectedAudit = asList("s1.doStart()");

        assertTrue( s1.isRunning() );
        assertEquals( expectedAudit, audit );
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
        assertEquals( expectedAudit, audit );
    }

    @Test
    public void givenDependentService_callStart_expectStartupToCascade() {
        FakeService s4 = new FakeService( "s4" );
        FakeService s3 = new FakeService( "s3" );
        FakeService s2 = new FakeService( "s2" );
        FakeService s1 = new FakeService( "s1" );

        s4.registerServicesBefore( s2, s3 );
        s2.registerServicesBefore( s1 );


        s4.start();


        assertTrue( s1.isRunning() );
        assertTrue( s2.isRunning() );
        assertTrue( s3.isRunning() );
        assertTrue( s4.isRunning() );

        List<String> expectedAudit = asList(
            "s1.doStart()",
            "s2.doStart()",
            "s3.doStart()",
            "s4.doStart()"
        );

        assertEquals( expectedAudit, audit );
    }

    @Test
    public void givenDependentServices_callStop_expectStopToCascade() {
        FakeService s4 = new FakeService( "s4" );
        FakeService s3 = new FakeService( "s3" );
        FakeService s2 = new FakeService( "s2" );
        FakeService s1 = new FakeService( "s1" );

        s4.registerServicesBefore( s2, s3 );
        s2.registerServicesBefore( s1 );


        s4.start();
        s4.stop();


        assertFalse( s1.isRunning() );
        assertFalse( s2.isRunning() );
        assertFalse( s3.isRunning() );
        assertFalse( s4.isRunning() );

        List<String> expectedAudit = asList(
            "s1.doStart()",
            "s2.doStart()",
            "s3.doStart()",
            "s4.doStart()",
            "s4.doStop()",
            "s3.doStop()",
            "s2.doStop()",
            "s1.doStop()"
        );

        assertEquals( expectedAudit, audit );
    }

    @Test
    public void givenDependentServices_callStartAndStopTwice_expectStartStopToOnlyBeCalledOnce() {
        FakeService s4 = new FakeService( "s4" );
        FakeService s3 = new FakeService( "s3" );
        FakeService s2 = new FakeService( "s2" );
        FakeService s1 = new FakeService( "s1" );

        s4.registerServicesBefore( s2, s3 );
        s2.registerServicesBefore( s1 );


        s4.start();
        s4.start();
        s4.stop();
        s4.stop();


        assertFalse( s1.isRunning() );
        assertFalse( s2.isRunning() );
        assertFalse( s3.isRunning() );
        assertFalse( s4.isRunning() );

        List<String> expectedAudit = asList(
            "s1.doStart()",
            "s2.doStart()",
            "s3.doStart()",
            "s4.doStart()",
            "s4.doStop()",
            "s3.doStop()",
            "s2.doStop()",
            "s1.doStop()"
        );

        assertEquals( expectedAudit, audit );
    }

    @Test
    public void givenChainedService_callStart_expectStartupToCascade() {
        FakeService s4 = new FakeService( "s4" );
        FakeService s3 = new FakeService( "s3" );
        FakeService s2 = new FakeService( "s2" );
        FakeService s1 = new FakeService( "s1" );

        s1.registerServicesAfter( s2, s4 );
        s2.registerServicesAfter( s3 );


        s1.start();


        assertTrue( s1.isRunning() );
        assertTrue( s2.isRunning() );
        assertTrue( s3.isRunning() );
        assertTrue( s4.isRunning() );

        List<String> expectedAudit = asList(
            "s1.doStart()",
            "s2.doStart()",
            "s3.doStart()",
            "s4.doStart()"
        );

        assertEquals( expectedAudit, audit );
    }

    @Test
    public void givenChainedServices_callStop_expectStopToCascade() {
        FakeService s4 = new FakeService( "s4" );
        FakeService s3 = new FakeService( "s3" );
        FakeService s2 = new FakeService( "s2" );
        FakeService s1 = new FakeService( "s1" );

        s1.registerServicesAfter( s2, s4 );
        s2.registerServicesAfter( s3 );


        s1.start();
        s1.stop();


        assertFalse( s1.isRunning() );
        assertFalse( s2.isRunning() );
        assertFalse( s3.isRunning() );
        assertFalse( s4.isRunning() );

        List<String> expectedAudit = asList(
            "s1.doStart()",
            "s2.doStart()",
            "s3.doStart()",
            "s4.doStart()",
            "s4.doStop()",
            "s3.doStop()",
            "s2.doStop()",
            "s1.doStop()"
        );

        assertEquals( expectedAudit, audit );
    }

    @Test
    public void givenChainedServices_callStartAndStopTwice_expectStartStopToOnlyBeCalledOnce() {
        FakeService s4 = new FakeService( "s4" );
        FakeService s3 = new FakeService( "s3" );
        FakeService s2 = new FakeService( "s2" );
        FakeService s1 = new FakeService( "s1" );

        s1.registerServicesAfter( s2, s4 );
        s2.registerServicesAfter( s3 );


        s1.start();
        s1.start();
        s1.stop();
        s1.stop();


        assertFalse( s1.isRunning() );
        assertFalse( s2.isRunning() );
        assertFalse( s3.isRunning() );
        assertFalse( s4.isRunning() );

        List<String> expectedAudit = asList(
            "s1.doStart()",
            "s2.doStart()",
            "s3.doStart()",
            "s4.doStart()",
            "s4.doStop()",
            "s3.doStop()",
            "s2.doStop()",
            "s1.doStop()"
        );

        assertEquals( expectedAudit, audit );
    }



    @SuppressWarnings("unchecked")
    private class FakeService extends StartStopMixin<FakeService> {
        public FakeService( String serviceName ) {
            super(serviceName);
        }


        protected void doStart() {
            audit.add( getServiceName()+".doStart()" );
        }

        protected void doStop() {
            audit.add( getServiceName()+".doStop()" );
        }
    }
}
