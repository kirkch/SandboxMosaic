package com.mosaic.bytes;

/**
 *
 */
public class ByteMarkerUtils {

    // NB DO NOT CHANGE THESE MARKERS ONCE THEY ARE OUT IN THE WILD
    public static final byte[] QUEUE_HEAP_MARKER      = encode( "BADBOY-001" );
    public static final byte[] FIXED_GRID_HEAP_MARKER = encode( "BADBOY-002" );


    /**
     * Encode the specified string into a byte array where the byte array reads in HEX similar to the
     * supplied string.
     *
     * @example "JAVABABE" -> 0xJAVABABE
     */
    public static byte[] encode( String s ) {
        byte[] bytes    = new byte[(s.length()+1)/2];

        for ( int i=0; i<bytes.length; i++ ) {
            int v = (encode(s.charAt(i*2)) << 4);

            if ( s.length() > i*2+1 ) {
                v |= encode(s.charAt(i*2+1));
            }

            bytes[i] = (byte) v;
        }

        return bytes;
    }

    public static byte encode( char c ) {
        switch (c) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return (byte) (c-'0');
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
                return (byte) (c-'a' + 10);
            case 'g':
                return 6;
            case 'h':
                return 7;
            case 'i':
                return 1;
            case 'j':
                return 1;
            case 'k':
                return 8;
            case 'l':
                return 1;
            case 'm':
                return 3;
            case 'n':
                return 2;
            case 'o':
                return 0;
            case 'p':
                return 9;
            case 'q':
                return 9;
            case 'r':
                return 7;
            case 's':
                return 3;
            case 't':
                return 2;
            case 'u':
                return 6;
            case 'v':
                return 2;
            case 'w':
                return 3;
            case 'x':
                return 2;
            case 'y':
                return 7;
            case 'z':
                return 3;
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
                return (byte) (c-'A' + 10);
            case 'G':
                return 6;
            case 'H':
                return 7;
            case 'I':
                return 1;
            case 'J':
                return 1;
            case 'K':
                return 8;
            case 'L':
                return 1;
            case 'M':
                return 3;
            case 'N':
                return 2;
            case 'O':
                return 0;
            case 'P':
                return 9;
            case 'Q':
                return 9;
            case 'R':
                return 7;
            case 'S':
                return 3;
            case 'T':
                return 2;
            case 'U':
                return 6;
            case 'V':
                return 2;
            case 'W':
                return 3;
            case 'X':
                return 2;
            case 'Y':
                return 7;
            case 'Z':
                return 3;
            default:
                return 0;
        }
    }

}
