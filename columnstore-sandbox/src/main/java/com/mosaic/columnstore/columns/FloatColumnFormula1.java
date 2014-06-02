package com.mosaic.columnstore.columns;

import com.mosaic.collections.LongSet;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.FloatColumn;
import com.mosaic.io.codecs.FloatCodec;
import com.mosaic.io.streams.UTF8Builder;
import com.mosaic.lang.functional.Long2FloatFunction;
import com.mosaic.utils.MapUtils;

import java.util.Map;


/**
 * A formula that processes a single float column into a new float column.
 */
public abstract class FloatColumnFormula1 implements FloatColumn {

    private String             columnName;
    private String             opName;
    private FloatColumn        sourceColumn;
    private int                expectedCellCount;
    private Long2FloatFunction autoCachingFormula = new CachingCalculation();


    /**
     *
     * @param expectedCellCount how many source cells are probably used to calculate a single cell in this column? (hint only)
     */
    protected FloatColumnFormula1( String columnName, String opName, FloatColumn sourceColumn, int expectedCellCount ) {
        this.columnName        = columnName;
        this.opName            = opName;
        this.sourceColumn      = sourceColumn;
        this.expectedCellCount = expectedCellCount;
    }

    public String getColumnName() {
        return columnName;
    }

    public boolean isSet( long row ) {
        return sourceColumn.isSet(row);
    }

    public void set( long row, float value ) {
        throw new UnsupportedOperationException("derived columns do not support having their values set directly");
    }

    public long rowCount() {
        return sourceColumn.rowCount();
    }

    public float get( long row ) {
        return autoCachingFormula.invoke(row);
    }

    public FloatCodec getCodec() {
        return sourceColumn.getCodec();
    }

    public CellExplanation explain( long row ) {
        FloatColumnAuditor auditor = new FloatColumnAuditor(sourceColumn, expectedCellCount);

        float               value           = get( row, auditor );
        LongSet             visitedRows     = auditor.getVisitedRows();
        Map<String,LongSet> referencedCells = MapUtils.asMap( sourceColumn.getColumnName(), visitedRows );
        String              eqn             = toEquation( referencedCells );
        String              formattedValue  = encodeValue( value );


        return new CellExplanation( formattedValue, eqn, referencedCells );
    }

    protected abstract float get( long row, FloatColumn col );

    private String encodeValue( float v ) {
        UTF8Builder buf = new UTF8Builder();

        try {
            getCodec().encode( v, buf );

            return buf.toString();
        } finally {
            buf.clear();
        }
    }

    /**
     * Returns a formulation of the cell in the form of 'OpName(columnName [rowIds])'.  May
     * be overridden to change the format or add constants.
     */
    protected String toEquation( Map<String,LongSet> touchedCells ) {
        StringBuilder buf = new StringBuilder();

        buf.append( opName );
        buf.append( '(' );

        boolean includeComma = false;
        for ( Map.Entry<String,LongSet> e : touchedCells.entrySet() ) {
            if ( includeComma ) {
                buf.append( ", " );
            } else {
                includeComma = true;
            }

            buf.append( e.getKey() );

            LongSet rowIds = e.getValue();
            if ( rowIds.hasContents() ) {
                buf.append( '[' );
                rowIds.appendTo( buf, "," );
                buf.append( ']' );
            }
        }

        buf.append( ')' );


        return buf.toString();
    }


    private class CachingCalculation implements Long2FloatFunction {
        private FloatColumn cachedColumn = new FloatColumnArray( "cache" );

        public float invoke( long row ) {
            if ( cachedColumn.isSet(row) ) {
                return cachedColumn.get(row);
            } else {
                float v = get( row, sourceColumn );

                cachedColumn.set( row, v );

                return v;
            }
        }
    }

    private static class FloatColumnAuditor implements FloatColumn {
        private FloatColumn sourceColumn;
        private LongSet     visitedRows;


        public FloatColumnAuditor( FloatColumn sourceColumn, int targetSampleCount ) {
            this.sourceColumn = sourceColumn;
            this.visitedRows  = LongSet.createLongSet(targetSampleCount);
        }

        public String getColumnName() {
            return sourceColumn.getColumnName();
        }

        public boolean isSet( long row ) {
            return sourceColumn.isSet( row );
        }

        public float get( long row ) {
            visitedRows.add(row);

            return sourceColumn.get( row );
        }

        public void set( long row, float value ) {
            throw new UnsupportedOperationException( "A column should cannot be modified when generating an explanation" );
        }

        public long rowCount() {
            return sourceColumn.rowCount();
        }

        public CellExplanation explain( long row ) {
            throw new UnsupportedOperationException( "explain an explain... hmmmmmm" );
        }

        public FloatCodec getCodec() {
            return sourceColumn.getCodec();
        }

        public LongSet getVisitedRows() {
            return visitedRows;
        }
    }
}
