package com.mosaic.columnstore.columns;

import com.mosaic.collections.LongSet;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.LongColumn;
import com.mosaic.io.codecs.BooleanCodec;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.SystemX;
import com.mosaic.utils.MapUtils;

import java.util.Map;


/**
 *
 */
public abstract class LongLong2BooleanFormula extends BaseBooleanColumn {

    private SystemX      system;
    private String       columnName;
    private String       description;

    private String       opName;
    private LongColumn   sourceColumn1;
    private LongColumn   sourceColumn2;

    private BooleanCodec codec;


    protected LongLong2BooleanFormula(
        SystemX system,
        String columnName, String description,
        String opName,
        LongColumn sourceColumn1, LongColumn sourceColumn2
    ) {
        this( system, columnName, description, opName, sourceColumn1, sourceColumn2, BooleanCodec.BOOLEAN_CODEC );
    }

    protected LongLong2BooleanFormula(
        SystemX system,
        String columnName, String description,
        String opName,
        LongColumn sourceColumn1, LongColumn sourceColumn2,
        BooleanCodec codec
    ) {
        this.system            = system;
        this.columnName        = columnName;
        this.description       = description;

        this.opName            = opName;
        this.sourceColumn1     = sourceColumn1;
        this.sourceColumn2     = sourceColumn2;
        this.codec             = codec;
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
        return codec;
    }

    public CellExplanation explain( long row ) {
        LongColumnAuditor auditor1 = new LongColumnAuditor( sourceColumn1, sourceColumn1.size() );
        LongColumnAuditor auditor2 = new LongColumnAuditor( sourceColumn2, sourceColumn2.size() );

        boolean             value           = get( row, auditor1, auditor2 );
        LongSet             visitedRows1    = auditor1.getVisitedRows();
        LongSet             visitedRows2    = auditor2.getVisitedRows();
        Map<String,LongSet> referencedCells = MapUtils.asLinkedMap( sourceColumn1.getColumnName(), visitedRows1, sourceColumn2.getColumnName(), visitedRows2 );
        String              eqn             = toEquation( referencedCells );
        String              formattedValue  = encodeValue( value );


        return new CellExplanation( formattedValue, eqn, referencedCells );
    }

    protected abstract boolean isSet( long row, LongColumn col1, LongColumn col2 );
    protected abstract boolean get( long row, LongColumn col1, LongColumn col2 );

    private String encodeValue( boolean v ) {
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


