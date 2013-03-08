package com.mosaic.io;

import com.mosaic.lang.Validate;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * An immutable collection of characters. Modification of these characters returns a new immutable instance of Characters.
 */
public abstract class Characters {

    public static final Characters EMPTY = wrapCharBuffer( CharBuffer.allocate( 0 ) );

    public static Characters wrapString( String str ) {
        return wrapCharBuffer( CharBuffer.wrap( str ) );
    }

    /**
     * Create an instance of Characters containing the remaining contents of buffer, that is the characters between position
     * and limit. Defensively makes a copy of the src buffer. The position of the buffer will be returned back to its
     * original position before completion of this method.
     */
    public static Characters wrapCharBuffer( CharBuffer src ) {
        Validate.notNull( src, "src" );

        int originalPosition = src.position();
        CharBuffer copy = CharBuffer.allocate( src.remaining() );
        copy.put( src );
        copy.flip();

        src.position( originalPosition );


        return new CharactersNIOWrapper(copy,0,0L);
    }

    /**
     * Create an instance of Characters containing the remaining contents of buffer, that is the characters between position
     * and limit. Ownership of the src buffer is transferred to the Characters object, it is not defensively copied and
     * thus any background changes to it will damage the immutability of the Characters instance.
     */
    public static Characters wrapCharactersBufferNoCopy( CharBuffer src ) {
        Validate.notNull( src, "src" );

        return new CharactersNIOWrapper(src,src.position(),0L);
    }

    /**
     * The index of where these characters start from in relation to the start of the original stream of characters. Appending
     * and consuming characters will maintain this value.
     */
    public abstract long streamOffset();

    /**
     * The number of characters represented by this collection.
     */
    public abstract int length();

    /**
     * Fetch the character at the specified index. The index starts from zero.
     */
    public abstract char getChar( int index );

    /**
     * Join two instances of Characters together. Does so by creating a wrapper around both instances of Characters, making them
     * appear as one.
     */
    public abstract Characters appendCharacters( Characters characters );

    /**
     * Jump over the specified number of characters. If this instance of Characters is a wrapper around multiple instances
     * of Characters and all of the characters of the first instance have now been 'consumed' then the reference to that
     * instance will be dropped. Making it possible to GC it.
     */
    public abstract Characters skipCharacters( int numCharacters );

    /**
     * Write the contents of this instance of Characters into the target CharBuffer. If the CharBuffer is too small to take
     * all of the characters then the write will stop at the buffers limit.
     */
    public void writeTo( CharBuffer targetBuffer ) {
        writeTo( targetBuffer, this.length() );
    }

    /**
     * Write the contents of this instance of Characters into the target CharBuffer. If the CharBuffer is too small to take
     * all of the characters then the write will stop at the buffers limit.
     */
    public abstract void writeTo( CharBuffer targetBuffer, int numCharacters );


    /**
     * An internal setter for ensuring that the stream offset is accurate, even when multiple instances of Characters are
     * being appended together.
     */
    abstract Characters setStreamOffset( long newStreamOffset );


    public boolean startsWith( String targetString ) {
        return containsAt( targetString, 0 );
    }

    public boolean containsAt( String targetString, int fromIndex ) {
        Validate.isGTEZero( fromIndex, "fromIndex" );

        int targetStringLength = targetString.length();
        if ( this.length()-fromIndex < targetStringLength ) {
            return false;
        }


        for ( int i=0; i<targetStringLength; i++ ) {
            char c = getChar( i+fromIndex );
            char t = targetString.charAt( i );

            if ( c != t ) {
                return false;
            }
        }

        return true;
    }
}



class CharactersNIOWrapper extends Characters {
    private final CharBuffer buf;
    private final int        positionOffset;
    private final long       streamOffset;

    CharactersNIOWrapper( CharBuffer src, int positionOffset, long streamOffset ) {
        this.buf            = src;
        this.positionOffset = positionOffset;
        this.streamOffset   = streamOffset;
    }

    public long streamOffset() {
        return streamOffset;
    }

    public int length() {
        return buf.remaining() - positionOffset;
    }

    public char getChar( int index ) {
        return buf.get( index + positionOffset );
    }

