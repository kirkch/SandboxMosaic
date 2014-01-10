package com.mosaic.io;

import com.mosaic.lang.CharacterPredicate;
import com.mosaic.lang.Validate;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * An immutable collection of characters. Modification of these characters
 * returns a new immutable instance of Characters.  Supports wrapping different
 * character stores, such as CharBuffer and String.  Designed for use with
 * simple pull parsers such as Tokenizer.
 */
public abstract class Characters implements CharSequence {

    public static final Characters EMPTY = wrapCharBuffer( CharBuffer.allocate( 0 ) );

    public static Characters wrapString( String str ) {
        return wrapCharBuffer( CharBuffer.wrap( str ) );
    }

    public static Characters wrapCharacters( CharSequence seq ) {
        if ( seq instanceof Characters ) {
            return (Characters) seq;
        } else  {
            return wrapCharBuffer( CharBuffer.wrap(seq) );
        }
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


        return new CharactersNIOWrapper(new CharPosition(), copy,0);
    }

    /**
     * Create an instance of Characters containing the remaining contents of buffer, that is the characters between position
     * and limit. Ownership of the src buffer is transferred to the Characters object, it is not defensively copied and
     * thus any background changes to it will damage the immutability of the Characters instance.
     */
    public static Characters wrapCharactersBufferNoCopy( CharBuffer src ) {
        Validate.notNull( src, "src" );

        return new CharactersNIOWrapper(new CharPosition(), src,src.position());
    }



    private final CharPosition position;

    protected Characters( CharPosition position ) {
        this.position = position;
    }


    /**
     * The index of where these characters start from in relation to the start of the original stream of characters. Appending
     * and consuming characters will maintain this value.
     */
    public long getStreamOffset() {
        return this.position.getCharacterOffset();
    }

    public int getLineNumber() {
        return this.position.getLineNumber();
    }

    public int getColumnNumber() {
        return this.position.getColumnNumber();
    }

    /**
     * The number of characters represented by this collection.
     */
    public abstract int length();

    /**
     * Fetch the character at the specified index. The index starts from zero.
     */
    public abstract char charAt( int index );


    public CharPosition getPosition() {
        return position;
    }

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


    public CharSequence subSequence(int start, int end) {
        Validate.withinRange( 0, start, end, this.length(), "start", "end" );

        int    length   = end - start;
        char[] chars    = new char[length];
        int    maxIndex = end-start;

        for ( int i=0; i<maxIndex; i++ ) {
            chars[i] = this.charAt( start+i );
        }

        return new String( chars );
    }

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

    /**
     * Returns true if targetString is at fromIndex.
     */
    public boolean containsAt( String targetString, int fromIndex ) {
        Validate.isGTEZero( fromIndex, "fromIndex" );

        int targetStringLength = targetString.length();
        if ( this.length()-fromIndex < targetStringLength ) {
            return false;
        }


        for ( int i=0; i<targetStringLength; i++ ) {
            char c = charAt( i + fromIndex );
            char t = targetString.charAt( i );

            if ( c != t ) {
                return false;
            }
        }

        return true;
    }

    public String toString() {
        int numChars = this.length();

        StringBuilder buf = new StringBuilder( numChars );
        for ( int i=0; i<numChars; i++ ) {
            buf.append( this.charAt( i ) );
        }

        return buf.toString();
    }

    /**
     * Extract the string starting from startIndexInc (inclusive) and endIndexExc (exclusive).
     */
    public String toString( int startIndexInc, int endIndexExc ) {
        Validate.withinRange( 0, startIndexInc, endIndexExc, this.length(), "startIndexInc", "endIndexExc" );

        StringBuilder buf = new StringBuilder( endIndexExc-startIndexInc );

        for ( int i=startIndexInc; i<endIndexExc; i++ ) {
            buf.append( this.charAt(i) );
        }

        return buf.toString();
    }

    public Characters skipWhile( CharacterPredicate predicate ) {
        int length = length();
        int i      = 0;

        while ( i < length && predicate.matches(this.charAt(i)) ) {
            i++;
        }

        if ( i == 0 ) {
            return this;
        }

        return skipCharacters(i);
    }

