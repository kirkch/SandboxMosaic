package com.mosaic.io.chronicle.map;

import com.mosaic.bytes.ByteView;
import com.mosaic.lang.StartStoppable;


/**
 * A hash map that is capable of storing its data to disk.
 */
public interface PersistableMap<K,V extends ByteView> extends StartStoppable<PersistableMap<K,V>> {

    /**
     * Retrieves an entry by key.  If the value does not exist, then the flyweight will point to
     * zeroed out region of Bytes.
     */
    public void getInto( K key, V value );

}
