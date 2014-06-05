package com.mosaic.columnstore.columns;

import com.mosaic.collections.LongSet;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.IntColumn;
import com.mosaic.io.codecs.IntCodec;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.lang.QA;
import com.mosaic.utils.MapUtils;

import java.util.Map;


/**
 *
 */
public abstract class IntColumnFormula1 implements IntColumn {

    private String    columnName;
    private String    description;

    private String    opName;
    private IntColumn sourceColumn;
    private int       expectedCellCount;


    /**
     *
     * @param expectedCellCount how many source cells are probably used to calculate a single cell in this column? (hint only)
     */
    protected IntColumnFormula1( String columnName, String description, String opName, IntColumn sourceColumn, int expectedCellCount ) {
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

    public void set( long row, int value ) {
        throw new UnsupportedOperationException("derived columns do not support having their values set directly");
    }

    public long rowCount() {
        return sourceColumn.rowCount();
    }

    public int get( long row ) {
        QA.isTrue( isSet(row), "do not call get(row) on a row that has not been set" );

        return get(row,sourceColumn);
    }

    public IntCodec getCodec() {
        return sourceColumn.getCodec();
    }

    public void writeValueTo( CharacterStream out, long row ) {
        if ( isSet(row) ) {
            int v = get(row);

            getCodec().encode( v, out );
        }
    }

    public CellExplanation explain( long row ) {
        IntColumnAuditor auditor = new IntColumnAuditor(sourceColumn, expectedCellCount);

        int               value           = get( row, auditor );
        LongSet visitedRows     = auditor.getVisitedRows();
        Map<String,LongSet> referencedCells = MapUtils.asMap( sourceColumn.getColumnName(), visitedRows );
        String              eqn             = toEquation( referencedCells );
        String              formattedValue  = encodeValue( value );


        return new CellExplanation( formattedValue, eqn, referencedCells );
    }

    protected abstract int get( long row, IntColumn col );

    private String encodeValue( int v ) {
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
