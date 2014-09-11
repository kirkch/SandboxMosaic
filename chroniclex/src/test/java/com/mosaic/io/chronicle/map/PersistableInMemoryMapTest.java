package com.mosaic.io.chronicle.map;

import com.mosaic.bytes.ByteSerializers;


public class PersistableInMemoryMapTest extends BasePersistableMapTestCases {

    protected PersistableMap<String, Account> createPersistableMap( long keySize, long entrySize ) {
        return new PersistableInMemoryMap<>( ByteSerializers.NULL_TERMINATED_STRING_SERIALIZER, keySize, entrySize );
    }

}