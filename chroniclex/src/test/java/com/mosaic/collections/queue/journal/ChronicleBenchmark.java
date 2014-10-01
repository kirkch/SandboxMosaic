package com.mosaic.collections.queue.journal;

import com.mosaic.io.FileUtils;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Benchmark;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.VanillaChronicle;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;


/**
 *
 */
//@RunWith(JUnitMosaicRunner.class)
public class ChronicleBenchmark {

    private File dir = FileUtils.makeTempDirectory( "benchmark_", "_tmp" );
    private VanillaChronicle chronicle;

    @Before
    public void setup() {
        chronicle = new VanillaChronicle(dir.getAbsolutePath()+"/foo");
    }

    @After
    public void tearDown() {
        FileUtils.deleteAll(dir);
    }

    // 220ms per million
    @Benchmark( value=3, batchCount=6, units="per million 24 byte messages" )
    public void b() throws IOException {
        ExcerptAppender out = chronicle.createAppender();

        for ( int i=0; i<1_000_000; i++) {
            out.startExcerpt( 24 );
            out.writeLong( i );
            out.writeLong( i );
            out.writeDouble( i );
            out.finish();
        }
    }
}
