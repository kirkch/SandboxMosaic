package com.mosaic.collections;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

/**
 *
 */
public class CircularBufferCharTest_appendReaderTests {

    @Test
    public void emptyReader_expectNoChangeToTheRing() throws IOException {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        assertTrue( buf.append(new StringReader("")) );

        assertEquals( 0, buf.usedCapacity() );
        assertTrue( buf.isEmpty() );
    }

    @Test
    public void readerFitsIntoRingInOneShot_expectTransferOfCharacters() throws IOException {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        assertTrue( buf.append(new StringReader("ab")) );

        assertEquals( 2, buf.usedCapacity() );
        assertFalse( buf.isEmpty() );
        assertEquals( 'a', buf.popFromLHS() );
        assertEquals( 'b', buf.popFromLHS() );
        assertEquals( -1, buf.popFromLHS() );
        assertTrue( buf.isEmpty() );
    }

    @Test
    public void readInTwoSeparateReadersThatBothFitIntoTheSameRingInOneGo_expectTransferOfCharacters() throws IOException {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        assertTrue( buf.append(new StringReader("ab")) );
        assertTrue( buf.append(new StringReader("c")) );

        assertEquals( 3, buf.usedCapacity() );
        assertEquals( 'a', buf.popFromLHS() );
        assertEquals( 'b', buf.popFromLHS() );
        assertEquals( 'c', buf.popFromLHS() );
        assertEquals( -1, buf.popFromLHS() );
        assertTrue( buf.isEmpty() );
    }

    @Test
    public void readInTheFirstPartOfAReaderThatIsTooLargeForTheRing_expectTransferOfCharacters() throws IOException {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        assertTrue( buf.append(new StringReader("abcdefg")) );

        assertEquals( 4, buf.usedCapacity() );
        assertEquals( 'a', buf.popFromLHS() );
        assertEquals( 'b', buf.popFromLHS() );
        assertEquals( 'c', buf.popFromLHS() );
        assertEquals( 'd', buf.popFromLHS() );
        assertEquals( -1, buf.popFromLHS() );
        assertTrue( buf.isEmpty() );
    }

    @Test
    public void tryReadingInWhenTheRingIsFull_expectAppendMethodToReturnFalse() throws IOException {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        StringReader reader = new StringReader( "abcdefg" );
        buf.append( reader );
        assertFalse( buf.append( reader ) );

        assertEquals( 4, buf.usedCapacity() );
        assertEquals( 'a', buf.popFromLHS() );
        assertEquals( 'b', buf.popFromLHS() );
        assertEquals( 'c', buf.popFromLHS() );
        assertEquals( 'd', buf.popFromLHS() );
        assertEquals( -1, buf.popFromLHS() );
        assertTrue( buf.isEmpty() );
    }

    @Test
    public void fullRing_freeUpSomeSpaceFromRHS_thenReadMoreCharacters_expectTransferOfCharacters() throws IOException {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        StringReader reader = new StringReader( "abcdefg" );
        buf.append( reader );
        buf.popFromRHS();
        buf.popFromRHS();

        assertTrue( buf.append( reader ) );

        assertEquals( 4, buf.usedCapacity() );
        assertEquals( 'a', buf.popFromLHS() );
        assertEquals( 'b', buf.popFromLHS() );
        assertEquals( 'e', buf.popFromLHS() );
        assertEquals( 'f', buf.popFromLHS() );
        assertEquals( -1, buf.popFromLHS() );
        assertTrue( buf.isEmpty() );
    }

    @Test
    public void fullRing_freeUpSomeSpaceFromLHS_thenReadMoreCharacters_expectTransferOfCharacters() throws IOException {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        StringReader reader = new StringReader( "abcdefg" );
        buf.append( reader );
        buf.popFromLHS();
        buf.popFromLHS();

        assertTrue( buf.append( reader ) );

        assertEquals( 4, buf.usedCapacity() );
        assertEquals( 'c', buf.popFromLHS() );
        assertEquals( 'd', buf.popFromLHS() );
        assertEquals( 'e', buf.popFromLHS() );
        assertEquals( 'f', buf.popFromLHS() );
        assertEquals( -1, buf.popFromLHS() );
        assertTrue( buf.isEmpty() );
    }

    @Test
    public void allocateCharactersInMiddleOfRing_readEnoughCharactersFromReaderInOneShotToFillEntireRing_expectCharactersFromReaderToWrapInTheRingFillingItEntirely() throws IOException {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        StringReader reader = new StringReader( "abcdefg" );
        buf.append( reader );
        buf.popFromLHS();
        buf.popFromRHS();

        assertTrue( buf.append( reader ) );

        assertEquals( 4, buf.usedCapacity() );
        assertEquals( 'b', buf.popFromLHS() );
        assertEquals( 'c', buf.popFromLHS() );
        assertEquals( 'e', buf.popFromLHS() );
        assertEquals( 'f', buf.popFromLHS() );
        assertEquals( -1, buf.popFromLHS() );
        assertTrue( buf.isEmpty() );
    }

}
