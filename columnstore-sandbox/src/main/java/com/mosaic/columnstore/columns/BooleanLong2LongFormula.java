package com.mosaic.columnstore.columns;

import com.mosaic.collections.LongSet;
import com.mosaic.columnstore.BooleanColumn;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.LongColumn;
import com.mosaic.io.codecs.LongCodec;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.SystemX;
import com.mosaic.utils.MapUtils;

import java.util.Map;


/**
 *
 */
public abstract class BooleanLong2LongFormula extends BaseLongColumn {

    private SystemX       system;
    private String        columnName;
    private String        description;

    private String        opName;
    private BooleanColumn sourceColumn1;
    private LongColumn    sourceColumn2;


    protected BooleanLong2LongFormula( SystemX system, String columnName, String description, String opName, BooleanColumn sourceColumn1, LongColumn sourceColumn2 ) {
        this.system            = system;
        this.columnName        = columnName;
        this.description       = description;

        this.opName            = opName;
        this.sourceColumn1     = sourceColumn1;
        this.sourceColumn2     = sourceColumn2;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSet( long row ) {
        return sourceColumn1.isSet(row) && sourceColumn2.isSet(row);
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
        QA.isTrue( isSet( row ), "do not call get(row) on a row that has not been set" );

        return get(row, sourceColumn1, sourceColumn2 );
    }

    public void writeValueTo( CharacterStream out, long row ) {
        if ( isSet(row) ) {
            long v = get(row);

            getCodec().encode( v, out );
        }
    }

    public LongCodec getCodec() {
        return sourceColumn2.getCodec();
    }

    public CellExplanation explain( long row ) {
        BooleanColumnAuditor auditor1 = new BooleanColumnAuditor( sourceColumn1, size() );
        LongColumnAuditor    auditor2 = new LongColumnAuditor( sourceColumn2, size() );

        long                value           = get( row, auditor1, auditor2 );
        LongSet             visitedRows1    = auditor1.getVisitedRows();
        LongSet             visitedRows2    = auditor2.getVisitedRows();
        Map<String,LongSet> referencedCells = MapUtils.asMap( sourceColumn1.getColumnName(), visitedRows1, sourceColumn2.getColumnName(), visitedRows2 );
        String              eqn             = toEquation( referencedCells );
        String              formattedValue  = encodeValue( value );


        return new CellExplanation( formattedValue, eqn, referencedCells );
    }

    protected abstract long get( long row, BooleanColumn col1, LongColumn col2 );

    private String encodeValue( long v ) {
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