    public Characters appendCharacters( Characters other ) {
        Validate.notNull( other, "other" );

        if ( this.length() == 0 ) {
            return other;
        } else if ( other.length() == 0 ) {
            return this;
        }

        List<Characters> buckets = new ArrayList(2);
        buckets.add( this );
        buckets.add( other.setStreamOffset(this.streamOffset+this.length()) );

        return new CharactersMultiBucketWrapper( buckets, this.streamOffset );
    }

    public Characters skipCharacters( int numCharacters ) {
        Validate.isLTE( numCharacters, this.length(), "numCharacters" );

        if ( numCharacters == 0 ) {
            return this;
        }

        return new CharactersNIOWrapper( this.buf, this.positionOffset+numCharacters, this.streamOffset+numCharacters );
    }

    public void writeTo( CharBuffer targetBuffer, int numCharacters ) {
        Validate.isLTE( numCharacters, this.length(), "numCharacters" );

        CharBuffer buf   = this.buf;
        int        limit = numCharacters+this.positionOffset;

        for ( int i=buf.position()+this.positionOffset; i<limit; i++ ) {
            targetBuffer.put( buf.get(i) );
        }
    }

    Characters setStreamOffset( long newStreamOffset ) {
        return new CharactersNIOWrapper( this.buf, this.positionOffset, newStreamOffset );
    }
}


/**
 * Wraps other instances of Characters. Allows appending instances together without copying all of the characters around. Also
 * allows for the deallocation of consumed characters on the fly, again without copying characters around.
 */
class CharactersMultiBucketWrapper extends Characters {
    private final List<Characters> buckets;
    private final long        streamOffset;

    CharactersMultiBucketWrapper( List<Characters> buckets, long streamOffset ) {
        this.buckets        = buckets;
        this.streamOffset   = streamOffset;
    }

    public long streamOffset() {
        return streamOffset;
    }

    public int length() {
        int remaining = 0;

        for ( Characters b : buckets ) {
            remaining += b.length();
        }

        return remaining;
    }

    public char getChar( final int index ) {
        int i = index;
        for ( Characters b : buckets ) {
            int length = b.length();

            if ( i < length ) {
                return b.getChar( i );
            } else {
                i -= length;
            }
        }

        throw new IndexOutOfBoundsException( );
    }

    public Characters appendCharacters( Characters other ) {
        Validate.notNull( other, "other" );

        if ( this.length() == 0 ) {
            return other;
        } else if ( other.length() == 0 ) {
            return this;
        }

        List<Characters> newCharacters = new ArrayList(this.buckets.size()+1);  // todo replace with an immutable variant
        newCharacters.addAll(this.buckets);
        newCharacters.add(other.setStreamOffset(this.streamOffset+this.length()));

        return new CharactersMultiBucketWrapper( newCharacters, streamOffset );
    }

    public Characters skipCharacters( int numCharacters ) {
        Validate.isLTE( numCharacters, this.length(), "numCharacters" );

        if ( numCharacters == 0 ) {
            return this;
        }

        int         charactersLeftToConsume = numCharacters;
        List<Characters> newBuckets         = new ArrayList(this.buckets.size());

        for ( Characters bucket : buckets ) {
            if ( charactersLeftToConsume == 0 ) {
                newBuckets.add( bucket );
            } else {
                int numCharactersInBucket = bucket.length();
                if ( charactersLeftToConsume < numCharactersInBucket ) {
                    newBuckets.add( bucket.skipCharacters(charactersLeftToConsume) );

                    charactersLeftToConsume = 0;
                } else {
                    charactersLeftToConsume -= numCharactersInBucket;
                }
            }
        }

        return new CharactersMultiBucketWrapper( newBuckets, this.streamOffset+numCharacters );
    }

    public void writeTo( CharBuffer targetBuffer, final int numCharacters ) {
        Validate.isLTE( numCharacters, this.length(), "numCharacters" );

        int numCharactersLeftToWrite = numCharacters;

        for ( Characters bucket : buckets ) {
            int numCharactersInBucket = bucket.length();

            if ( numCharactersInBucket < numCharactersLeftToWrite ) {
                bucket.writeTo( targetBuffer, numCharactersLeftToWrite );

                break;
            } else {
                bucket.writeTo( targetBuffer );

                numCharactersLeftToWrite -= numCharactersInBucket;
            }
        }
    }

    Characters setStreamOffset( long newStreamOffset ) {
        return new CharactersMultiBucketWrapper( this.buckets, newStreamOffset );
    }
}