package com.mosaic.columnstore.columns;

import com.mosaic.collections.LongSet;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.DoubleColumn;
import com.mosaic.io.codecs.DoubleCodec;
import com.mosaic.io.streams.CharacterStream;


/**
 * Automatically detect which rows are accessed by a formula.  Used to generate explanations
 * of how a column is derived.
 */
class DoubleColumnAuditor extends BaseDoubleColumn implements ColumnAuditor {
    private DoubleColumn sourceColumn;
    private LongSet visitedRows;


    public DoubleColumnAuditor( DoubleColumn sourceColumn, int targetSampleCount ) {
        this.sourceColumn = sourceColumn;
        this.visitedRows  = LongSet.factory( targetSampleCount );
    }

    public String getColumnName() {
        return sourceColumn.getColumnName();
    }

    public String getDescription() {
        return sourceColumn.getDescription();
    }

    public boolean isSet( long row ) {
        return sourceColumn.isSet( row );
    }

    public double get( long row ) {
        visitedRows.add(row);

        return sourceColumn.get( row );
    }

    public void set( long row, double value ) {
        throw new UnsupportedOperationException( "A column should cannot be modified when generating an explanation" );
    }

    public void unset( long row ) {
        throw new UnsupportedOperationException( "A column should cannot be modified when generating an explanation" );
    }

    public long size() {
        return sourceColumn.size();
    }

    public void resizeIfNecessary( long newSize ) {
        sourceColumn.resizeIfNecessary( newSize );
    }

    public CellExplanation explain( long row ) {
        throw new UnsupportedOperationException( "explain an explain... hmmmmmm" );
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

    public LongSet getVisitedRows() {
        return visitedRows;
    }
}
