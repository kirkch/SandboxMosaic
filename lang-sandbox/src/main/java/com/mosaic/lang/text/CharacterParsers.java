package com.mosaic.lang.text;

import com.mosaic.io.bytes.InputBytes;


/**
 *
 */
public class CharacterParsers {

    // exactMatch("")
    // exactMatch( byte[] )
    // float
    // double
    // char
    //

    public static ByteMatcher<UTF8> exactMatch( String target ) {
        return new ExactMatch( new UTF8(target) );
    }

    public static ByteMatcher tabOrSpaces() {
        return TabOrSpace.INSTANCE;
    }

    public static ByteMatcher tabsSpacesAndEOL() {
        return TabOrSpaceEOL.INSTANCE;
    }

    public static ByteMatcher eol() {
        return EOL.INSTANCE;
    }





    private static class EOL implements ByteMatcher {
        public static final ByteMatcher INSTANCE = new EOL();

        public void parse( InputBytes source, long fromInc, long toExc, ParserResult result ) {
            long pos = fromInc;

            for ( ; pos < toExc; pos++ ) {
                byte b = source.readByte(pos);

                if ( b != '\n' && b != '\r' ) {
                    break;
                }
            }

            if ( pos == fromInc ) {
                result.resultNoMatch();
            } else {
                result.resultMatchedNoValue( fromInc, pos );
            }
        }

        public String toString() {
            return "EOL";
        }
    }

    private static class TabOrSpace implements ByteMatcher {
        public static final ByteMatcher INSTANCE = new TabOrSpace();

        public void parse( InputBytes source, long fromInc, long toExc, ParserResult result ) {
            long pos = fromInc;

            for ( ; pos < toExc; pos++ ) {
                byte b = source.readByte(pos);

                if ( b != ' ' && b != '\t' ) {
                    break;
                }
            }

            if ( pos == fromInc ) {
                result.resultNoMatch();
            } else {
                result.resultMatchedNoValue( fromInc, pos );
            }
        }

        public String toString() {
            return "tabOrSpace";
        }
    }

    private static class TabOrSpaceEOL implements ByteMatcher {
        public static final ByteMatcher INSTANCE = new TabOrSpaceEOL();

        public void parse( InputBytes source, long fromInc, long toExc, ParserResult result ) {
            long pos = fromInc;

            for ( ; pos < toExc; pos++ ) {
                byte b = source.readByte(pos);

                if ( b != ' ' && b != '\t' && b != '\r' && b != '\n' ) {
                    break;
                }
            }

            if ( pos == fromInc ) {
                result.resultNoMatch();
            } else {
                result.resultMatchedNoValue( fromInc, pos );
            }
        }

        public String toString() {
            return "tabOrSpaceOrEOL";
        }
    }

    private static class ExactMatch implements ByteMatcher<UTF8> {
        private UTF8 target;

        public ExactMatch( UTF8 target ) {
            this.target = target;
        }

        public void parse( InputBytes source, long fromInc, long toExc, ParserResult<UTF8> result ) {
            long   sourceIndex = fromInc;
            int    targetIndex = 0;
            byte[] targetBytes = target.getBytes();


            int numBytes = targetBytes.length;
            if ( (toExc-fromInc) < numBytes ) {
                result.resultNoMatch();

                return;
            }

            while ( targetIndex < numBytes ) {
                byte b = source.readByte(sourceIndex);

                if ( b != targetBytes[targetIndex] ) {
                    result.resultNoMatch();

                    return;
                }

                sourceIndex++;
                targetIndex++;
            }

            result.resultMatched( target, fromInc, sourceIndex );
        }

        public String toString() {
            return target.toString();
        }
    }

}
