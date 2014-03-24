package com.mosaic.io.memory;

import com.mosaic.lang.ComparisonResult;


/**
 *
 */
public interface FlyWeightComparator<T extends FlyWeight<T>> {

    public ComparisonResult compare( T flyweight, long record1, long record2 );

}