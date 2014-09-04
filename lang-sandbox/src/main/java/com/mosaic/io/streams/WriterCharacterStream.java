package com.mosaic.io.streams;

import com.mosaic.io.RuntimeIOException;
import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.BigCashType;
import com.mosaic.lang.QA;
import com.mosaic.lang.SmallCashType;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.text.UTF8;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;


/**
 * Bridges CharacterStreams with java.io.Writer.  This code has not been optimised, and will
 * be fairly slow.
 */
public class WriterCharacterStream implements CharacterStream {

    private Writer out;
    private boolean isEnabled = true;


    public WriterCharacterStream( Writer out ) {
        QA.argNotNull( out, "out" );

        this.out = out;
    }


    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled( boolean flag ) {
        this.isEnabled = flag;
    }

    public void writeBoolean( boolean v ) {
        if ( isEnabled ) {
            try {
                out.write( v ? "true" : "false" );
            } catch ( IOException ex ) {
                throw new RuntimeIOException( ex );
            }
        }
    }

    public void writeByteAsNumber( byte v ) {
        if ( isEnabled ) {
            try {
                out.append( Byte.toString( v ) );
            } catch ( IOException ex ) {
                throw new RuntimeIOException( ex );
            }
        }
    }

    public void writeUTF8Bytes( Bytes bytes ) {
        if ( isEnabled ) {
            try {
                out.write( new UTF8( bytes, 0, bytes.bufferLength() ).toString() );
            } catch ( IOException ex ) {
                throw new RuntimeIOException( ex );
            }
        }
    }

    public void writeUTF8Bytes( Bytes bytes, int fromIndexInc, int toExc ) {
        if ( isEnabled ) {
            try {
                out.write( new UTF8( bytes, fromIndexInc, toExc ).toString() );
            } catch ( IOException ex ) {
                throw new RuntimeIOException( ex );
            }
        }
    }

    public void writeUTF8Bytes( byte[] bytes ) {
        if ( isEnabled ) {
            try {
                out.write( new String( bytes, SystemX.UTF8 ) );
            } catch ( IOException ex ) {
                throw new RuntimeIOException( ex );
            }
        }
    }

    public void writeUTF8Bytes( byte[] bytes, int fromIndexInc, int toExc ) {
        if ( isEnabled ) {
            try {
                String str = new String( bytes, fromIndexInc, toExc - fromIndexInc, SystemX.UTF8 );

                out.write( str );
            } catch ( IOException ex ) {
                throw new RuntimeIOException( ex );
            }
        }
    }

    public void writeCharacter( char v ) {
        if ( isEnabled ) {
            try {
                out.write( v );
            } catch ( IOException ex ) {
                throw new RuntimeIOException( ex );
            }
        }
    }

    public void writeCharacters( char[] chars ) {
        if ( isEnabled ) {
            try {
                out.write( chars );
            } catch ( IOException ex ) {
                throw new RuntimeIOException( ex );
            }
        }
    }

    public void writeCharacters( char[] chars, int fromIndexInc, int toExc ) {
        if ( isEnabled ) {
            try {
                out.write( chars, fromIndexInc, toExc - fromIndexInc );
            } catch ( IOException ex ) {
                throw new RuntimeIOException( ex );
            }
        }
    }

    public void writeShort( short v ) {
        if ( isEnabled ) {
            try {
                out.write( Short.toString( v ) );
            } catch ( IOException ex ) {
                throw new RuntimeIOException( ex );
            }
        }
    }

    public void writeUnsignedShort( int v ) {
        if ( isEnabled ) {
            QA.isUnsignedShort( v, "v" );

            try {
                out.write( Integer.toString( v ) );
            } catch ( IOException ex ) {
                throw new RuntimeIOException( ex );
            }
        }
    }

    public void writeInt( int v ) {
        if ( isEnabled ) {
            try {
                out.write( Integer.toString( v ) );
            } catch ( IOException ex ) {
                throw new RuntimeIOException( ex );
            }
        }
    }

    public void writeFixedWidthInt( int v, int fixedWidth, byte paddingByte ) {
        if ( isEnabled ) {
            String numString = Integer.toString( Math.abs( v ) );
            int prefixLength = fixedWidth - numString.length();

            try {
                if ( v < 0 ) {
                    out.write( '-' );

                    prefixLength--;
                }

                for ( int i = 0; i < prefixLength; i++ ) {
                    out.write( (char) paddingByte );
                }

                out.write( numString );
            } catch ( IOException ex ) {
                throw new RuntimeIOException( ex );
            }
        }
    }

