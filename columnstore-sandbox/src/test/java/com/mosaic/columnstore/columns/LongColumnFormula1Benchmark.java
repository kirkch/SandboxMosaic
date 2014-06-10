package com.mosaic.columnstore.columns;

import com.mosaic.columnstore.LongColumn;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Benchmark;
import org.junit.runner.RunWith;


/**
 *
 */
@RunWith(JUnitMosaicRunner.class)
public class LongColumnFormula1Benchmark {

    private int        rowCount     = 1000000;
    private LongColumn columnA      = LongColumnFormula1Test.createColumn("A", rowCount);
    private LongColumn columnB      = LongColumnFormula1Test.createFormulaColumn( "B", columnA );
    private LongColumn columnBCache = new LongColumnArray( "BCache", "cache of B", rowCount );

    /**
      Serial version:     8.88ms per call
Nieve Fork/Join version: 79.57ms per call

The fork/join version was much slower due to the use of a blocking synchronisation on every call to Column.set.
Without it, it still came in at 18ms.  Which was still slower than the serial version, while also giving
the wrong result.

A lockless version of LongColumn could be designed, although the mechanism to grow the size of the
column would require some thought.  However the important thing to consider is that using a non-threadsafe
column in a fork/join context was still slower.  Thus concentrating on a lockless version is not deemed
beneficial.

Conclusion:  carving up a column for processing is possible, however giving a generic solution that can
     be easily reused and maintained is challenging.  The parallelism offered by a single core is sufficient
     to give a better pay off in the majority of cases.  If parallelism is required, then it will be simpler
     to gain it by sending each column to a different core.

*/

    @Benchmark( value=100 )
    public void benchmarkPrePopulateColumn() {
        columnB.prePopulateColumn( columnBCache );
    }

}
