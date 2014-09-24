package com.mosaic.io.chronicle.map;

import com.mosaic.bytes.ByteSerializer;
import com.mosaic.bytes.ByteView;
import com.mosaic.bytes.Bytes;
import com.mosaic.lang.QA;
import com.mosaic.lang.StartStopMixin;
import com.mosaic.lang.system.SystemX;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import net.openhft.collections.SharedMapErrorListener;
import net.openhft.lang.io.serialization.ObjectSerializer;
import net.openhft.lang.model.Byteable;

import java.io.File;
import java.io.IOException;

import static com.mosaic.lang.system.Backdoor.toInt;


/**
 * An implementation of PersistableMap that uses ChronicleMap as its implementation.
 */
@SuppressWarnings("unchecked")
public class PersistableChronicleMap<K, V extends ByteView> extends StartStopMixin<PersistableMap<K,V>>
    implements PersistableMap<K,V>
{
    private final SystemX           system;
    private final File              file;

    private final Class<K>          keyClass;
    private final ByteSerializer<K> keySerializer;

    private final long              fixedKeySize;
    private final int               fixedValueSize;
    private final long              maxEntryCount;


    private ChronicleMap<K,BytableValue> map;


    public PersistableChronicleMap(
        SystemX           system,
        String            serviceName,
        File              file,
        Class<K>          keyClass,
        ByteSerializer<K> keySerializer,
        long              fixedKeySize,
        long              fixedValueSize,
        long              maxEntryCount
    ) {
        super( serviceName );

        this.system         = system;

        this.file           = file;
        this.keyClass       = keyClass;
        this.keySerializer  = keySerializer;
        this.fixedKeySize   = fixedKeySize;
        this.fixedValueSize = toInt(fixedValueSize);
        this.maxEntryCount  = maxEntryCount;
    }

    public void getInto( K key, V value ) {
        if ( map == null ) {
            throw new IllegalStateException( "'"+getServiceName()+"' is not running" );
        }

        map.acquireUsing( key, new BytableValue(value) );
    }


    protected void doStart() throws Exception {
        this.map = (ChronicleMap<K,BytableValue>) ChronicleMapBuilder.of( keyClass, BytableValue.class )
            .file( file )
            .objectSerializer(
                new ObjectSerializer() {
                    // only used to serialize keys; from that point on the keys are treated as raw bytes and never unmarsheled back
                    public void writeSerializable( net.openhft.lang.io.Bytes bytes, Object o, Class aClass ) throws IOException {
                        long pos = bytes.position();

                        QA.isTrue( aClass == keyClass, "unexpected type" );

                        Bytes b = new ChronicleBytesAdapter( bytes, pos, pos + fixedKeySize );

                        K key = (K) o;
                        keySerializer.encodeInto( key, b, 0, fixedKeySize );
                        bytes.position( pos + fixedKeySize );
                    }

                    // We use Byteable values; which means that this method will not be used
                    public <T> T readSerializable( net.openhft.lang.io.Bytes bytes, Class<T> aClass, T t ) throws IOException, ClassNotFoundException {
                        throw new UnsupportedOperationException();
                    }
                }
            )
            .entrySize( fixedValueSize )
            .entries( maxEntryCount )
            .transactional( false )
            .canReplicate( false )
            .errorListener(
                new SharedMapErrorListener() {
                    public void onLockTimeout( long l ) throws IllegalStateException {
                        system.opsAudit( "Chronicle map '%s' reported onLockTimeout %s", getServiceName(), l );
                    }

                    public void errorOnUnlock( IllegalMonitorStateException e ) {
                        system.opsAudit( e, "Chronicle map '%s' reported errorOnUnlock %s", getServiceName() );
                    }
                }
            ).create();
    }

    protected void doStop() throws Exception {
        this.map.close();

        this.map = null;
    }


    /**
     * Converts V into an HFT Byteable; telling Chronicle Map to share the underlying bytes
     * without having to copy them.
     */
    private class BytableValue implements Byteable {
        private net.openhft.lang.io.Bytes hftBytes;

        private V     value;
        private Bytes valueBytes;
        private long  offset;

        public BytableValue( V value ) {
            this.value = value;
        }

        public void bytes( net.openhft.lang.io.Bytes bytes, long offset ) {
            this.hftBytes   = bytes;
            this.valueBytes = new ChronicleBytesAdapter( bytes, offset, offset + fixedValueSize );
            this.offset     = offset;

            this.value.setBytes( valueBytes, 0, fixedValueSize );
        }

        public net.openhft.lang.io.Bytes bytes() {
            return hftBytes;
        }

        public long offset() {
            return offset;
        }

        public int maxSize() {
            return fixedValueSize;
        }
    }

}
