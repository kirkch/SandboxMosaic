package com.mosaic.columnstore.columns;

import com.mosaic.collections.LongSet;
import com.mosaic.columnstore.BooleanColumn;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.io.codecs.BooleanCodec;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.lang.QA;
import com.mosaic.utils.MapUtils;

import java.util.Map;


/**
 *
 */
public abstract class BooleanColumnFormula2 extends BaseBooleanColumn {

    private String        columnName;
    private String        description;

    private String        opName;
    private BooleanColumn sourceColumn1;
    private BooleanColumn sourceColumn2;
    private int           expectedCellCount;


    /**
     *
     * @param expectedCellCount how many source cells are probably used to calculate a single cell in this column? (hlong only)
     */
    protected BooleanColumnFormula2( String columnName, String description, String opName, BooleanColumn sourceColumn1, BooleanColumn sourceColumn2, int expectedCellCount ) {
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
        return isSet( row, sourceColumn1, sourceColumn2 );
    }

    public void set( long row, boolean value ) {
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

    public boolean get( long row ) {
        QA.isTrue( isSet( row ), "do not call get(row) on a row that has not been set" );

        return get(row, sourceColumn1, sourceColumn2 );
    }

    public void writeValueTo( CharacterStream out, long row ) {
        if ( isSet(row) ) {
            boolean v = get(row);

            getCodec().encode( v, out );
        }
    }

    public BooleanCodec getCodec() {
        return sourceColumn1.getCodec();
    }

    public CellExplanation explain( long row ) {
        BooleanColumnAuditor auditor1 = new BooleanColumnAuditor( sourceColumn1, expectedCellCount);
        BooleanColumnAuditor auditor2 = new BooleanColumnAuditor( sourceColumn2, expectedCellCount);

        boolean             value           = get( row, auditor1, auditor2 );
        LongSet             visitedRows1    = auditor1.getVisitedRows();
        LongSet             visitedRows2    = auditor2.getVisitedRows();
        Map<String,LongSet> referencedCells = MapUtils.asMap( sourceColumn1.getColumnName(), visitedRows1, sourceColumn2.getColumnName(), visitedRows2 );
        String              eqn             = toEquation( referencedCells );
        String              formattedValue  = encodeValue( value );


        return new CellExplanation( formattedValue, eqn, referencedCells );
    }

    protected abstract boolean isSet( long row, BooleanColumn sourceColumn1, BooleanColumn sourceColumn2 );
    protected abstract boolean get( long row, BooleanColumn col1, BooleanColumn col2 );

    private String encodeValue( boolean v ) {
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
