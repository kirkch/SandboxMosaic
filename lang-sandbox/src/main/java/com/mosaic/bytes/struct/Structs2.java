package com.mosaic.bytes.struct;

import com.mosaic.bytes.ByteView;
import com.mosaic.bytes.Bytes;

import java.util.Comparator;


/**
 * A collection of fixed size structs.
 */
public interface Structs2<T extends ByteView> extends Iterable<T> {

    public long numRecords();

    public void selectInto( long index, T view );
    public T select( long index );

    public long allocateNewRecord();
    public long allocateNewRecord( long numRecords );


    public void clearAll();
    public void sort( Comparator<T> comparator );

    public void swapRecords( Bytes tmpBytes, long index1, long index2 );

}