    public void writeSmallCashMajorUnit( int v ) {
        if ( isEnabled ) {
            int major = Math.abs( SmallCashType.extractMajorComponent( v ) );
            int minor = Math.abs( SmallCashType.extractMinorComponent( v ) );

            if ( v < 0 ) {
                writeCharacter( '-' );
            }

            writeInt( major );
            writeCharacter( '.' );
            writeFixedWidthInt( minor, 2, (byte) '0' );
        }
    }

    public void writeSmallCashMinorUnit( int v ) {
        if ( isEnabled ) {
            int major = v / 10;
            int minor = Math.abs( v % 10 );

            if ( v < 0 ) {
                writeCharacter( '-' );

                major = Math.abs( major );
            }

            writeInt( major );
            writeCharacter( '.' );
            writeInt( minor );
        }
    }

    public void writeBigCashMajorUnit( long v ) {
        if ( isEnabled ) {
            long major = Math.abs( BigCashType.extractMajorComponent( v ) );
            long minor = Math.abs( BigCashType.extractMinorComponent( v ) );

            if ( v < 0 ) {
                writeCharacter( '-' );
            }

            writeLong( major );
            writeCharacter( '.' );
            writeFixedWidthInt( (int) minor, 2, (byte) '0' );
        }
    }

    public void writeBigCashMinorUnit( long v ) {
        if ( isEnabled ) {
            long major = v / 100;
            long minor = Math.abs( v % 100 );

            if ( v < 0 ) {
                writeCharacter( '-' );

                major = Math.abs( major );
            }

            writeLong( major );
            writeCharacter( '.' );

            if ( minor < 10 ) {
                writeCharacter( '0' );
            }

            writeLong( minor );
        }
    }

    public void writeUnsignedInt( long v ) {
        if ( isEnabled ) {
            QA.isUnsignedInt( v, "v" );

            writeLong( v );
        }
    }

    public void writeLong( long v ) {
        if ( isEnabled ) {
            try {
                out.write( Long.toString( v ) );
            } catch ( IOException ex ) {
                throw new RuntimeIOException( ex );
            }
        }
    }

    public void writeFloat( float v ) {
        if ( isEnabled ) {
            try {
                out.write( Float.toString( v ) );
            } catch ( IOException ex ) {
                throw new RuntimeIOException( ex );
            }
        }
    }

    public void writeFloat( float v, int numDecimalPlaces ) {
        if ( isEnabled ) {
            // this strangeness is because 3.145 (2dp) would be 3.14 without it :(
            // that is, 3.145 becomes 3.1449 and so would round the wrong way, so we add 0.00001
            // to correct for this
            float roundingFudge = 1 / (float) Math.pow( 10, numDecimalPlaces + 2 );

            try {
                out.write( String.format( "%." + numDecimalPlaces + "f", v + roundingFudge ) );
            } catch ( IOException ex ) {
                throw new RuntimeIOException( ex );
            }
        }
    }

    public void writeDouble( double v ) {
        if ( isEnabled ) {
            try {
                out.write( Double.toString( v ) );
            } catch ( IOException ex ) {
                throw new RuntimeIOException( ex );
            }
        }
    }

    public void writeDouble( double v, int numDecimalPlaces ) {
        if ( isEnabled ) {
            // decimals do not require rounding fudge as they are higher precision

            try {
                out.write( String.format( "%." + numDecimalPlaces + "f", v ) );
            } catch ( IOException ex ) {
                throw new RuntimeIOException( ex );
            }
        }
    }

    public void writeString( String v ) {
        if ( isEnabled ) {
            try {
                out.write( v );
            } catch ( IOException ex ) {
                throw new RuntimeIOException( ex );
            }
        }
    }

    public void writeLine( String v ) {
        if ( isEnabled ) {
            try {
                out.write( v );
                newLine();
            } catch ( IOException ex ) {
                throw new RuntimeIOException( ex );
            }
        }
    }

    public void writeUTF8( UTF8 v ) {
        if ( isEnabled ) {
            try {
                out.write( v.toString() );
            } catch ( IOException ex ) {
                throw new RuntimeIOException( ex );
            }
        }
    }

    public void writeLine( UTF8 v ) {
        if ( isEnabled ) {
            writeLine( v.toString() );
        }
    }

    public void writeException( Throwable ex ) {
        if ( isEnabled ) {
            ex.printStackTrace( new PrintWriter( out ) );
        }
    }

    public void writeException( String msg, Throwable ex ) {
        if ( isEnabled ) {
            writeLine( msg );
            ex.printStackTrace( new PrintWriter( out ) );
        }
    }

    public void newLine() {
        if ( isEnabled ) {
            try {
                out.write( SystemX.NEWLINE );
            } catch ( IOException ex ) {
                throw new RuntimeIOException( ex );
            }
        }
    }

    public void flush() {}

}
