package com.mosaic.io.memory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.QA;


/**
 *
 */
@SuppressWarnings("unchecked")
public class FlyWeightView<T extends FlyWeight<T>> implements FlyWeight<T> {

    private FlyWeight<T> wrappedFlyweight;
    private long         fromInc;
    private long         toExc;


    public FlyWeightView( FlyWeight<T> wrappedFlyweight, long fromInc, long toExc ) {
        this.wrappedFlyweight = wrappedFlyweight;
        this.fromInc          = fromInc;
        this.toExc            = toExc;
    }


    @SuppressWarnings("CloneDoesntCallSuperClone")
    public T clone() {
        return (T) new FlyWeightView( wrappedFlyweight.clone(), fromInc, toExc );
    }

    public long getRecordCount() {
        return toExc - fromInc;
    }

    public boolean isEmpty() {
        return getRecordCount() == 0;
    }

    public long getRecordWidth() {
        return wrappedFlyweight.getRecordWidth();
    }

    public boolean hasNext() {
        return selectedIndex() < toExc;
    }

    public boolean next() {
        if ( hasNext() ) {
            wrappedFlyweight.next();

            return true;
        } else {
            return false;
        }
    }

    public long selectedIndex() {
        return wrappedFlyweight.selectedIndex()+fromInc;
    }

    public T select( long recordIndex ) {
        wrappedFlyweight.select( recordIndex + fromInc );

        return (T) this;
    }

    public long allocateNewRecords( long numElements ) {
        throw new UnsupportedOperationException( "Unable to allocate new records from a view" );
    }

    public FlyWeight<T> subview( long fromInc, long toExc ) {
        QA.argIsBetween( 0, fromInc, this.getRecordCount(), "fromInc" );
        QA.argIsBetween( 0, toExc, this.getRecordCount()+1, "toExc" );

        return new FlyWeightView( wrappedFlyweight, this.fromInc + fromInc, this.fromInc+toExc );
    }

    public void copySelectedRecordTo( long toDestinationIndex ) {
        wrappedFlyweight.copySelectedRecordTo( toDestinationIndex );
    }

    public void copySelectedRecordTo( Bytes destinationBytes, long destinationOffsetBytes ) {
        wrappedFlyweight.copySelectedRecordTo( destinationBytes, destinationOffsetBytes );
    }

    public void copySelectedRecordFrom( Bytes sourceBytes, long sourceOffsetBytes ) {
        wrappedFlyweight.copySelectedRecordFrom( sourceBytes, sourceOffsetBytes );
    }

    public void swapRecords( long recordIndex1, long recordIndex2, Bytes tmpBuffer, long tmpBufferOffset ) {
        wrappedFlyweight.swapRecords( recordIndex1+fromInc, recordIndex2+fromInc, tmpBuffer, tmpBufferOffset );
    }

    public void sort( FlyWeightComparator<T> comparator ) {
        wrappedFlyweight.sort( comparator );
    }

    public void sort( FlyWeightComparator<T> comparator, Bytes tmpBuffer, long tmpBufferOffset ) {
        wrappedFlyweight.sort( comparator, tmpBuffer, tmpBufferOffset );
    }

    public void sort( FlyWeightComparator<T> comparator, long fromInc, long toExc, Bytes tmpBuffer, long tmpBufferOffset ) {
        wrappedFlyweight.sort( comparator, fromInc, toExc, tmpBuffer, tmpBufferOffset );
    }

}
