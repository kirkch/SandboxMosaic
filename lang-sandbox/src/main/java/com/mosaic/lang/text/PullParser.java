package com.mosaic.lang.text;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.bytes.InputBytes;
import com.mosaic.lang.NotThreadSafe;
import com.mosaic.lang.Validate;


/**
 * A Pull Parser parses only upon request, and then only as much as the request
 * asks for. <p/>
 *
 * This implementation is designed for low GC and copying overhead.
 */
@NotThreadSafe
@SuppressWarnings("unchecked")
public class PullParser {

    public static PullParser wrap( String txt ) {
        Bytes hdr = Bytes.wrap( txt );

        return new PullParser( hdr );
    }



    private long       position = 0;
    private InputBytes source;


    public PullParser( InputBytes bytes ) {
        Validate.argNotNull( bytes, "bytes" );

        source = bytes;
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


    private ParserResult result = new ParserResult();

    private CharacterParser autoSkipParser = NoOpParser.INSTANCE;

    public void autoSkip( CharacterParser skipParser ) {
        Validate.argNotNull( skipParser, "skipParser" );

        this.autoSkipParser = skipParser;
    }




    /**
     * Expects the specified parser to match.  If it does not then an exception
     * will be thrown.
     */
    public <T> T pullCustom( CharacterParser<T> p ) {
        doAutoSkip();

        ParserResult<T> r = result;  // NB trick to avoid casting at runtime; uses compile time erasure instead

        p.parse( source, position, source.endIndexExc(), r );

        if ( result.hasMatched() ) {
            this.position = r.getToExc();

            return r.getValue();
        } else {
            throw new ParseException( "Expected '"+p+"'", position );
        }
    }

    /**
     * Matches and returns an int.  If no int is matched then an exception
     * will be thrown.
     */
    public int pullInt() {
        doAutoSkip();

        INT_PARSER.parse( source, position, source.endIndexExc(), result );

        if ( result.hasMatched() ) {
            this.position = result.getToExc();

            return result.getValueInt();
        } else {
            throw new ParseException( "Expected 'int'", position );
        }
    }

    /**
     * Matches and returns an long.  If no long is matched then an exception
     * will be thrown.
     */
    public long pullLong() {
        doAutoSkip();

        LONG_PARSER.parse( source, position, source.endIndexExc(), result );

        if ( result.hasMatched() ) {
            this.position = result.getToExc();

            return result.getValueLong();
        } else {
            throw new ParseException( "Expected 'long'", position );
        }
    }

    /**
     * Matches and returns a float.  If no float is matched then an exception
     * will be thrown.
     */
    public float pullFloat() {
        doAutoSkip();

        FLOAT_PARSER.parse( source, position, source.endIndexExc(), result );

        if ( result.hasMatched() ) {
            this.position = result.getToExc();

            return result.getValueFloat();
        } else {
            throw new ParseException( "Expected 'float'", position );
        }
    }

    /**
     * Matches and returns a double.  If no double is matched then an exception
     * will be thrown.
     */
    public double pullDouble() {
        doAutoSkip();

        DOUBLE_PARSER.parse( source, position, source.endIndexExc(), result );

        if ( result.hasMatched() ) {
            this.position = result.getToExc();

            return result.getValueDouble();
        } else {
            throw new ParseException( "Expected 'double'", position );
        }
    }

    /**
     * Matches the specified parser but does not return a result.  For parsers
     * that never generate a response, or lazily create it then this will be
     * more efficient than the other pull methods.
     */
    public void pullVoid( CharacterParser p ) {
        doAutoSkip();

        p.parse( source, position, source.endIndexExc(), result );

        if ( result.hasMatched() ) {
            this.position = result.getToExc();
        } else {
            throw new ParseException( "Expected '"+p+"'", position );
        }
    }

    /**
     * Optionally match the specified parser.  Does not distinguish between
     * a parser returning null and the parser not matching anything.
     */
    public <T> T optionallyPull( CharacterParser<T> p ) {
        doAutoSkip();

        ParserResult<T> r = result;  // NB trick to avoid casting at runtime; uses compile time erasure instead

        p.parse( source, position, source.endIndexExc(), r );

        if ( r.hasMatched() ) {
            this.position = r.getToExc();

            return r.getValue();
        } else {
            return null;
        }
    }

    /**
     * Optionally match the specified parser.  Does not distinguish between
     * a parser returning null and the parser not matching anything.
     */
    public void optionallyPullVoid( CharacterParser p ) {
        doAutoSkip();

        p.parse( source, position, source.endIndexExc(), result );

        if ( result.hasMatched() ) {
            this.position = result.getToExc();
        }
    }


    public void setPosition( long i ) {
        position = i;
    }

    public long getPosition() {
        return position;
    }


    private void doAutoSkip() {
        autoSkipParser.parse( source, position, source.endIndexExc(), result );

        if ( result.hasMatched() ) {
            this.position = result.getToExc();
        }
    }







    private static final CharacterParser INT_PARSER = new CharacterParser() {
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

                    Validate.argIsLTEZero( r, "num" );

                    result.resultMatchedInt( -num, fromInc, i );
                }
            } else {
                Validate.argIsGTEZero( num, "num" );

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




    private static final CharacterParser LONG_PARSER = new CharacterParser() {
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

                    Validate.argIsLTEZero( r, "num" );

                    result.resultMatchedLong( r, fromInc, i );
                }
            } else {
                if ( i == fromInc  ) {
                    result.resultNoMatch();
                } else {
                    Validate.argIsGTEZero( num, "num" );

                    result.resultMatchedLong( num, fromInc, i );
                }
            }
        }

        public String toString() {
            return "Long";
        }
    };

    private static final CharacterParser FLOAT_PARSER = new CharacterParser() {
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

            long i=fromInc+1;
            for ( ; i<toExc; i++ ) {
                v = source.readByte(i);

                if ( v < '0' || v > '9' ) {
                    break;
                } else {
                    num /= 10;
                    num += v - '0';
                }
            }

            result.resultMatchedFloat( num, result.getFrom(), i );
        }

        public String toString() {
            return "Float";
        }
    };


    private static final CharacterParser DOUBLE_PARSER = new CharacterParser() {
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

            long i=fromInc+1;
            for ( ; i<toExc; i++ ) {
                v = source.readByte(i);

                if ( v < '0' || v > '9' ) {
                    break;
                } else {
                    num /= 10;
                    num += v - '0';
                }
            }

            result.resultMatchedDouble( num, result.getFrom(), i );
        }

        public String toString() {
            return "Double";
        }
    };



    private static class NoOpParser implements CharacterParser {
        public static final CharacterParser INSTANCE = new NoOpParser();

        public void parse( InputBytes source, long fromInc, long toExc, ParserResult result ) {
            result.resultNoMatch();
        }
    }
}
