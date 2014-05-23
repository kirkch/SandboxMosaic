package com.mosaic.io.streams;

import com.mosaic.collections.Table;
import com.mosaic.utils.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.Character.isWhitespace;


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

    public static void repeat( StringBuilder buf, char c, int numTimes ) {
        for ( int i=0; i<numTimes; i++ ) {
            buf.append( c );
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

    public static void englishList( StringBuilder out, Iterable<String> nouns, String penultimateSeparator ) {
        boolean printComma = false;

        Iterator<String> it = nouns.iterator();
        while ( it.hasNext() ) {
            String noun = it.next();

            if ( printComma ) {
                if ( it.hasNext() ) {
                    out.append( ", " );
                } else {
                    out.append( " " );
                    out.append( penultimateSeparator );
                    out.append( " " );
                }
            } else {
                printComma = true;
            }

            out.append( noun );
        }
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

    public static String underscoreCaseToCamelCase( String underscoreCase ) {
        if ( underscoreCase == null ) {
            return null;
        } else if ( underscoreCase.length() == 0 ) {
            return "";
        }


        StringBuilder buf   = new StringBuilder();
        String[]      words = underscoreCase.split( "_" );

        for ( String word : words ) {
            if ( word.length() != 0 ) {
                buf.append( Character.toUpperCase(word.charAt(0)) );
                buf.append( word.substring(1).toLowerCase() );
            }
        }

        return buf.toString();
    }

    /**
     * Ensure that the sentence starts with a capital letter and ends with a full stop.
     */
    public static String cleanEnglishSentence( String sentence ) {
        StringBuilder buf = new StringBuilder( sentence.length()+2 );

        buf.append( sentence );

        cleanEnglishSentence( buf );

        return buf.toString();
    }

    public static void cleanEnglishSentence( StringBuilder sentence ) {
        if ( sentence.length() == 0 ) {
            return;
        }

        int firstCharIndex = findFirstNonBlankCharacter(sentence);
        int lastCharIndex  = findLastNonBlankCharacter(sentence);

        if ( isCharacterLC(sentence,firstCharIndex) ) {
            sentence.setCharAt( firstCharIndex, Character.toUpperCase(sentence.charAt(firstCharIndex)) );
        }

        if ( !isCharacterFullStop(sentence,lastCharIndex) ) {
            if ( lastCharIndex == sentence.length()-1 ) {
                sentence.append( '.' );
            } else {
                sentence.setCharAt( lastCharIndex+1, '.' );
            }
        }
    }

    private static int findFirstNonBlankCharacter( StringBuilder sentence ) {
        for ( int i=0; i<sentence.length(); i++ ) {
            char c = sentence.charAt( i );

            if ( c != ' ' && c != '\t' ) {
                return i;
            }
        }

        return -1;
    }

    private static int findLastNonBlankCharacter( StringBuilder sentence ) {
        for ( int i=sentence.length()-1; i>=0; i-- ) {
            char c = sentence.charAt( i );

            if ( c != ' ' && c != '\t' ) {
                return i;
            }
        }

        return -1;
    }

    private static boolean isCharacterFullStop( StringBuilder sentence, int i ) {
        return i >= 0 && sentence.charAt(i) == '.';
    }

    private static boolean isCharacterLC( StringBuilder sentence, int i ) {
        return i >= 0 && Character.isLowerCase(sentence.charAt(i));
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

        /**
         * Always writes the entire cell width, padding with spaces.
         */
        private int writeFragmentToCell( Table<String> tableBuffer, int currentRow, int col, String str, int numCharactersRemaining, int maxWidth ) {
            StringBuilder buf = new StringBuilder(maxWidth);

            int from = skipWhiteSpace(str, str.length()-numCharactersRemaining);
            int to   = selectEndOfStringToCopy(str, from, maxWidth);
            int len  = to - from;

            buf.append( str.substring(from, to) );


            PrettyPrinter.repeat( buf, ' ', maxWidth-len );

            tableBuffer.set( currentRow, col, buf.toString() );

            return str.length()-to;
        }

        private int skipWhiteSpace( String str, int i ) {
            while ( i<str.length() && isWhitespace(str.charAt(i)) ) {
                i++;
            }

            return i;
        }

        private int selectEndOfStringToCopy( String str, int from, int maxWidth ) {
            int to = Math.min( str.length(), from+maxWidth );

            if ( isWithinWord(str,to) ) {
                for ( int i=to-1; i>from; i-- ) {
                    if ( isWhitespace(str.charAt(i)) ) {
                        return i;
                    }
                }
            }

            return to;
        }

        private boolean isWithinWord( String str, int i ) {
            if ( i < str.length() ) {
                return !isWhitespace( str.charAt(i) ) && !isWhitespace( str.charAt(i-1) );
            }

            return false;
        }
    }


}
