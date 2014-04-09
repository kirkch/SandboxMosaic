package com.mosaic.lang.text;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.bytes.InputBytes;
import com.mosaic.lang.NotThreadSafe;
import com.mosaic.lang.QA;

import java.util.ArrayList;
import java.util.List;


/**
 * A Pull Parser parses only upon request, and then only as much as the request
 * asks for. <p/>
 *
 * This implementation is designed for low GC and copying overhead.
 */
@NotThreadSafe
@SuppressWarnings("unchecked")
public class PullParser {

    public static PullParser wrap( String name, String txt ) {
        Bytes hdr = Bytes.wrap( txt );

        return new PullParser( name, hdr );
    }

    public static List<String> toLines( Bytes bytes ) {
        List<String>  lines = new ArrayList<>();
        PullParser    p     = new PullParser(bytes);

        StringBuilder buf   = new StringBuilder(200);

        while ( p.hasMore() ) {
            p.pullLine(buf);

            lines.add( buf.toString() );

            buf.setLength( 0 );
        }

        return lines;
    }


    private String     name;
    private long       position = 0;
    private InputBytes source;


    public PullParser( InputBytes bytes ) {
        this( bytes.getName(), bytes );
    }

    public PullParser( String name, InputBytes bytes ) {
        QA.argNotBlank( name, "name" );
        QA.argNotNull( bytes, "bytes" );

        this.name   = name;
        this.source = bytes;
    }


    // pullCustom()
    // pullInt()
    // pullLong()
    // pullVoid()
    //
    // pullOptional()
    // autoSkip()
    //
    // collectZeroOrMore
    // collectOneOrMore


    private ParserResult     result         = new ParserResult();
    private DecodedCharacter charBuf        = new DecodedCharacter();

    private ByteMatcher      autoSkipParser = NoOpParser.INSTANCE;

    public void autoSkip( ByteMatcher skipParser ) {
        QA.argNotNull( skipParser, "skipParser" );

        this.autoSkipParser = skipParser;
    }


    public String getName() {
        return name;
    }

    public boolean hasMore() {
        return position < source.bufferLength();
    }

    /**
     *
     * @return null if at the end of the file
     */
    public void pullLine( StringBuilder buf ) {
        doAutoSkip();

        long i=position;
        for ( ; i<source.getEndIndexExc(); i+=charBuf.numBytesConsumed ) {
            source.readUTF8Character( i, charBuf );

            if ( charBuf.c == '\n' ) {
                i += charBuf.numBytesConsumed;

                setPosition( i );

                return;
            } else if ( charBuf.c == '\r' ) {
                // skip
            } else {
                buf.append(charBuf.c);
            }
        }

        setPosition( i );
    }

    /**
     * Expects the specified parser to match.  If it does not then an exception
     * will be thrown.
     */
    public <T> T pullCustom( ByteMatcher<T> p ) {
        doAutoSkip();

        ParserResult<T> r = parse( p );

        if ( r.hasMatched() ) {
            this.position = r.getToExc();

            return r.getValue();
        } else {
            throw newParseException( "Expected '" + p + "'", position );
        }
    }



    /**
     * Matches and returns an int.  If no int is matched then an exception
     * will be thrown.
     */
    public int pullInt() {
        doAutoSkip();

        ParserResult r = parse( INT_PARSER );

        if ( r.hasMatched() ) {
            this.position = r.getToExc();

            return r.getValueInt();
        } else {
            throw newParseException( "Expected 'int'", position );
        }
    }

    /**
     * Matches and returns an long.  If no long is matched then an exception
     * will be thrown.
     */
    public long pullLong() {
        doAutoSkip();

        ParserResult r = parse( LONG_PARSER );

        if ( r.hasMatched() ) {
            this.position = r.getToExc();

            return r.getValueLong();
        } else {
            throw newParseException( "Expected 'long'", position );
        }
    }



