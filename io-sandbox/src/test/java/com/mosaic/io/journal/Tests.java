package com.mosaic.io.journal;

import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import com.softwaremosaic.junit.JUnitMosaic;
import org.junit.After;


/**
 *
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

    @After
    public void tearDown() {
        system.fileSystem.getRoot().deleteAll();
        system.stop();

        spinUntilTrue( () -> Backdoor.getActiveAllocCounter() == 0 );
        spinUntilTrue( () -> system.fileSystem.getNumberOfOpenFiles() == 0 );
    }


}
