package com.mosaic.columnstore.columns;

import com.mosaic.collections.LongSet;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.IntColumn;
import com.mosaic.io.codecs.IntCodec;
import com.mosaic.io.streams.CharacterStream;


/**
 * Automatically detect which rows are accessed by a formula.  Used to generate explanations
 * of how a column is derived.
 */
class IntColumnAuditor implements IntColumn {
    private IntColumn sourceColumn;
    private LongSet visitedRows;


    public IntColumnAuditor( IntColumn sourceColumn, int targetSampleCount ) {
        this.sourceColumn = sourceColumn;
        this.visitedRows  = LongSet.createLongSet(targetSampleCount);
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

    public int get( long row ) {
        visitedRows.add(row);

        return sourceColumn.get( row );
    }

    public void set( long row, int value ) {
        throw new UnsupportedOperationException( "A column should cannot be modified when generating an explanation" );
    }

    public void unset( long row ) {
        throw new UnsupportedOperationException( "A column should cannot be modified when generating an explanation" );
    }

    public long rowCount() {
        return sourceColumn.rowCount();
    }

    public CellExplanation explain( long row ) {
        throw new UnsupportedOperationException( "explain an explain... hmmmmmm" );
    }

    public void writeValueTo( CharacterStream out, long row ) {
        if ( isSet(row) ) {
            int v = get(row);

            getCodec().encode( v, out );
        }
    }

    public IntCodec getCodec() {
        return sourceColumn.getCodec();
    }

    public LongSet getVisitedRows() {
        return visitedRows;
    }
}