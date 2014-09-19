package com.mosaic.bytes;


/**
 * Knows how to serialize and de-serialize T.  Assumes that the bytes are shared to reduce
 * copying and objection allocations.
 */
public interface ByteSerializer<T> {
    /**
     *
     * @param v the value to be serialized out to bytes
     * @param b the byte buffer to write into
     * @param base the starting address to write to
     * @param maxExc the maximum address that may be written up to but not including
     *
     * @return the number of bytes written out
     */
    public long encodeInto( T v, Bytes b, long base, long maxExc );
    public T decodeFrom( Bytes b, long base, long maxExc );
}