    /**
     * Matches numbers up with up to 3dp, and returns them as an integer in
     * tenths of the minor currency.  Thus numbers in pounds and pence such as
     * 4.23 pounds will be returned as 4230 (tenths of a pence).
     */
    public int pullSmallCashMajorUnit() {
        doAutoSkip();

        ParserResult r = parse( SMALLCASHMAJOR_PARSER );

        if ( r.hasMatched() ) {
            this.position = r.getToExc();

            return r.getValueInt();
        } else {
            throw newParseException( "Expected 'smallcash'", position );
        }
    }

    /**
     * Matches numbers up with up to 1dp, and returns them as an integer
     * in tenths of the minor currency.  Thus numbers in pence such as 4.23 pence
     * will be returned as 42 (tenths of a pence).
     */
    public int pullSmallCashMinorUnit() {
        doAutoSkip();

        ParserResult r = parse( SMALLCASHMINOR_PARSER );

        if ( r.hasMatched() ) {
            this.position = r.getToExc();

            return r.getValueInt();
        } else {
            throw newParseException( "Expected 'smallcash'", position );
        }
    }

    /**
     * Matches numbers up with up to 4dp, and returns them as a long in
     * hundredths of the minor currency.  Thus numbers in pounds and pence such as
     * 4.23 pounds will be returned as 42300 (hundredths of a pence).
     */
    public long pullBigCashMajorUnit() {
        doAutoSkip();

        ParserResult r = parse( BIGCASHMAJOR_PARSER );

        if ( r.hasMatched() ) {
            this.position = r.getToExc();

            return r.getValueLong();
        } else {
            throw newParseException( "Expected 'bigcash'", position );
        }
    }

    /**
     * Matches numbers up with up to 2dp, and returns them as a long in
     * hundredths of the minor currency.  Thus numbers in pence such as
     * 4.23 pence will be returned as 423 (hundredths of a pence).
     */
    public long pullBigCashMinorUnit() {
        doAutoSkip();

        ParserResult r = parse( BIGCASHMINOR_PARSER );

        if ( r.hasMatched() ) {
            this.position = r.getToExc();

            return r.getValueLong();
        } else {
            throw newParseException( "Expected 'bigcash'", position );
        }
    }

    /**
     * Matches and returns a float.  If no float is matched then an exception
     * will be thrown.
     */
    public float pullFloat() {
        doAutoSkip();

        ParserResult r = parse( FLOAT_PARSER );

        if ( r.hasMatched() ) {
            this.position = r.getToExc();

            return r.getValueFloat();
        } else {
            throw newParseException( "Expected 'float'", position );
        }
    }

    /**
     * Matches and returns a double.  If no double is matched then an exception
     * will be thrown.
     */
    public double pullDouble() {
        doAutoSkip();

        ParserResult r = parse( DOUBLE_PARSER );

        if ( r.hasMatched() ) {
            this.position = r.getToExc();

            return r.getValueDouble();
        } else {
            throw newParseException( "Expected 'double'", position );
        }
    }

    /**
     * Matches the specified parser but does not return a result.  For parsers
     * that never generate a response, or lazily create it then this will be
     * more efficient than the other pull methods.
     */
    public void pullVoid( ByteMatcher p ) {
        doAutoSkip();

        ParserResult r = parse( p );

        if ( r.hasMatched() ) {
            this.position = r.getToExc();
        } else {
            throw newParseException( "Expected '"+p+"'", position );
        }
    }

    /**
     * Optionally match the specified parser.  Does not distinguish between
     * a parser returning null and the parser not matching anything.
     */
    public <T> T optionallyPull( ByteMatcher<T> p ) {
        doAutoSkip();

        ParserResult<T> r = parse( p );

        if ( r.hasMatched() ) {
            this.position = r.getToExc();

            return r.getValue();
        } else {
            return null;
        }
    }

    /**
     * Optionally match the specified parser.  Does not distinguish between
     * a matcher returning null and the matcher not matching anything.
     */
    public void optionallyPullVoid( ByteMatcher p ) {
        doAutoSkip();

        p.parse( source, position, source.getEndIndexExc(), result );

        if ( result.hasMatched() ) {
            this.position = result.getToExc();
        }
    }

