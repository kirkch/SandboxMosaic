package com.mosaic.io.memory;

import com.mosaic.lang.Immutable;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.SystemX;


/**
 * Describes a range of elements within a FlyWeight data structure.
 */
@Immutable
public class FlyWeightRegion<T extends FlyWeight<T>> {

    private T    flyWeight;
    private long fromInc;
    private long toExc;


    public FlyWeightRegion( T flyWeight, long fromInc, long toExc ) {
        QA.argNotNull( flyWeight, "flyWeight" );
        QA.argIsBetween( 0, fromInc, flyWeight.getRecordCount(), "fromInc" );
        QA.argIsBetween( 0, toExc, flyWeight.getRecordCount()+1, "toExc" );

        this.flyWeight = flyWeight;
        this.fromInc   = fromInc;
        this.toExc     = toExc;
    }

    public T getFlyWeight() {
        return flyWeight;
    }

    public long getFromInc() {
        return fromInc;
    }

    public long getToExc() {
        return toExc;
    }

    public boolean isEmpty() {
        return getRecordCount() == 0;
    }

    public long getRecordCount() {
        return toExc-fromInc;
    }

    public long getSizeBytes() {
        return flyWeight.getRecordWidth()*getRecordCount();
    }

    public boolean contains( FlyWeightRegion subregion ) {
        return this.fromInc <= subregion.fromInc && this.toExc >= subregion.toExc;
    }

    public boolean fitsInCacheLine() {
        return getSizeBytes() <= SystemX.getCacheLineLengthBytes();
    }

    public String toString() {
        return String.format( "FlyWeightRegion(%s,%s,%s)",flyWeight, fromInc, toExc );
    }
}
