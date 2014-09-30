package com.mosaic.io.chronicle.map;

import com.mosaic.bytes.ByteSerializers;
import com.mosaic.collections.map.chronicle.PersistableChronicleMap;
import com.mosaic.collections.map.PersistableMap;
import com.mosaic.io.FileUtils;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import org.junit.After;

import java.io.File;


public class PersistableChronicleMapTest extends BasePersistableMapTestCases {

    private File    dir    = FileUtils.makeTempDirectory("PersistableChronicleMapTest",".junit");
    private SystemX system = DebugSystem.withActualFileSystem( dir.getAbsolutePath() );


    protected PersistableMap<String, Account> _createPersistableMap( long fixedKeySize, long fixedValueSize, long maxEntryCount ) {
        return new PersistableChronicleMap<>(
            system,
            "junit",
            new File(dir, "map.dat"),
            String.class,
            ByteSerializers.NULL_TERMINATED_STRING_SERIALIZER,
            fixedKeySize,
            fixedValueSize,
            maxEntryCount
        );
    }


    @After
    public void tearDown() {
        super.tearDown();

        FileUtils.deleteAll( dir );
    }


}