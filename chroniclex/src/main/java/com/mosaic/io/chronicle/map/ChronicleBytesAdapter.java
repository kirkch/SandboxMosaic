package com.mosaic.io.chronicle.map;

import com.mosaic.bytes.BaseBytes;
import com.mosaic.bytes.Bytes;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.text.DecodedCharacter;

import java.io.UTFDataFormatException;

import static com.mosaic.lang.system.Backdoor.toInt;


/**
 *
 */
public class ChronicleBytesAdapter extends BaseBytes {
    private net.openhft.lang.io.Bytes hftBytes;


    protected ChronicleBytesAdapter( net.openhft.lang.io.Bytes hftBytes, long base, long maxExc ) {
        super( base, maxExc );

        this.hftBytes = hftBytes;
    }

    public void resize( long newLength ) {
        throw new UnsupportedOperationException();
    }

    public void fill( long from, long toExc, byte v ) {
        for ( long i = from; i<toExc; i++ ) {
            hftBytes.writeByte( base+i, v );
        }
    }

    public byte readByte( long offset, long maxExc ) {
        return hftBytes.readByte(base+offset);
    }

    public void writeByte( long offset, long maxExc, byte v ) {
        hftBytes.writeByte( base+offset, v );
    }

    public short readShort( long offset, long maxExc ) {
        return hftBytes.readShort(base+offset);
    }

    public void writeShort( long offset, long maxExc, short v ) {
        hftBytes.writeShort( base+offset, v );
    }

    public char readCharacter( long offset, long maxExc ) {
        return hftBytes.readChar(base+offset);
    }

    public void writeCharacter( long offset, long maxExc, char v ) {
        hftBytes.writeChar( base+offset, v );
    }

    public int readInt( long offset, long maxExc ) {
        return hftBytes.readInt( base+offset );
    }

    public void writeInt( long offset, long maxExc, int v ) {
        hftBytes.writeInt( base+offset, v );
    }

    public long readLong( long offset, long maxExc ) {
        return hftBytes.readLong( base+offset );
    }

    public void writeLong( long offset, long maxExc, long v ) {
        hftBytes.writeLong( base+offset, v );
    }

    public float readFloat( long offset, long maxExc ) {
        return hftBytes.readFloat( base+offset );
    }

    public void writeFloat( long offset, long maxExc, float v ) {
        hftBytes.writeFloat( base+offset, v );
    }

    public double readDouble( long offset, long maxExc ) {
        return hftBytes.readDouble( base+offset );
    }

    public void writeDouble( long offset, long maxExc, double v ) {
        hftBytes.writeDouble( base+offset, v );
    }

    public short readUnsignedByte( long offset, long maxExc ) {
        return (short) hftBytes.readUnsignedByte( base+offset );
    }

    public void writeUnsignedByte( long offset, long maxExc, short v ) {
        hftBytes.writeUnsignedByte( base+offset, v );
    }

    public int readUnsignedShort( long offset, long maxExc ) {
        return hftBytes.readUnsignedShort( base+offset );
    }

    public void writeUnsignedShort( long offset, long maxExc, int v ) {
        hftBytes.writeUnsignedShort( base+offset, v );
    }

    public long readUnsignedInt( long offset, long maxExc ) {
        return hftBytes.readUnsignedInt( base+offset );
    }

    public void writeUnsignedInt( long offset, long maxExc, long v ) {
        hftBytes.writeUnsignedInt( base+offset, v );
    }