    /**
     * Moves the current position to the start of the previous line.  If already
     * at the start of the region then the pointer will not move.
     *
     * @return number of bytes reversed over.
     */
    public long rewindLine() {
        long minInc = source.startIndex();

        long i     = position-1;
        long count = 0;
        for ( ; i>= minInc; i-- ) { // reverse over 'current' eol marker
            if ( !isEOL(i) ) {
                break;
            }

            count++;
        }

        for ( ; i>= minInc; i-- ) { // reverse up to previous eol marker
            if ( isEOL(i) ) {
                break;
            }

            count++;
        }

        this.setPosition( position - count );

        return count;
    }

    private boolean isEOL( long i ) {
        byte b = source.readByte( i );

        return b == '\n' || b == '\r';
    }

    public void setPosition( long i ) {
        position = i;
    }

    public long getPosition() {
        return position;
    }

    public long getEndIndexExc() {
        return source.getEndIndexExc();
    }


    private void doAutoSkip() {
        autoSkipParser.parse( source, position, source.getEndIndexExc(), result );

        if ( result.hasMatched() ) {
            this.position = result.getToExc();
        }
    }

    private ParseException newParseException( String msg, long position ) {
        return ParseException.newParseException( source, position, name, msg );
    }

    private <T> ParserResult<T> parse( ByteMatcher<T> p ) {
        ParserResult<T> r = result;  // NB trick to avoid casting at runtime; uses compile time erasure instead

        try {
            p.parse( source, position, source.getEndIndexExc(), r );
        } catch ( Throwable ex ) {
            throw ParseException.newParseException( source, position, name, ex.getMessage(), ex );
        }

        return r;
    }





    private static final ByteMatcher INT_PARSER = new ByteMatcher() {
        public void parse( InputBytes source, long fromInc, long toExc, ParserResult result ) {
            int num = 0;

            long i=fromInc;
            byte v = source.readByte(i);

            boolean isNeg = v == '-';
            if ( isNeg ) {
                i += 1;
            }

            for ( ; i<toExc; i++ ) {
                v = source.readByte(i);

                if ( v < '0' || v > '9' ) {
                    break;
                } else {
                    num *= 10;
                    num += v - '0';
                }
            }

            if ( isNeg ) {
                if ( i == fromInc+1 ) {
                    result.resultNoMatch();
                } else {
                    int r = -num;

                    QA.argIsLTEZero( r, "num" );

                    result.resultMatchedInt( -num, fromInc, i );
                }
            } else {
                QA.argIsGTEZero( num, "num" );

                if ( i == fromInc  ) {
                    result.resultNoMatch();
                } else {
                    result.resultMatchedInt( num, fromInc, i );
                }
            }
        }

        public String toString() {
            return "Int";
        }
    };


    private static final ByteMatcher SMALLCASHMAJOR_PARSER = new SmallCashParser(3);
    private static final ByteMatcher SMALLCASHMINOR_PARSER = new SmallCashParser(1);

    private static class SmallCashParser implements ByteMatcher {
        private int[] dpMultipliers;

        public SmallCashParser( int numDP ) {
            dpMultipliers = new int[numDP+1];

            int mult = 1;
            for ( int i=numDP; i>=0; i-- ) {
                dpMultipliers[i] = mult;

                mult *= 10;
            }
        }

