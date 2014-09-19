package com.mosaic.columnstore;

import com.mosaic.io.streams.UTF8Builder;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
@SuppressWarnings("unchecked")
public class ColumnsTest {

    private SystemX system = new DebugSystem();


    @Test
    public void rowCount() {
        IntColumn    col1 = Columns.newIntColumn( system, "col1", "desc1", 3, 2, 10 );
        FloatColumn  col2 = Columns.newFloatColumn( system, "col2", "desc2", 3.1f, 2.2f, 10.3f, 1.4f );
        ObjectColumn col3 = Columns.newObjectColumn( system, "col3", "desc3", "red", "green", "blue" );

        Columns columns = new Columns(col1,col2,col3);

        assertEquals( 4, columns.rowCount() );
    }

    @Test
    public void writeAsCSVTo_noRows() {
        IntColumn    col1 = Columns.newIntColumn( system, "col1", "desc1" );
        FloatColumn  col2 = Columns.newFloatColumn( system, "col2", "desc2" );
        ObjectColumn col3 = Columns.newObjectColumn( system, "col3", "desc3" );

        Columns columns = new Columns(col1,col2,col3);

        UTF8Builder builder = new UTF8Builder(system);

        columns.writeAsCSVTo( builder );

        assertEquals( "rowId, col1, col2, col3\n", builder.toString() );
    }

    @Test
    public void writeAsCSVTo_severalRowsWithGaps() {
        IntColumn    col1 = Columns.newIntColumn( system, "col1", "desc1", 3, 2, 10 );
        FloatColumn  col2 = Columns.newFloatColumn( system, "col2", "desc2", 3.1f, 2.2f, 10.3f, 1.4f );
        ObjectColumn col3 = Columns.newObjectColumn( system, "col3", "desc3", "red", "green", "blue" );

        Columns columns = new Columns(col1,col2,col3);

        UTF8Builder builder = new UTF8Builder(system);

        columns.writeAsCSVTo( builder );

        assertEquals(
            "rowId, col1, col2, col3\n"+
            "0, 3, 3.10, red\n"+
            "1, 2, 2.20, green\n"+
            "2, 10, 10.30, blue\n"+
            "3, , 1.40, \n",
            builder.toString()
        );
    }

    @Test
    public void writeAsCSVTo_makeSureThatBlankRowsAreSkipped() {
        IntColumn    col1 = Columns.newIntColumn( system, "col1", "desc1", 3, 2, 10 );
        FloatColumn  col2 = Columns.newFloatColumn( system, "col2", "desc2", 3.1f, 2.2f, 10.3f, 1.4f );
        ObjectColumn col3 = Columns.newObjectColumn( system, "col3", "desc3", "red", "green", "blue" );

        col1.unset(0);
        col2.unset(0);
        col3.unset(0);

        Columns columns = new Columns(col1,col2,col3);

        UTF8Builder builder = new UTF8Builder(system);

        columns.writeAsCSVTo( builder );

        assertEquals(
            "rowId, col1, col2, col3\n"+
            "1, 2, 2.20, green\n"+
            "2, 10, 10.30, blue\n"+
            "3, , 1.40, \n",
            builder.toString()
        );
    }

}
