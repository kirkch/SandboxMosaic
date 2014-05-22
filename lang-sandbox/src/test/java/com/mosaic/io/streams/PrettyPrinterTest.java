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
    public void writeColumns_truncateToColumnWidth() {
        CapturingCharacterStream out = new CapturingCharacterStream();
        PrettyPrinter p = new PrettyPrinter( out, 3,10 );

        p.write( 1,"hello" );
        p.write( "over","flow" );

        List<String> expected = Arrays.asList(
            "1   hello",
            "ove flow"
        );

        assertEquals( expected, out.audit );
    }

    @Test
    public void writeColumns_wrapFirstColumnToColumnWidth() {
        CapturingCharacterStream out = new CapturingCharacterStream();
        PrettyPrinter p = new PrettyPrinter( out, 3,10 );
        p.setColumnHandler( 0, PrettyPrinter.WRAP );

        p.write( 1,"hello" );
        p.write( "over","flow" );

        List<String> expected = Arrays.asList(
            "1   hello",
            "ove flow",
            "r"
        );

        assertEquals( expected, out.audit );
    }

    @Test
    public void writeColumns_wrapSecondColumnToColumnWidth() {
        CapturingCharacterStream out = new CapturingCharacterStream();
        PrettyPrinter p = new PrettyPrinter( out, 4,2 );
        p.setColumnHandler( 1, PrettyPrinter.WRAP );

        p.write( 1,"hello" );
        p.write( "over","flow" );

        List<String> expected = Arrays.asList(
            "1    he",
            "     ll",
            "     o",
            "over fl",
            "     ow"
        );

        assertEquals( expected, out.audit );
    }

    @Test
    public void printWrapped() {
        CapturingCharacterStream out = new CapturingCharacterStream();

        PrettyPrinter.printWrapped( out, "data" , 10 );
        PrettyPrinter.printWrapped( out, "over flow", 3 );


        List<String> expected = Arrays.asList(
            "data",
            "ove",
            "r f",
            "low"
        );

        assertEquals( expected, out.audit );
    }

    @Test
    public void printPleural() {
        CapturingCharacterStream out = new CapturingCharacterStream();

        PrettyPrinter.printPleural( out, "container", 1 );
        out.newLine();

        PrettyPrinter.printPleural( out, "container" , 3 );
        out.newLine();


        List<String> expected = Arrays.asList(
            "container",
            "containers"
        );

        assertEquals( expected, out.audit );
    }

    @Test
    public void englishList() {
        CapturingCharacterStream out = new CapturingCharacterStream();

        PrettyPrinter.englishList( out, new String[] {"a","b","c"} );
        out.newLine();

        PrettyPrinter.englishList( out, new String[] {"a","b","c"},1,2 );
        out.newLine();

        PrettyPrinter.englishList( out, new String[] {"a","b","c"},1,3 );
        out.newLine();

        PrettyPrinter.englishList( out, new String[] {"a","b","c"},0,1 );
        out.flush();

        List<String> expected = Arrays.asList(
            "a, b and c",
            "b",
            "b and c",
            "a"
        );

        assertEquals( expected, out.audit );
    }

    @Test
    public void cleanEnglishSentence() {
        assertEquals( "Foo.", PrettyPrinter.cleanEnglishSentence("foo") );
        assertEquals( "Foo bar.", PrettyPrinter.cleanEnglishSentence("foo bar") );
        assertEquals( " Foo.", PrettyPrinter.cleanEnglishSentence(" foo ") );
        assertEquals( " Foo bar.\t ", PrettyPrinter.cleanEnglishSentence(" foo bar \t ") );
    }

    @Test
    public void underscoreCaseToCamelCase() {
        assertEquals( "", PrettyPrinter.underscoreCaseToCamelCase("") );
        assertEquals( null, PrettyPrinter.underscoreCaseToCamelCase(null) );
        assertEquals( "F", PrettyPrinter.underscoreCaseToCamelCase("f") );
        assertEquals( "Foo", PrettyPrinter.underscoreCaseToCamelCase("Foo") );
        assertEquals( "Foobar", PrettyPrinter.underscoreCaseToCamelCase("FooBar") );
        assertEquals( "Foo", PrettyPrinter.underscoreCaseToCamelCase("foo") );
        assertEquals( "FooBar", PrettyPrinter.underscoreCaseToCamelCase("foo_bar") );
        assertEquals( "FooBar", PrettyPrinter.underscoreCaseToCamelCase("foo__bar") );
    }

}
