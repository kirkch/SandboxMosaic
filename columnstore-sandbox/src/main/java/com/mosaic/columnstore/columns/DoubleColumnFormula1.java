package com.mosaic.columnstore.columns;

import com.mosaic.collections.LongSet;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.DoubleColumn;
import com.mosaic.io.codecs.DoubleCodec;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.lang.system.SystemX;
import com.mosaic.utils.MapUtils;

import java.util.Map;


/**
 * A formula that processes a single double column into a new double column.
 */
public abstract class DoubleColumnFormula1 extends BaseDoubleColumn {

    private SystemX      system;
    private String       columnName;
    private String       description;

    private String       opName;
    private DoubleColumn sourceColumn;
    private long         expectedCellCount;


    /**
     *
     * @param expectedCellCount how many source cells are probably used to calculate a single cell in this column? (hint only)
     */
    protected DoubleColumnFormula1( SystemX system, String columnName, String description, String opName, DoubleColumn sourceColumn, long expectedCellCount ) {
        this.system            = system;
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

    public void set( long row, double value ) {
        throw new UnsupportedOperationException("derived columns do not support having their values set directly");
    }

    public void unset( long row ) {
        throw new UnsupportedOperationException( "A column should cannot be modified when generating an explanation" );
    }

    public long size() {
        return sourceColumn.size();
    }

    public void resizeIfNecessary( long newSize ) {
        throw new UnsupportedOperationException( "A column should cannot be modified when generating an explanation" );
    }

    public double get( long row ) {
        return get(row,sourceColumn);
    }

    public void writeValueTo( CharacterStream out, long row ) {
        if ( isSet(row) ) {
            double v = get(row);

            getCodec().encode( v, out );
        }
    }

    public DoubleCodec getCodec() {
        return sourceColumn.getCodec();
    }

    public CellExplanation explain( long row ) {
        DoubleColumnAuditor auditor = new DoubleColumnAuditor(sourceColumn, expectedCellCount);

        double              value           = get( row, auditor );
        LongSet             visitedRows     = auditor.getVisitedRows();
        Map<String,LongSet> referencedCells = MapUtils.asMap( sourceColumn.getColumnName(), visitedRows );
        String              eqn             = toEquation( referencedCells );
        String              formattedValue  = encodeValue( value );


        return new CellExplanation( formattedValue, eqn, referencedCells );
    }

    protected abstract double get( long row, DoubleColumn col );

    private String encodeValue( double v ) {
        return getCodec().toString(system,v);
    }

    /**
     * Returns a formulation of the cell in the form of 'OpName(columnName [rowIds])'.  May
     * be overridden to change the format or add constants.
     */
    protected String toEquation( Map<String,LongSet> touchedCells ) {
        return ColumnUtils.formatTouchedCells( opName, touchedCells );
    }

}