        public void parse( InputBytes source, long fromInc, long toExc, ParserResult result ) {
            int num = 0;

            long i=fromInc;
            byte v = source.readByte(i);

            boolean isNeg = v == '-';
            if ( isNeg ) {
                i += 1;
            }

            for ( ; i<toExc; i++ ) {
                v = source.readByte(i);

                if ( v < '0' || v > '9' ) {
                    break;
                } else {
                    num *= 10;
                    num += v - '0';
                }
            }


            if ( v == '.' ) {
                i++;

                long firstDecimalDigit = i;
                long maxIndex          = Math.min(i+dpMultipliers.length-1,toExc);

                for ( ; i<maxIndex; i++ ) {
                    v = source.readByte(i);

                    if ( v < '0' || v > '9' ) {
                        break;
                    } else {
                        num *= 10;
                        num += v - '0';
                    }
                }

                int numDecimalDigitsParsed = (int) (i-firstDecimalDigit);
                num *= dpMultipliers[numDecimalDigitsParsed];

                if ( i<toExc ) {  // if we have truncated the input, round the last decimal place
                    v = source.readByte(i);

                    if ( v >= '5' && v <= '9' ) {
                        i++;

                        num += 1;
                    }
                }

                for ( ; i<toExc; i++ ) {  // skip the rest of any digits
                    v = source.readByte(i);

                    if ( v < '0' || v > '9' ) {
                        break;
                    }
                }
            } else {
                num *= dpMultipliers[0];
            }


            if ( isNeg ) {
                if ( i == fromInc+1 ) {
                    result.resultNoMatch();
                } else {
                    int r = -num;

                    QA.argIsLTEZero( r, "num" );

                    result.resultMatchedInt( -num, fromInc, i );
                }
            } else {
                QA.argIsGTEZero( num, "num" );

                if ( i == fromInc  ) {
                    result.resultNoMatch();
                } else {
                    result.resultMatchedInt( num, fromInc, i );
                }
            }
        }

        public String toString() {
            return "smallcash";
        }
    };

    private static final ByteMatcher BIGCASHMAJOR_PARSER = new BigCashParser(4);
    private static final ByteMatcher BIGCASHMINOR_PARSER = new BigCashParser(2);

    private static class BigCashParser implements ByteMatcher {
        private long[] dpMultipliers;

        public BigCashParser( int numDP ) {
            dpMultipliers = new long[numDP+1];

            int mult = 1;
            for ( int i=numDP; i>=0; i-- ) {
                dpMultipliers[i] = mult;

                mult *= 10;
            }
        }

        public void parse( InputBytes source, long fromInc, long toExc, ParserResult result ) {
            long num = 0;

            long i=fromInc;
            byte v = source.readByte(i);

            boolean isNeg = v == '-';
            if ( isNeg ) {
                i += 1;
            }

            for ( ; i<toExc; i++ ) {
                v = source.readByte(i);

                if ( v < '0' || v > '9' ) {
                    break;
                } else {
                    num *= 10;
                    num += v - '0';
                }
            }


            if ( v == '.' ) {
                i++;

                long firstDecimalDigit = i;
                long maxIndex          = Math.min(i+dpMultipliers.length-1,toExc);

                for ( ; i<maxIndex; i++ ) {
                    v = source.readByte(i);

                    if ( v < '0' || v > '9' ) {
                        break;
                    } else {
                        num *= 10;
                        num += v - '0';
                    }
                }

                int numDecimalDigitsParsed = (int) (i-firstDecimalDigit);
                num *= dpMultipliers[numDecimalDigitsParsed];

                if ( i<toExc ) {  // if we have truncated the input, round the last decimal place
                    v = source.readByte(i);

                    if ( v >= '5' && v <= '9' ) {
                        i++;

                        num += 1;
                    }
                }

                for ( ; i<toExc; i++ ) {  // skip the rest of any digits
                    v = source.readByte(i);

                    if ( v < '0' || v > '9' ) {
                        break;
                    }
                }
            } else {
                num *= dpMultipliers[0];
            }


            if ( isNeg ) {
                if ( i == fromInc+1 ) {
                    result.resultNoMatch();
                } else {
                    long r = -num;

                    QA.argIsLTEZero( r, "num" );

                    result.resultMatchedLong( -num, fromInc, i );
                }
            } else {
                QA.argIsGTEZero( num, "num" );

                if ( i == fromInc  ) {
                    result.resultNoMatch();
                } else {
                    result.resultMatchedLong( num, fromInc, i );
                }
            }
        }

        public String toString() {
            return "bigcash";
        }
    };


