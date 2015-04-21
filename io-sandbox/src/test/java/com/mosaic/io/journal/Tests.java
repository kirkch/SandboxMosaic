package com.mosaic.io.journal;

import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import com.softwaremosaic.junit.JUnitMosaic;
import org.junit.After;
import org.junit.Before;


/**
 * Convenience base class for tests that make use of SystemX.
 */
public class Tests extends JUnitMosaic {

    protected final SystemX    system  = createSystem();
    protected final DirectoryX dataDir = system.fileSystem.getCurrentWorkingDirectory().getOrCreateDirectory( "data" );


    /**
     * Override to customise the runtime environment, for example change the default in memory DebugSystem
     * with one that uses the file system.
     */
    protected SystemX createSystem() {
        return new DebugSystem();
    }

    @Before
    public void setUp() {
        system.start();
    }

    @After
    public void tearDown() {
        system.stop();
        system.fileSystem.getRoot().deleteAll();

        spinUntilTrue( () -> Backdoor.getActiveAllocCounter() == 0 );
        spinUntilTrue( "files left open: "+system.fileSystem.getNumberOfOpenFiles(), () -> system.fileSystem.getNumberOfOpenFiles() == 0 );
    }


}
