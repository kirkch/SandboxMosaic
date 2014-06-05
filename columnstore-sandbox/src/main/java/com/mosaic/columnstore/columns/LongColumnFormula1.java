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
public abstract class LongColumnFormula1 implements LongColumn {

    private String    columnName;
    private String     description;

    private String     opName;
    private LongColumn sourceColumn;
    private int        expectedCellCount;


    /**
     *
     * @param expectedCellCount how many source cells are probably used to calculate a single cell in this column? (hlong only)
     */
    protected LongColumnFormula1( String columnName, String description, String opName, LongColumn sourceColumn, int expectedCellCount ) {
        this.columnName        = columnName;
        this.description       = description;

        this.opName            = opName;
        this.sourceColumn      = sourceColumn;
        this.expectedCellCount = expectedCellCount;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSet( long row ) {
        return sourceColumn.isSet(row);
    }

    public void set( long row, long value ) {
        throw new UnsupportedOperationException("derived columns do not support having their values set directly");
    }

    public void unset( long row ) {
        throw new UnsupportedOperationException( "A column should cannot be modified when generating an explanation" );
    }

    public long rowCount() {
        return sourceColumn.rowCount();
    }

    public long get( long row ) {
        QA.isTrue( isSet(row), "do not call get(row) on a row that has not been set" );

        return get(row,sourceColumn);
    }

    public LongCodec getCodec() {
        return sourceColumn.getCodec();
    }

    public void writeValueTo( CharacterStream out, long row ) {
        if ( isSet(row) ) {
            long v = get(row);

            getCodec().encode( v, out );
        }
    }

    public CellExplanation explain( long row ) {
        LongColumnAuditor auditor = new LongColumnAuditor(sourceColumn, expectedCellCount);

        long                value           = get( row, auditor );
        LongSet             visitedRows     = auditor.getVisitedRows();
        Map<String,LongSet> referencedCells = MapUtils.asMap( sourceColumn.getColumnName(), visitedRows );
        String              eqn             = toEquation( referencedCells );
        String              formattedValue  = encodeValue( value );


        return new CellExplanation( formattedValue, eqn, referencedCells );
    }

    protected abstract long get( long row, LongColumn col );

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