    private static final ByteMatcher BIGCASH_PARSEROLD = new ByteMatcher() {
        public void parse( InputBytes source, long fromInc, long toExc, ParserResult result ) {
            long num = 0;

            long i=fromInc;
            byte v = source.readByte(i);

            boolean isNeg = v == '-';
            if ( isNeg ) {
                i += 1;
            }

            for ( ; i<toExc; i++ ) {
                v = source.readByte(i);

                if ( v < '0' || v > '9' ) {
                    break;
                } else {
                    num *= 10;
                    num += v - '0';
                }
            }



            if ( v == '.' ) {
                long dpIndex  = i;
                long maxIndex = Math.min(i+5,toExc);

                i++;

                for ( ; i<maxIndex; i++ ) {
                    v = source.readByte(i);

                    if ( v < '0' || v > '9' ) {
                        break;
                    } else {
                        num *= 10;
                        num += v - '0';
                    }
                }

                switch ( (int) (i-dpIndex) ) {
                    case 4:
                        num *= 10;
                        break;
                    case 3:
                        num *= 100;
                        break;
                    case 2:
                        num *= 1000;
                        break;
                    case 1:
                        num *= 10000;
                        break;
                }


                if ( i<toExc ) {  // round the 3rd decimal place
                    v = source.readByte(i);

                    if ( v >= '5' && v <= '9' ) {
                        i++;

                        num += 1;
                    }
                }

                for ( ; i<toExc; i++ ) {  // skip the rest of any digits
                    v = source.readByte(i);

                    if ( v < '0' || v > '9' ) {
                        break;
                    }
                }
            } else {
                num *= 10000;
            }


            if ( isNeg ) {
                if ( i == fromInc+1 ) {
                    result.resultNoMatch();
                } else {
                    long r = -num;

                    QA.argIsLTEZero( r, "num" );

                    result.resultMatchedLong( -num, fromInc, i );
                }
            } else {
                QA.argIsGTEZero( num, "num" );

                if ( i == fromInc  ) {
                    result.resultNoMatch();
                } else {
                    result.resultMatchedLong( num, fromInc, i );
                }
            }
        }

        public String toString() {
            return "bigcash";
        }
    };




    private static final ByteMatcher LONG_PARSER = new ByteMatcher() {
        public void parse( InputBytes source, long fromInc, long toExc, ParserResult result ) {
            long num = 0;

            long i=fromInc;
            byte v = source.readByte(i);

            boolean isNeg = v == '-';
            if ( isNeg ) {
                i += 1;
            }

            for ( ; i<toExc; i++ ) {
                v = source.readByte(i);

                if ( v < '0' || v > '9' ) {
                    break;
                } else {
                    num *= 10;
                    num += v - '0';
                }
            }

            if ( isNeg ) {
                if ( i == fromInc+1 ) {
                    result.resultNoMatch();
                } else {
                    long r = -num;

                    QA.argIsLTEZero( r, "num" );

                    result.resultMatchedLong( r, fromInc, i );
                }
            } else {
                if ( i == fromInc  ) {
                    result.resultNoMatch();
                } else {
                    QA.argIsGTEZero( num, "num" );

                    result.resultMatchedLong( num, fromInc, i );
                }
            }
        }

        public String toString() {
            return "Long";
        }
    };

