package com.mosaic.collections.map.inmemory;

import com.mosaic.bytes.ArrayBytes;
import com.mosaic.bytes.ByteView;
import com.mosaic.bytes.ByteSerializer;
import com.mosaic.bytes.Bytes;
import com.mosaic.collections.map.PersistableMap;
import com.mosaic.io.filesystemx.inmemory.InMemoryFile;
import com.mosaic.lang.QA;
import com.mosaic.lang.StartStopMixin;
import com.mosaic.lang.system.LogMessage;
import com.mosaic.lang.system.SystemX;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 *
 */
@SuppressWarnings("unchecked")
public class PersistableInMemoryMap<K, V extends ByteView> extends StartStopMixin<PersistableMap<K,V>>
    implements PersistableMap<K,V>
{

    private final LogMessage maxSizeWarning = new LogMessage("Persistent map '%s' has exceeded its design threshold size of '%s'.  Its current size is %s.");


    private final SystemX           system;
    private final InMemoryFile      file;

    private final ByteSerializer<K> keySerializer;

    private final long              fixedKeySize;
    private final long              fixedValueSize;
    private final long              maxEntryCount;


    private Map<K,Bytes> map;


    public PersistableInMemoryMap( SystemX system, String serviceName, InMemoryFile file, ByteSerializer<K> keySerializer, long fixedKeySize, long fixedValueSize, long maxEntryCount ) {
        super( serviceName );
        this.system = system;

        this.file           = file;
        this.keySerializer  = keySerializer;
        this.fixedKeySize   = fixedKeySize;
        this.fixedValueSize = fixedValueSize;
        this.maxEntryCount  = maxEntryCount;
    }

    public void getInto( K key, V value ) {
        throwIfNotReady();

        if ( map.size() > maxEntryCount && maxSizeWarning.getDisplayCount() != 0 ) {
            system.warn( maxSizeWarning, getServiceName(), maxEntryCount, map.size() );
        }

        assertKeySerialisationRoundTrip( key );


        Bytes bytes = map.get( key );
        if ( bytes == null ) {
            bytes = new ArrayBytes(fixedValueSize);

            map.put( key, bytes );
        }

        value.setBytes( bytes, 0, fixedValueSize );
    }

    protected void doStart() throws Exception {
        this.map = (Map<K, Bytes>) file.getAttachmentNbl();

        if ( map == null ) {
            map = new HashMap<>();

            file.setAttachmentNbl( map );
        }
    }


    protected void doStop() throws Exception {
        this.map = null;
    }



    private void assertKeySerialisationRoundTrip( K key ) {
        if ( SystemX.isDebugRun() ) {
            Bytes bytes = new ArrayBytes(fixedKeySize);
            keySerializer.encodeInto( key, bytes, 0, fixedKeySize );

            K roundTrippedKey = keySerializer.decodeFrom( bytes, 0, fixedKeySize );

            QA.isTrue( Objects.equals( key, roundTrippedKey ), "Serialisation/de-serialisation round trip failed for key '" + key + "'" );
        }
    }

}
