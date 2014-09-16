package com.mosaic.columnstore.columns;

import com.mosaic.collections.LongSet;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.Column;
import com.mosaic.columnstore.LongColumn;
import com.mosaic.io.codecs.LongCodec;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.lang.QA;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.system.SystemX;
import com.mosaic.utils.ListUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 */
public abstract class LongColumnFormula2N extends BaseLongColumn {

    private   final SystemX          system;

    private   final String           columnName;
    private   final String           description;

    protected final String           opName;
    private   final List<LongColumn> sourceColumnsA  = new ArrayList<>();
    private   final List<LongColumn> sourceColumnsB  = new ArrayList<>();


    protected LongColumnFormula2N( SystemX system, String columnName, String description, String opName) {
        this.system      = system;

        this.columnName  = columnName;
        this.description = description;
        this.opName      = opName;
    }

    public void addSourceColumns( LongColumn colA, LongColumn colB ) {
        QA.argNotNull( colA, "colA" );
        QA.argNotNull( colB, "colB" );

        this.sourceColumnsA.add( colA );
        this.sourceColumnsB.add( colB );
    }

    public String getColumnName() {
        return columnName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSet( long row ) {
        return isSet( row, sourceColumnsA, sourceColumnsB );
    }

    protected abstract boolean isSet( long row, List<LongColumn> columnsA, List<LongColumn> columnsB );

    public void set( long row, long value ) {
        throw new UnsupportedOperationException("derived columns do not support having their values set directly");
    }

    public void unset( long row ) {
        throw new UnsupportedOperationException( "A column should cannot be modified when generating an explanation" );
    }

    public long size() {
        long max = 0;

        for ( Column col : sourceColumnsA ) {
            max = Math.max( max, col.size() );
        }

        return max;
    }

    public void resizeIfNecessary( long newSize ) {
        throw new UnsupportedOperationException( "A column should cannot be modified when generating an explanation" );
    }

    public long get( long row ) {
        QA.isTrue( isSet( row ), "do not call get(row) on a row that has not been set" );

        return get( row, sourceColumnsA, sourceColumnsB );
    }

    public LongCodec getCodec() {
        if ( sourceColumnsA.isEmpty() ) {
            return LongCodec.LONG_CODEC;
        } else {
            return sourceColumnsA.get(0).getCodec();
        }
    }

    public void writeValueTo( CharacterStream out, long row ) {
        if ( isSet(row) ) {
            long v = get(row);

            getCodec().encode( v, out );
        }
    }

    public CellExplanation explain( long row ) {
        final long expectedCellCount = sourceColumnsA.size();

        List<LongColumnAuditor> auditingColumnsA = ListUtils.map( sourceColumnsA, new Function1<LongColumn, LongColumnAuditor>() {
            public LongColumnAuditor invoke( LongColumn sourceColumn ) {
                return new LongColumnAuditor(sourceColumn, expectedCellCount );
            }
        });

        List<LongColumnAuditor> auditingColumnsB = ListUtils.map( sourceColumnsB, new Function1<LongColumn, LongColumnAuditor>() {
            public LongColumnAuditor invoke( LongColumn sourceColumn ) {
                return new LongColumnAuditor(sourceColumn, expectedCellCount );
            }
        });


        long value = get( row, auditingColumnsA, auditingColumnsB );


        Map<String,LongSet> referencedCells = new HashMap<>();
        for ( LongColumnAuditor auditor : auditingColumnsA ) {
            referencedCells.put( auditor.getColumnName(), auditor.getVisitedRows() );
        }

        for ( LongColumnAuditor auditor : auditingColumnsB ) {
            referencedCells.put( auditor.getColumnName(), auditor.getVisitedRows() );
        }



        String eqn             = toEquation( referencedCells );
        String formattedValue  = encodeValue( value );


        return new CellExplanation( formattedValue, eqn, referencedCells );
    }

    protected abstract <T extends LongColumn> long get( long row, List<T> columnsA, List<T> columnsB );

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
