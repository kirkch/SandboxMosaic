package com.mosaic.lang.text;

import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.QA;

import java.io.UTFDataFormatException;


/**
 *
 */
public class UTF8Tools {

    public static int write( byte[] bytes, int pos, char c ) {
        if ( (c >= 0x0000) && (c <= 0x007F) ) {
            Backdoor.setByteIn( bytes, pos, (byte) c );

            return 1;
        } else if ( c > 0x07FF ) {
            Backdoor.setByteIn( bytes, pos,   (byte) (0xE0 | ((c >> 12) & 0x0F)));
            Backdoor.setByteIn( bytes, pos+1, (byte) (0x80 | ((c >> 6) & 0x3F)));
            Backdoor.setByteIn( bytes, pos+2, (byte) (0x80 | (c & 0x3F)));

            return 3;
        } else {
            Backdoor.setByteIn( bytes, pos,   (byte) (0xC0 | ((c >> 6) & 0x1F)));
            Backdoor.setByteIn( bytes, pos+1, (byte) (0x80 | c & 0x3F));

            return 2;
        }
    }

    public static int write( long ptr, long maxAddressExc, char c ) {
        if ( (c >= 0x0000) && (c <= 0x007F) ) {
            QA.argIsLT( ptr, maxAddressExc, "ptr", "maxAddressExc" );

            Backdoor.setByte( ptr, (byte) c );

            return 1;
        } else if ( c > 0x07FF ) {
            QA.argIsLT( ptr + 2, maxAddressExc, "ptr+2", "maxAddressExc" );

            Backdoor.setByte( ptr,   (byte) (0xE0 | ((c >> 12) & 0x0F)));
            Backdoor.setByte( ptr+1, (byte) (0x80 | ((c >> 6) & 0x3F)));
            Backdoor.setByte( ptr+2, (byte) (0x80 | (c & 0x3F)));

            return 3;
        } else {
            QA.argIsLT( ptr + 1, maxAddressExc, "ptr+2", "maxAddressExc" );

            Backdoor.setByte( ptr,   (byte) (0xC0 | ((c >> 6) & 0x1F)));
            Backdoor.setByte( ptr+1, (byte) (0x80 | c & 0x3F));

            return 2;
        }
    }

    public static void decode( byte[] bytes, int pos, DecodedCharacter output ) {
        int c = bytes[pos] & 0xFF;
        switch (c >> 4) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                /* 0xxxxxxx */
                output.c                = (char) c;
                output.numBytesConsumed = 1;
                break;
            case 12:
            case 13: {
                /* 110x xxxx 10xx xxxx */
                int char2 = bytes[pos+1];

                if ( !SystemX.isRecklessRun() ) {
                    if ( (char2 & 0xC0) != 0x80 ) {
                        Backdoor.throwException( new UTFDataFormatException("malformed input around byte") );
                    }
                }

                output.c                = (char) (((c & 0x1F) << 6) | (char2 & 0x3F));
                output.numBytesConsumed = 2;
                break;
            }
            case 14: {
                /* 1110 xxxx 10xx xxxx 10xx xxxx */
                int char2 = bytes[pos+1];
                int char3 = bytes[pos+2];

                if ( !SystemX.isRecklessRun() ) {
                    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80)) {
                        Backdoor.throwException( new UTFDataFormatException("malformed input around byte") );
                    }
                }

                output.c                = (char) (((c & 0x0F) << 12) | ((char2 & 0x3F) << 6) | (char3 & 0x3F));
                output.numBytesConsumed = 3;

                break;
            }
            default:
                /* 10xx xxxx, 1111 xxxx */
                Backdoor.throwException( new UTFDataFormatException("malformed input around byte") );
        }
    }

    public static void decode( long ptr, long maxAddressExc, DecodedCharacter output ) {
        QA.argIsLT( ptr, maxAddressExc, "ptr", "maxAddressExc" );

        int c = Backdoor.getUnsignedByte( ptr );
        switch (c >> 4) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                /* 0xxxxxxx */
                output.c                = (char) c;
                output.numBytesConsumed = 1;
                break;
            case 12:
            case 13: {
                /* 110x xxxx 10xx xxxx */
                QA.argIsLT( ptr + 1, maxAddressExc, "ptr", "maxAddressExc" );

                int char2 = Backdoor.getUnsignedByte( ptr + 1 );

                if ( !SystemX.isRecklessRun() ) {
                    if ( (char2 & 0xC0) != 0x80 ) {
                        Backdoor.throwException( new UTFDataFormatException("malformed input around byte") );
                    }
                }

                output.c                = (char) (((c & 0x1F) << 6) | (char2 & 0x3F));
                output.numBytesConsumed = 2;
                break;
            }
            case 14: {
                /* 1110 xxxx 10xx xxxx 10xx xxxx */
                QA.argIsLT( ptr + 2, maxAddressExc, "ptr", "maxAddressExc" );

                int char2 = Backdoor.getUnsignedByte( ptr + 1 );
                int char3 = Backdoor.getUnsignedByte( ptr + 2 );

                if ( !SystemX.isRecklessRun() ) {
                    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80)) {
                        Backdoor.throwException( new UTFDataFormatException("malformed input around byte") );
                    }
                }

                output.c                = (char) (((c & 0x0F) << 12) | ((char2 & 0x3F) << 6) | (char3 & 0x3F));
                output.numBytesConsumed = 3;

                break;
            }
            default:
                /* 10xx xxxx, 1111 xxxx */
                Backdoor.throwException( new UTFDataFormatException("malformed input around byte") );
        }
    }

    public static int countBytesFor( CharSequence characters ) {
        int count     = 0;
        int seqLength = characters.length();

        char c;
        for ( int i=0; i<seqLength; i++ ) {
            c = characters.charAt(i);
            if ( (c >= 0x0000) && (c <= 0x007F) ) {
                count++;
            } else if ( c > 0x07FF ) {
                count += 3;
            } else {
                count += 2;
            }
        }

        return count;
    }

    public static int countBytesFor( char c ) {
        if ( (c >= 0x0000) && (c <= 0x007F) ) {
            return 1;
        } else if ( c > 0x07FF ) {
            return 3;
        } else {
            return 2;
        }
    }

}
