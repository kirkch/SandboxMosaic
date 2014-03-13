package com.mosaic.io.streams;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class PrettyPrinterTest {

    @Test
    public void writeColumns() {
        CapturingWriter out = new CapturingWriter();
        PrettyPrinter p = new PrettyPrinter( out, 3,10 );

        p.write( 1,"hello" );
        p.write( "over","flow" );

        List<String> expected = Arrays.asList(
            "1   hello      ",
            "ove flow       ",
            ""
        );

        assertEquals( expected, out.audit );
    }

    @Test
    public void printWrapped() {
        CapturingWriter out = new CapturingWriter();

        PrettyPrinter.printWrapped( out, "data" , 10 );
        PrettyPrinter.printWrapped( out, "over flow", 3 );


        List<String> expected = Arrays.asList(
            "data",
            "ove",
            "r f",
            "low",
            ""
        );

        assertEquals( expected, out.audit );
    }

    @Test
    public void printPleural() {
        CapturingWriter out = new CapturingWriter();

        PrettyPrinter.printPleural( out, "container", 1 );
        out.newLine();

        PrettyPrinter.printPleural( out, "container" , 3 );


        List<String> expected = Arrays.asList(
            "container",
            "containers"
        );

        assertEquals( expected, out.audit );
    }

    @Test
    public void englishList() {
        CapturingWriter out = new CapturingWriter();

        PrettyPrinter.englishList( out, new String[] {"a","b","c"} );
        out.newLine();

        PrettyPrinter.englishList( out, new String[] {"a","b","c"},1,2 );
        out.newLine();

        PrettyPrinter.englishList( out, new String[] {"a","b","c"},1,3 );
        out.newLine();

        PrettyPrinter.englishList( out, new String[] {"a","b","c"},0,1 );


        List<String> expected = Arrays.asList(
            "a, b and c",
            "b",
            "b and c",
            "a"
        );

        assertEquals( expected, out.audit );
    }

}