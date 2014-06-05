package com.mosaic.columnstore.columns;

import com.mosaic.collections.LongSet;

import java.util.Map;


/**
 *
 */
public class ColumnUtils {
    /**
     * Default implementations of getEquation on each of the Formula classes.  It formats
     * the formula as 'OpName(columnName [rowIds])'.
     */
    public static String formatTouchedCells( String opName, Map<String, LongSet> touchedCells ) {
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
}
