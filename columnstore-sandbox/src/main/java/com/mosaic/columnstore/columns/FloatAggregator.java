package com.mosaic.columnstore.columns;

/**
 * An interface used to aggregate a series of float values.
 */
public interface FloatAggregator {
    public void append( float v );
    public float result();
}
