package com.mosaic.io.chronicle.map;

import com.mosaic.bytes.ByteSerializers;
import com.mosaic.collections.map.inmemory.PersistableInMemoryMap;
import com.mosaic.collections.map.PersistableMap;
import com.mosaic.io.filesystemx.inmemory.InMemoryFile;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;


public class PersistableInMemoryMapTest extends BasePersistableMapTestCases {

    private SystemX system = new DebugSystem();

    protected PersistableMap<String, Account> _createPersistableMap( long fixedKeySize, long fixedValueSize, long maxEntryCount ) {
        InMemoryFile file = (InMemoryFile) system.fileSystem.getRoot().getOrCreateFile( "data.db" );

        return new PersistableInMemoryMap<>( system, "junit", file, ByteSerializers.NULL_TERMINATED_STRING_SERIALIZER, fixedKeySize, fixedValueSize, maxEntryCount );
    }

}