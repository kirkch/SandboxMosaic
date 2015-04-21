package com.mosaic.lang.functional;

import com.mosaic.lang.MultipleExceptions;
import com.mosaic.utils.ListUtils;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;


@SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
public class TryNowTest {


// static tryAll

    @Test
    public void givenNoOps_tryAll_shouldReturnVoid() {
        TryNow.tryAll();
    }

    @Test
    public void givenTwoOpsThatDoNotError_tryAll_shouldInvokeEachOpAndNotError() {
        List<String> audit = new ArrayList<>();

        TryNow.tryAll(
            () -> audit.add("A"),
            () -> audit.add("B")
        );

        assertEquals( asList( "A", "B" ), audit );
    }

    @Test
    public void givenThreeOpsWhereTheMiddleOneErrors_tryAll_shouldInvokeEachOpAndThrowsTheError() {
        List<String> audit = new ArrayList<>();

        try {
            TryNow.tryAll(
                () -> audit.add( "A" ),
                () -> {throw new FileNotFoundException( "catch me" );},
                () -> audit.add( "C" )
            );

            fail("expected MultipleExceptions" );
        } catch ( MultipleExceptions ex ) {
            assertEquals( asList("catch me"), ListUtils.map(ex.getUnderlyingExceptions(), Throwable::getMessage) );
            assertEquals( asList(FileNotFoundException.class), ListUtils.map(ex.getUnderlyingExceptions(), Throwable::getClass) );
        }

        assertEquals( asList("A", "C"), audit );
    }

    @Test
    public void givenThreeOpsWhereTheLastTwoError_tryAll_shouldInvokeEachOpAndThrowsTheError() {
        List<String> audit = new ArrayList<>();

        try {
            TryNow.tryAll(
                () -> audit.add( "A" ),
                () -> {throw new FileNotFoundException( "catch 1" );},
                () -> {throw new IOException( "catch 2" );}
            );

            fail("expected MultipleExceptions" );
        } catch ( MultipleExceptions ex ) {
            assertEquals(
                asList("catch 1", "catch 2"),
                ListUtils.map(ex.getUnderlyingExceptions(), Throwable::getMessage)
            );

            assertEquals(
                asList(FileNotFoundException.class, IOException.class),
                ListUtils.map(ex.getUnderlyingExceptions(), Throwable::getClass)
            );
        }

        assertEquals( asList("A"), audit );
    }

}