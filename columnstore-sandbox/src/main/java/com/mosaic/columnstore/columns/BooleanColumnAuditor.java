package com.mosaic.columnstore.columns;

import com.mosaic.collections.LongSet;
import com.mosaic.columnstore.BooleanColumn;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.io.codecs.BooleanCodec;
import com.mosaic.io.streams.CharacterStream;


/**
 *
 */
public class BooleanColumnAuditor extends BaseBooleanColumn {
    private BooleanColumn sourceColumn;
    private LongSet       visitedRows;


    public BooleanColumnAuditor( BooleanColumn sourceColumn, int targetSampleCount ) {
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

    public boolean get( long row ) {
        visitedRows.add(row);

        return sourceColumn.get( row );
    }

    public void set( long row, boolean value ) {
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
            boolean v = get(row);

            getCodec().encode( v, out );
        }
    }

    public BooleanCodec getCodec() {
        return sourceColumn.getCodec();
    }

    public LongSet getVisitedRows() {
        return visitedRows;
    }

}
