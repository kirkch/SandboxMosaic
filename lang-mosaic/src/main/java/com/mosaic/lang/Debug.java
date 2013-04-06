package com.mosaic.lang;

/**
 * Util class for printing out debug code. Tuned so that it can be optimised out by hotspot within production environments.
 */
public class Debug {

    // Not thread safe; which is not a problem given how this class is to be used. We don't want the performance impact of volatile here
    private boolean isEnabled;

    private int[] columnWidths = new int[] {};

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled( boolean flag ) {
        this.isEnabled = flag;
    }

    public void log( String msg, Object...args ) {
        if ( isEnabled ) {
            System.out.println( String.format(msg, args) );
        }
    }

    public void setColumnWidths( int... columnWidths ) {
        this.columnWidths = columnWidths;
    }

    /**
     * Pretty print the objects into columns.
     */
    public void logPP( Object...columnData ) {
        int columnIndex = 0;

        StringBuilder buf = new StringBuilder(200);
        for ( Object column:columnData ) {
            String text     = column == null ? "null" : column.toString();
            int    maxWidth = columnIndex < columnWidths.length ? columnWidths[columnIndex] : Integer.MAX_VALUE;
            int    pad      = maxWidth == Integer.MAX_VALUE ? 0 : maxWidth - text.length();

            if ( pad < 0 ) { // truncate text
                text = text.substring( 0, maxWidth-2 ) + "..";
            }

            if ( columnIndex != 0 ) { // column separator
                buf.append( ' ' );
            }

            buf.append( text );

            while ( pad > 0 ) {  // pad columns out to fixed widths
                buf.append( ' ' );
                pad--;
            }

            columnIndex++;
        }

        log( buf.toString() );
    }
}
