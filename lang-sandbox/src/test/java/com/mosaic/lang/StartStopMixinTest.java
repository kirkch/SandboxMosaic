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

        assertFalse( s1.isReady() );
        assertEquals( expectedAudit, s1.audit );
    }

    @Test
    public void givenNewService_callInit_expectServiceToStart() {
        FakeService s1 = new FakeService( "s1" );

        s1.init();


        List<String> expectedAudit = asList(
            "s1.doInit()"
        );

        assertTrue( s1.isReady() );
        assertEquals( expectedAudit, s1.audit );
    }

    @Test
    public void givenNewService_callInitTwice_expectServiceToStartOnce() {
        FakeService s1 = new FakeService( "s1" );

        s1.init();
        s1.init();


        List<String> expectedAudit = asList(
            "s1.doInit()"
        );

        assertTrue( s1.isReady() );
        assertEquals( expectedAudit, s1.audit );
    }

    @Test
    public void givenRunningService_callTearDown_expectServiceToShutdown() {
        FakeService s1 = new FakeService( "s1" );

        s1.init();
        s1.tearDown();


        List<String> expectedAudit = asList(
            "s1.doInit()",
            "s1.doTearDown()"
        );

        assertFalse( s1.isReady() );
        assertEquals( expectedAudit, s1.audit );
    }

    @Test
    public void givenChainedService_callInit_expectStartupToCascade() {
        FakeService s3  = new FakeService( "s3" );
        FakeService s2a = new FakeService( "s2a", s3 );
        FakeService s2b = new FakeService( "s2b" );
        FakeService s1  = new FakeService( "s1", s2a, s2b );

        s1.init();


        assertTrue( s1.isReady() );
        assertTrue( s2a.isReady() );
        assertTrue( s2b.isReady() );
        assertTrue( s3.isReady() );

        assertEquals( asList( "s1.doInit()" ), s1.audit );
        assertEquals( asList( "s2a.doInit()" ), s2a.audit );
        assertEquals( asList( "s2b.doInit()" ), s2b.audit );
        assertEquals( asList( "s3.doInit()" ), s3.audit );
    }

    @Test
    public void givenChainOfStartedServices_callTearDown_expectTearDownToCascade() {
        FakeService s3  = new FakeService( "s3" );
        FakeService s2a = new FakeService( "s2a", s3 );
        FakeService s2b = new FakeService( "s2b" );
        FakeService s1  = new FakeService( "s1", s2a, s2b );

        s1.init();
        s1.tearDown();


        assertFalse( s1.isReady() );
        assertFalse( s2a.isReady() );
        assertFalse( s2b.isReady() );
        assertFalse( s3.isReady() );

        assertEquals( asList( "s1.doInit()", "s1.doTearDown()" ), s1.audit );
        assertEquals( asList( "s2a.doInit()", "s2a.doTearDown()" ), s2a.audit );
        assertEquals( asList( "s2b.doInit()", "s2b.doTearDown()" ), s2b.audit );
        assertEquals( asList( "s3.doInit()", "s3.doTearDown()" ), s3.audit );
    }

    @Test
    public void givenChainOfServices_callInitAndTearDownTwice_expectStartStopToOnlyBeCalledOnce() {
        FakeService s3  = new FakeService( "s3" );
        FakeService s2a = new FakeService( "s2a", s3 );
        FakeService s2b = new FakeService( "s2b" );
        FakeService s1  = new FakeService( "s1", s2a, s2b );

        s1.init();
        s1.init();
        s1.tearDown();
        s1.tearDown();


        assertFalse( s1.isReady() );
        assertFalse( s2a.isReady() );
        assertFalse( s2b.isReady() );
        assertFalse( s3.isReady() );

        assertEquals( asList( "s1.doInit()", "s1.doTearDown()" ), s1.audit );
        assertEquals( asList( "s2a.doInit()", "s2a.doTearDown()" ), s2a.audit );
        assertEquals( asList( "s2b.doInit()", "s2b.doTearDown()" ), s2b.audit );
        assertEquals( asList( "s3.doInit()", "s3.doTearDown()" ), s3.audit );
    }



    @SuppressWarnings("unchecked")
    private static class FakeService extends StartStopMixin<FakeService> {
        public List<String> audit = new ArrayList();

        public FakeService( String serviceName, Object...dependsOn ) {
            super(serviceName);

            chainTo( dependsOn );
        }


        protected void doInit() {
            audit.add( getServiceName()+".doInit()" );
        }

        protected void doTearDown() {
            audit.add( getServiceName()+".doTearDown()" );
        }
    }
}