    public boolean hasContents() {
        return length() > 0;
    }
}



class CharactersNIOWrapper extends Characters {
    private final CharBuffer buf;
    private final int        bufOffset;

    CharactersNIOWrapper( CharPosition position, CharBuffer src, int bufOffset ) {
        super( position );

        this.buf       = src;
        this.bufOffset = bufOffset;
    }

    public int length() {
        return buf.remaining() - bufOffset;
    }

    public char charAt( int index ) {
        return buf.get( index + bufOffset );
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
        buckets.add( other.setStreamOffset(this.getStreamOffset()+this.length()) );

        return new CharactersMultiBucketWrapper( this.getPosition(), buckets );
    }

    public Characters skipCharacters( int numCharacters ) {
        Validate.isLTE( numCharacters, this.length(), "numCharacters" );

        if ( numCharacters == 0 ) {
            return this;
        }

        CharPosition newPosition = this.getPosition().walkCharacters( numCharacters, this );
        return new CharactersNIOWrapper( newPosition, this.buf, this.bufOffset +numCharacters );
    }

    public void writeTo( CharBuffer targetBuffer, int numCharacters ) {
        Validate.isLTE( numCharacters, this.length(), "numCharacters" );

        CharBuffer buf   = this.buf;
        int        limit = numCharacters+this.bufOffset;

        for ( int i=buf.position()+this.bufOffset; i<limit; i++ ) {
            targetBuffer.put( buf.get(i) );
        }
    }

    Characters setStreamOffset( long newStreamOffset ) {
        return new CharactersNIOWrapper( this.getPosition().setCharacterOffset(newStreamOffset), this.buf, this.bufOffset );
    }
}


/**
 * Wraps other instances of Characters. Allows appending instances together without copying all of the characters around. Also
 * allows for the deallocation of consumed characters on the fly, again without copying characters around.
 */
class CharactersMultiBucketWrapper extends Characters {
    private final List<Characters> buckets;

    CharactersMultiBucketWrapper( CharPosition pos, List<Characters> buckets ) {
        super(pos);

        this.buckets = buckets;
    }

    public int length() {
        int remaining = 0;

        for ( Characters b : buckets ) {
            remaining += b.length();
        }

        return remaining;
    }

    public char charAt( final int index ) {
        int i = index;
        for ( Characters b : buckets ) {
            int length = b.length();

            if ( i < length ) {
                return b.charAt( i );
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

        List<Characters> newCharacters = new ArrayList(this.buckets.size()+1);
        newCharacters.addAll( this.buckets );
        newCharacters.add(other.setStreamOffset(this.getStreamOffset()+this.length()));

        return new CharactersMultiBucketWrapper( this.getPosition(), newCharacters );
    }

    public Characters skipCharacters( int numCharacters ) {
        Validate.isLTE( numCharacters, this.length(), "numCharacters" );

        if ( numCharacters == 0 ) {
            return this;
        }

        int              charactersLeftToConsume = numCharacters;
        List<Characters> newBuckets              = new ArrayList(this.buckets.size());
        CharPosition     pos                     = this.getPosition();

        for ( Characters bucket : buckets ) {
            if ( charactersLeftToConsume == 0 ) {
                newBuckets.add( bucket );
            } else {
                int numCharactersInBucket = bucket.length();
                if ( charactersLeftToConsume < numCharactersInBucket ) {
                    newBuckets.add( bucket.skipCharacters(charactersLeftToConsume) );

                    pos = pos.walkCharacters( charactersLeftToConsume, bucket );
                    charactersLeftToConsume = 0;
                } else {
                    charactersLeftToConsume -= numCharactersInBucket;
                    pos = pos.walkCharacters( numCharactersInBucket, bucket );
                }
            }
        }

        return new CharactersMultiBucketWrapper( pos, newBuckets );
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
        return new CharactersMultiBucketWrapper( this.getPosition().setCharacterOffset(newStreamOffset), this.buckets );
    }
}