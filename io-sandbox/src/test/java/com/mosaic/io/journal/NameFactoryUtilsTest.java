package com.mosaic.io.journal;

import com.mosaic.lang.functional.Function0;
import com.softwaremosaic.junit.JUnitMosaic;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToLongFunction;
import java.util.stream.LongStream;

import static org.junit.Assert.*;


public class NameFactoryUtilsTest {

    @Test
    public void createSequenceNameFactory_singleThreadBehaviour() {
        Function0<String> factory = NameFactoryUtils.createSequenceNameFactory( "abc" );

        for ( int i=1; i<=100; i++ ) {
            assertEquals( "abc"+i, factory.invoke() );
        }
    }

    /**
     * createSequenceNameFactory alters each name by incrementing a number by one each time.
     * to test, verify that for n calls that the sequence number was incremented by one
     * each time.. with no gaps or duplications.
     */
    @Test
    public void createSequenceNameFactory_multiThreadedBehaviour() {
        Function0<String> factory = NameFactoryUtils.createSequenceNameFactory( "abc" );

        List<List<String>> results = JUnitMosaic.multiThreaded( numIterations -> {
            List<String> generatedNames = new ArrayList<>(numIterations);

            for ( int i=0; i<numIterations; i++ ) {
                generatedNames.add( factory.invoke() );
            }

            return generatedNames;
        } );


        ToLongFunction<String> extractSeq = s -> Long.parseLong( s.substring("abc".length()) );

        long[] extractedSeqs = results.stream().flatMap(List::stream).mapToLong(extractSeq).sorted().toArray();
        long[] expectedSeqs  = LongStream.rangeClosed(1, extractedSeqs.length).toArray();

        assertArrayEquals( expectedSeqs, extractedSeqs );
    }

}