    private static final ByteMatcher FLOAT_PARSER = new ByteMatcher() {
        public void parse( InputBytes source, long fromInc, long toExc, ParserResult result ) {
            byte v = source.readByte(fromInc);

            boolean isNeg;
            long    isNegOffset;
            if ( v == '-' ) {
                isNeg       = true;
                isNegOffset = 1;
            } else {
                isNeg       = false;
                isNegOffset = 0;
            }


            parseIntegerPart( source, fromInc+isNegOffset, toExc, result );

            if ( result.hasMatched() ) {
                parseDecimalPart( source, result.getValueFloat(), result.getToExc(), toExc, result );

                if ( isNeg ) {
                    result.setValueFloat( -result.getValueFloat() );
                    result.setFrom( fromInc );
                }
            }
        }

        private void parseIntegerPart( InputBytes source, long fromInc, long toExc, ParserResult result ) {
            float num = 0;

            long i=fromInc;
            for ( ; i<toExc; i++ ) {
                byte v = source.readByte(i);

                if ( v < '0' || v > '9' ) {
                    break;
                } else {
                    num *= 10;
                    num += v - '0';
                }
            }

            if ( i == fromInc  ) {
                result.resultNoMatch();
            } else {
                result.resultMatchedFloat( num, fromInc, i );
            }
        }

        /**
         * Matches from the end of the whole number part. Starts off expecting a decimal.
         *
         * @param fromInc where to expect the '.'
         * @param result contains the result so far, including where the parsing started from originally
         */
        private void parseDecimalPart( InputBytes source, float num, long fromInc, long toExc, ParserResult result ) {
            if ( fromInc >= toExc ) {
                return;
            }

            byte v = source.readByte(fromInc);

            if ( v != '.' ) {
                return;
            }

            float offsetCount = 1;
            long i=fromInc+1;
            for ( ; i<toExc; i++ ) {
                v = source.readByte(i);

                if ( v < '0' || v > '9' ) {
                    break;
                } else {
                    offsetCount *= 10;
                    num *= 10;
                    num += v - '0';
                }
            }

            num /= offsetCount;

            result.resultMatchedFloat( num, result.getFrom(), i );
        }

        public String toString() {
            return "Float";
        }
    };


    private static final ByteMatcher DOUBLE_PARSER = new ByteMatcher() {
        public void parse( InputBytes source, long fromInc, long toExc, ParserResult result ) {
            byte v = source.readByte(fromInc);

            boolean isNeg;
            long    isNegOffset;
            if ( v == '-' ) {
                isNeg       = true;
                isNegOffset = 1;
            } else {
                isNeg       = false;
                isNegOffset = 0;
            }


            parseIntegerPart( source, fromInc+isNegOffset, toExc, result );

            if ( result.hasMatched() ) {
                parseDecimalPart( source, result.getValueDouble(), result.getToExc(), toExc, result );

                if ( isNeg ) {
                    result.setValueDouble( -result.getValueDouble() );
                    result.setFrom( fromInc );
                }
            }

        }

        private void parseIntegerPart( InputBytes source, long fromInc, long toExc, ParserResult result ) {
            double num = 0;

            long i=fromInc;
            for ( ; i<toExc; i++ ) {
                byte v = source.readByte(i);

                if ( v < '0' || v > '9' ) {
                    break;
                } else {
                    num *= 10;
                    num += v - '0';
                }
            }

            if ( i == fromInc  ) {
                result.resultNoMatch();
            } else {
                result.resultMatchedDouble( num, fromInc, i );
            }
        }

        /**
         * Matches from the end of the whole number part. Starts off expecting a decimal.
         *
         * @param fromInc where to expect the '.'
         * @param result contains the result so far, including where the parsing started from originally
         */
        private void parseDecimalPart( InputBytes source, double num, long fromInc, long toExc, ParserResult result ) {
            if ( fromInc >= toExc ) {
                return;
            }

            byte v = source.readByte(fromInc);

            if ( v != '.' ) {
                return;
            }

            double offsetCount = 1.0;
            long i=fromInc+1;
            for ( ; i<toExc; i++ ) {
                v = source.readByte(i);

                if ( v < '0' || v > '9' ) {
                    break;
                } else {
                    offsetCount *= 10;
                    num *= 10;
                    num += v - '0';
                }
            }

            num /= offsetCount;
            result.resultMatchedDouble( num, result.getFrom(), i );
        }

        public String toString() {
            return "Double";
        }
    };



    private static class NoOpParser implements ByteMatcher {
        public static final ByteMatcher INSTANCE = new NoOpParser();

        public void parse( InputBytes source, long fromInc, long toExc, ParserResult result ) {
            result.resultNoMatch();
        }
    }
}
