package com.mosaic.io.chronicle.map;

import com.mosaic.bytes.ByteFlyWeight;
import com.mosaic.bytes.ByteSerializer;
import com.mosaic.bytes.ByteSerializers;


/**
 *
 */
public class PersistableInMemoryMap<K, V extends ByteFlyWeight> implements PersistableMap<K,V> {

    public PersistableInMemoryMap( Class<K> keyType, long keySize, long entrySize ) {
        this( ByteSerializers.lookup(keyType), keySize, entrySize );
    }

    public PersistableInMemoryMap( ByteSerializer<K> keySerializer, long keySize, long entrySize ) {

    }

    public void getInto( K key, V flyweight ) {

    }

}
