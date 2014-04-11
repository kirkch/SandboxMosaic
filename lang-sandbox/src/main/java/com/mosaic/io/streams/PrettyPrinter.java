package com.mosaic.io.streams;

/**
 *
 */
public class PrettyPrinter {


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
    private int[]   fixedColumnWidths;

    public PrettyPrinter( CharacterStream out, int...fixedColumnWidths ) {
        this.out               = out;
        this.fixedColumnWidths = fixedColumnWidths;
    }

    public void write( Object...columnValues ) {
        for ( int i=0; i<fixedColumnWidths.length; i++ ) {
            if ( columnValues.length == i ) {
                break;
            }

            String s = columnValues[i].toString();
            int fixedWidth = fixedColumnWidths[i];

            if ( s.length() >= fixedWidth ) {
                out.writeString( s.substring(0, fixedWidth) );
            } else {
                out.writeString( s );

                for ( int j=fixedWidth-s.length(); j > 0; j-- ) {
                    out.writeCharacter( ' ' );
                }
            }

            out.writeCharacter( ' ' );
        }

        out.writeLine( "" );
    }
}
