package com.mosaic.lang.system;

import org.junit.Test;

import static org.junit.Assert.*;


public class SystemXTest {

    @Test
    public void retrievePID() {
        SystemX system = new DebugSystem();

        assertTrue( system.getProcessId() > 0 );
    }

    @Test
    public void retrievePIDTwice_expectSameValueEachTime() {
        SystemX system = new DebugSystem();

        int pid1 = system.getProcessId();
        int pid2 = system.getProcessId();

        assertEquals( pid1, pid2 );
    }

}