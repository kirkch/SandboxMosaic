package com.mosaic.columnstore.columns;

import com.mosaic.columnstore.LongColumn;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Benchmark;
import org.junit.runner.RunWith;


/**
 *
 */
@RunWith(JUnitMosaicRunner.class)
public class LongColumnFormula1Benchmark {

    private SystemX    system       = new DebugSystem();


    private int        rowCount     = 1000000;
    private LongColumn columnA      = LongColumnFormula1Test.createColumn(system, "A", rowCount);
    private LongColumn columnB      = LongColumnFormula1Test.createFormulaColumn( system, "B", columnA );
    private LongColumn columnBCache = new LongColumnArray( system, "BCache", "cache of B", rowCount );

    /**
      Serial version:     8.88ms per call
Simple Fork/Join version: 79.57ms per call

removed column resizing+simple fork/join: 3ms
tuned fork/join parameters a little:      2ms

*/

    @Benchmark( value=100 )
    public void benchmarkPrePopulateColumn() {
        columnB.prePopulateColumn( columnBCache );
    }

}
