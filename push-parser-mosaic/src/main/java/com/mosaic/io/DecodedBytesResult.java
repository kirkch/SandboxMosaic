package com.mosaic.io;

/**
 *
 */
public class DecodedBytesResult {
    public final Bytes      remainingBytes;
    public final Characters decodedCharacters;

    public DecodedBytesResult( Bytes remainingBytes, Characters decodedCharacters ) {
        this.remainingBytes    = remainingBytes;
        this.decodedCharacters = decodedCharacters;
    }
}
