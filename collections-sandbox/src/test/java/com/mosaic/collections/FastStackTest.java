package com.mosaic.collections;

import com.mosaic.hammer.junit.Benchmark;
import com.mosaic.hammer.junit.Hammer;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 *
 */

public class FastStackTest {

    private FastStack stack = new FastStack();


    @Test
    public void emptyStack_size_expectZero() {
        assertEquals( 0, stack.size() );
    }

    
}
