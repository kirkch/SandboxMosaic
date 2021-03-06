package com.mosaic.bytes;

import com.mosaic.lang.text.DecodedCharacter;
import com.mosaic.lang.text.UTF8Tools;

import static com.mosaic.lang.system.SystemX.NULL_BYTE;


/**
 *
 */
public class ByteSerializers {

    public static <K> ByteSerializer<K> lookup( Class<K> keyType ) {
        return null;
    }


    public static final ByteSerializer<String> NULL_TERMINATED_STRING_SERIALIZER = new ByteSerializer<String>() {
        public long encodeInto( String v, Bytes b, long base, long maxExc ) {
            int  numCharacters = v.length();
            long toAddress     = base;

            for ( int i=0; i<numCharacters; i++ ) {
                char c = v.charAt( i );

                toAddress += b.writeUTF8Character( toAddress, maxExc, c );
            }

            b.writeByte( toAddress, maxExc, NULL_BYTE );

            return (toAddress-base)+1;
        }

        public String decodeFrom( Bytes b, long base, long maxExc ) {
            StringBuilder    buf = new StringBuilder((int) (maxExc-base));
            DecodedCharacter dec = new DecodedCharacter();

            long nextAddress = base;

            do {
                b.readUTF8Character( nextAddress, maxExc, dec );

                if ( dec.c != NULL_BYTE ) {
                    buf.append( dec.c );

                    nextAddress += 1;
                } else {
                    break;
                }
            } while ( nextAddress < maxExc && dec.c != NULL_BYTE );


            return buf.toString();
        }

        public long sizeOf( String v ) {
            return UTF8Tools.countBytesFor(v)+1;
        }
    };

}
