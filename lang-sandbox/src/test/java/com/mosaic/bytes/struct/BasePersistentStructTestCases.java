package com.mosaic.bytes.struct;

import com.mosaic.bytes.struct.examples.redbull.PersistentRedBull;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.system.SystemX;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public abstract class BasePersistentStructTestCases {

    private DirectoryX        tmpDirectory;
    private FileX             dataFile;
    private PersistentRedBull bull;

    protected abstract SystemX createSystem();


    @Before
    public void setup() {
        SystemX system = createSystem();

        this.tmpDirectory = system.fileSystem.getTempDirectory().createDirectoryWithRandomName( this.getClass().getSimpleName(), ".junit" );
        this.dataFile     = tmpDirectory.getOrCreateFile( "bull.data" );
        this.bull         = new PersistentRedBull(dataFile);

        system.registerServicesAfter( bull );

        system.start();
    }

    @After
    public void tearDown() {
        createSystem().stop();

        this.tmpDirectory.deleteAll();
    }



    @Test
    public void createStruct_modifyIt_stop_expectAccessToDataToBeUnavailable() {
        bull.setHasWings( true );
        bull.setAge( 42 );
        bull.setWeight( 12.4f );

        bull.stop();

        try {
            bull.getHasWings();
            fail( "expected exception" );
        } catch ( NullPointerException ex ) {

        }
    }

    @Test
    public void createStruct_modifyIt_restart_expectContentsToStillBeAvailableAfterRestart() {
        bull.setHasWings( true );
        bull.setAge( 42 );
        bull.setWeight( 12.4f );

        bull.stop();
        bull.start();

        assertEquals( true,  bull.getHasWings() );
        assertEquals( 42,    bull.getAge() );
        assertEquals( 12.4f, bull.getWeight(), 1e-6 );
    }

}