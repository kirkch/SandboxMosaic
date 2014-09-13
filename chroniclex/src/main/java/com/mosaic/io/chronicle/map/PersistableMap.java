package com.mosaic.io.chronicle.map;

import com.mosaic.bytes.ByteView;


/**
 * A hash map that is capable of storing its data to disk.
 */
public interface PersistableMap<K,V extends ByteView> {

    /**
     * Retrieves an entry by key.  If the value does not exist, then the flyweight will point to
     * zeroed out region of Bytes.
     */
    public void getInto( K key, V flyweight );

}
