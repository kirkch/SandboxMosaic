package com.mosaic.io.streams;

import com.mosaic.collections.Table;
import com.mosaic.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class PrettyPrinter {

    public static final ColumnHandler TRUNCATE        = new TruncateColumnHandler();
    public static final ColumnHandler PAD_OR_TRUNCATE = new PadOrTruncateColumnHandler();
    public static final ColumnHandler WRAP            = new WrapColumnHandler();


    public static int longestLength( String[] strings ) {
        int longest = 0;
        for ( String s : strings ) {
            longest = Math.max( s.length(), longest );
        }

        return longest;
    }

    public static void printWrapped( CharacterStream info, String text, int maxLineWidth ) {
        for ( int i=0; i<text.length(); i += maxLineWidth) {
            int endIndexExc = i + maxLineWidth;

            if ( endIndexExc >= text.length() ) {
                info.writeLine( text.substring(i) );
            } else {
                info.writeLine( text.substring(i, endIndexExc) );
            }
        }
    }

    public static void printPleural( CharacterStream out, String noun, int count ) {
        out.writeString( noun );

        if ( count > 1 ) {
            out.writeString( "s" );
        }
    }

    public static void englishList( CharacterStream out, String[] nouns ) {
        englishList( out, nouns, 0, nouns.length );
    }

    public static void englishList( CharacterStream out, String[] nouns, int fromInc, int toExc ) {
        if ( toExc-fromInc <= 0 ) {
            return;
        }

        out.writeString( nouns[fromInc] );

        for ( int i=fromInc+1; i<toExc-1; i++ ) {
            out.writeString( ", " );
            out.writeString( nouns[i] );
        }

        if ( toExc > fromInc+1 ) {
            out.writeString( " and " );
            out.writeString( nouns[toExc-1] );
        }
    }




    private CharacterStream out;
    private int[]           fixedColumnWidths;

    public PrettyPrinter( CharacterStream out, int...fixedColumnWidths ) {
        assert fixedColumnWidths.length > 0;

        this.out               = out;
        this.fixedColumnWidths = fixedColumnWidths;

        for ( int i=0; i<fixedColumnWidths.length-1; i++ ) {
            columnHandlers.add( PAD_OR_TRUNCATE );
        }

        columnHandlers.add( TRUNCATE );
    }

    public void setColumnHandler( int columnIndex, ColumnHandler columnHandler ) {
        columnHandlers.set( columnIndex, columnHandler );
    }



    private Table<String>       tableBuffer    = new Table<>();
    private List<ColumnHandler> columnHandlers = new ArrayList<>();

    public void write( Object...columnValues ) {
        tableBuffer.clear();

        writeRowToTableBuffer( columnValues );
        writeTableBufferToOutputStream();
    }

    private void writeRowToTableBuffer( Object[] columnValues ) {
        int row = tableBuffer.rowCount();

        for ( int col=0; col<columnValues.length; col++ ) {
            ColumnHandler columnHandler = columnHandlers.get(col);
            Object        columnValue   = columnValues[col];

            columnHandler.writeTo( columnValue, fixedColumnWidths[col], tableBuffer, row, col );
        }
    }

    private void writeTableBufferToOutputStream() {
        for ( int x=0; x<tableBuffer.rowCount(); x++ ) {
            for ( int y=0; y<tableBuffer.columnCount(x); y++ ) {
                String col = tableBuffer.get(x,y);

                if ( col == null ) {
                    for ( int i=0; i<fixedColumnWidths[y]; i++ ) {
                        out.writeCharacter( ' ' );
                    }
                }

                if ( y != 0 ) {
                    out.writeCharacter( ' ' );
                }

                if ( col != null ) {
                    if ( y == tableBuffer.columnCount(x)-1 ) {
                        col = StringUtils.trimRight( col );
                    }

                    out.writeString( col );
                }
            }

            out.newLine();
        }
    }




    public interface ColumnHandler {

        void writeTo( Object value, int columnWidth, Table<String> tableBuffer, int row, int col );

    }

    private static class TruncateColumnHandler implements ColumnHandler {
        public void writeTo( Object value, int columnWidth, Table<String> tableBuffer, int row, int col ) {
            String formattedString = format(value, columnWidth);

            tableBuffer.set(row,col,formattedString);
        }

        private String format( Object o, int maxWidth ) {
            String str       = o.toString();
            int    strLength = str.length();

            if ( strLength > maxWidth ) {
                return str.substring( 0, maxWidth );
            } else {
                return str;
            }
        }
    }

    private static class PadOrTruncateColumnHandler implements ColumnHandler {
        public void writeTo( Object value, int columnWidth, Table<String> tableBuffer, int row, int col ) {
            String formattedString = format(value, columnWidth);

            tableBuffer.set(row,col,formattedString);
        }

        private String format( Object o, int maxWidth ) {
            String str       = o.toString();
            int    strLength = str.length();

            if ( strLength > maxWidth ) {
                return str.substring( 0, maxWidth );
            } else {
                StringBuilder buf = new StringBuilder();

                buf.append( str );

                for ( int j=maxWidth-strLength; j > 0; j-- ) {
                    buf.append( ' ' );
                }

                return buf.toString();
            }
        }
    }

    /**
     * If the string is too long for one cell, write what fits and then wrap to the cell in
     * the same column but on the next row.
     */
    private static class WrapColumnHandler implements ColumnHandler {
        public void writeTo( Object value, final int columnWidth, Table<String> tableBuffer, final int row, final int col ) {
            final String str = value.toString();

            int numCharactersRemaining = str.length();
            int currentRow = row;

            while ( numCharactersRemaining > 0 ) {
                numCharactersRemaining = writeFragmentToCell( tableBuffer, currentRow, col, str, numCharactersRemaining, columnWidth );

                currentRow += 1;
            }
        }

        private int writeFragmentToCell( Table<String> tableBuffer, int currentRow, int col, String str, int numCharactersRemaining, int maxWidth ) {
            int stringLength = str.length();
            int from         = stringLength - numCharactersRemaining;

            if ( numCharactersRemaining > maxWidth ) {
                int endIndexExc = from + maxWidth;
                String substring = str.substring( from, endIndexExc );

                tableBuffer.set( currentRow, col, substring );

                return stringLength-endIndexExc;
            } else {
                StringBuilder buf = new StringBuilder();

                buf.append( str, from, stringLength );

                for ( int j=maxWidth-buf.length(); j > 0; j-- ) {
                    buf.append( ' ' );
                }


                tableBuffer.set( currentRow, col, buf.toString() );

                return 0;
            }
        }
    }
}