    public void readUTF8Character( long offset, long maxExc, DecodedCharacter output ) {
        int c = hftBytes.readByte(base+offset) & 0xFF;
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
                int char2 = hftBytes.readByte(base+offset+1);

                if ( !SystemX.isRecklessRun() ) {
                    if ( (char2 & 0xC0) != 0x80 ) {
                        Backdoor.throwException( new UTFDataFormatException( "malformed input around byte" ) );
                    }
                }

                output.c                = (char) (((c & 0x1F) << 6) | (char2 & 0x3F));
                output.numBytesConsumed = 2;
                break;
            }
            case 14: {
                /* 1110 xxxx 10xx xxxx 10xx xxxx */
                int char2 = hftBytes.readByte(base+offset+1);
                int char3 = hftBytes.readByte(base+offset+2);

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

    public int writeUTF8Character( long offset, long maxExc, char c ) {
        if ( c <= 0x007F ) {
            hftBytes.writeByte( base+offset, (byte) c );
            return 1;
        } else if ( c > 0x07FF ) {
            hftBytes.writeByte( base+offset,   (byte) (0xE0 | ((c >> 12) & 0x0F)));
            hftBytes.writeByte( base+offset+1, (byte) (0x80 | ((c >> 6) & 0x3F)));
            hftBytes.writeByte( base+offset+2, (byte) (0x80 | (c & 0x3F)));

            return 3;
        } else {
            hftBytes.writeByte( base+offset,   (byte) (0xC0 | ((c >> 6) & 0x1F)));
            hftBytes.writeByte( base+offset+1, (byte) (0x80 | c & 0x3F));

            return 2;
        }
    }

    public int readBytes( long offset, long maxExc, Bytes destination, long destinationInc, long destinationExc ) {
        long len = Math.min( Math.min(maxExc-offset, destinationExc-destinationInc), hftBytes.limit() );

        for ( long i=0; i<len; i++ ) {
            byte v = hftBytes.readByte( base+offset+i );

            destination.writeByte( destinationInc+i, destinationExc, v );
        }

        return toInt( len );
    }

    public int writeBytes( long offset, long maxExc, Bytes sourceBytes, long sourceInc, long sourceExc ) {
        long len = Math.min( Math.min(maxExc-offset, sourceExc-sourceInc), hftBytes.limit() );

        for ( long i=0; i<len; i++ ) {
            byte v = sourceBytes.readByte( sourceInc+i, sourceExc );

            this.writeByte( offset+i, maxExc, v );
        }

        return toInt( len );
    }

    public int readBytes( long offset, long maxExc, byte[] destinationArray, long destinationArrayInc, long destinationArrayExc ) {
        int len        = toInt( Math.min( Math.min( maxExc - offset, destinationArrayExc - destinationArrayInc ), hftBytes.limit() ) );
        int destOffset = toInt(destinationArrayInc);

        for ( int i=0; i<len; i++ ) {
            byte v = hftBytes.readByte( base+offset+i );

            destinationArray[destOffset+i] = v;
        }

        return len;
    }

    public int writeBytes( long offset, long maxExc, byte[] sourceArray, long sourceArrayInc, long sourceArrayExc ) {
        int len          = toInt( Math.min( Math.min( maxExc - offset, sourceArrayExc - sourceArrayInc ), hftBytes.limit() ) );
        int sourceOffset = toInt(sourceArrayInc);

        for ( int i=0; i<len; i++ ) {
            byte v = sourceArray[sourceOffset+i];

            this.writeByte( offset+i, maxExc, v );
        }

        return len;
    }

    public int readBytes( long offset, long maxExc, long toAddressBase, long toAddressInc, long toAddressExc ) {
        long len = Math.min( Math.min(maxExc-offset, toAddressExc-toAddressInc), hftBytes.limit() );

        for ( long i=0; i<len; i++ ) {
            byte v = hftBytes.readByte( base+offset+i );

            Backdoor.setByte( toAddressBase+toAddressInc+i, v );
        }

        return toInt( len );
    }

    public int writeBytes( long offset, long maxExc, long fromAddressBase, long fromAddressInc, long fromAddressExc ) {
        long len = Math.min( Math.min(maxExc-offset, fromAddressExc-fromAddressInc), hftBytes.limit() );

        for ( long i=0; i<len; i++ ) {
            byte v = Backdoor.getByte( fromAddressBase + fromAddressInc+i );

            this.writeByte( offset+i, maxExc, v );
        }

        return toInt( len );
    }
}
