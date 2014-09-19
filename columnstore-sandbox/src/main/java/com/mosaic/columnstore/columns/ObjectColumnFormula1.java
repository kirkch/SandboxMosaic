package com.mosaic.columnstore.columns;

import com.mosaic.collections.LongSet;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.ObjectColumn;
import com.mosaic.io.codecs.ObjectCodec;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.lang.system.SystemX;
import com.mosaic.utils.MapUtils;

import java.util.Map;


/**
 * A formula that processes a single float column into a new float column.
 */
@SuppressWarnings("unchecked")
public abstract class ObjectColumnFormula1<T> extends BaseObjectColumn<T> {

    private SystemX      system;

    private String       columnName;
    private String       description;

    private String       opName;
    private ObjectColumn sourceColumn;
    private int          expectedCellCount;


    /**
     *
     * @param expectedCellCount how many source cells are probably used to calculate a single cell in this column?
     */
    protected ObjectColumnFormula1( SystemX system, String columnName, String description, String opName, ObjectColumn sourceColumn, int expectedCellCount ) {
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

    public void set( long row, float value ) {
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

    public T get( long row ) {
        return get(row,sourceColumn);
    }

    public void writeValueTo( CharacterStream out, long row ) {
        if ( isSet(row) ) {
            T v = get(row);

            getCodec().encode( v, out );
        }
    }

    public ObjectCodec<T> getCodec() {
        return sourceColumn.getCodec();
    }

    public CellExplanation explain( long row ) {
        ObjectColumnAuditor auditor = new ObjectColumnAuditor(sourceColumn, expectedCellCount);

        T                   value           = get( row, auditor );
        LongSet             visitedRows     = auditor.getVisitedRows();
        Map<String,LongSet> referencedCells = MapUtils.asMap( sourceColumn.getColumnName(), visitedRows );
        String              eqn             = toEquation( referencedCells );
        String              formattedValue  = encodeValue( value );


        return new CellExplanation( formattedValue, eqn, referencedCells );
    }

    protected abstract T get( long row, ObjectColumn col );

    private String encodeValue( T v ) {
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

