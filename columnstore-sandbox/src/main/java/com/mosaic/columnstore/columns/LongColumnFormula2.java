package com.mosaic.columnstore.columns;

import com.mosaic.collections.LongSet;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.LongColumn;
import com.mosaic.io.codecs.LongCodec;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.lang.QA;
import com.mosaic.utils.MapUtils;

import java.util.Map;


/**
 *
 */
public abstract class LongColumnFormula2 extends BaseLongColumn {

    private String     columnName;
    private String     description;

    private String     opName;
    private LongColumn sourceColumn1;
    private LongColumn sourceColumn2;
    private int        expectedCellCount;


    /**
     *
     * @param expectedCellCount how many source cells are probably used to calculate a single cell in this column? (hlong only)
     */
    protected LongColumnFormula2( String columnName, String description, String opName, LongColumn sourceColumn1, LongColumn sourceColumn2, int expectedCellCount ) {
        this.columnName        = columnName;
        this.description       = description;

        this.opName            = opName;
        this.sourceColumn1     = sourceColumn1;
        this.sourceColumn2     = sourceColumn2;
        this.expectedCellCount = expectedCellCount;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSet( long row ) {
        return sourceColumn1.isSet(row) || sourceColumn2.isSet(row);
    }

    public void set( long row, long value ) {
        throw new UnsupportedOperationException("derived columns do not support having their values set directly");
    }

    public void unset( long row ) {
        throw new UnsupportedOperationException( "A column should cannot be modified when generating an explanation" );
    }

    public long size() {
        return Math.max( sourceColumn1.size(), sourceColumn2.size() );
    }

    public void resizeIfNecessary( long newSize ) {
        throw new UnsupportedOperationException( "A column should cannot be modified when generating an explanation" );
    }

    public long get( long row ) {
        QA.isTrue( isSet(row), "do not call get(row) on a row that has not been set" );

        return get(row, sourceColumn1, sourceColumn2 );
    }

    public void writeValueTo( CharacterStream out, long row ) {
        if ( isSet(row) ) {
            long v = get(row);

            getCodec().encode( v, out );
        }
    }

    public LongCodec getCodec() {
        return sourceColumn1.getCodec();
    }

    public CellExplanation explain( long row ) {
        LongColumnAuditor auditor1 = new LongColumnAuditor( sourceColumn1, expectedCellCount);
        LongColumnAuditor auditor2 = new LongColumnAuditor( sourceColumn2, expectedCellCount);

        long                 value           = get( row, auditor1, auditor2 );
        LongSet             visitedRows1    = auditor1.getVisitedRows();
        LongSet             visitedRows2    = auditor2.getVisitedRows();
        Map<String,LongSet> referencedCells = MapUtils.asMap( sourceColumn1.getColumnName(), visitedRows1, sourceColumn2.getColumnName(), visitedRows2 );
        String              eqn             = toEquation( referencedCells );
        String              formattedValue  = encodeValue( value );


        return new CellExplanation( formattedValue, eqn, referencedCells );
    }

    protected abstract long get( long row, LongColumn col1, LongColumn col2 );

    private String encodeValue( long v ) {
        return getCodec().toString(v);
    }

    /**
     * Returns a formulation of the cell in the form of 'OpName(columnName [rowIds])'.  May
     * be overridden to change the format or add constants.
     */
    protected String toEquation( Map<String,LongSet> touchedCells ) {
        return ColumnUtils.formatTouchedCells( opName, touchedCells );
    }

}
