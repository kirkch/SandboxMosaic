package com.mosaic.io.streams;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class EnglishPrettyPrintUtilsTest {

    @Test
    public void writeColumns_truncateToColumnWidth() {
        CapturingCharacterStream out = new CapturingCharacterStream();
        EnglishPrettyPrintUtils p = new EnglishPrettyPrintUtils( out, 3,10 );

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
        EnglishPrettyPrintUtils p = new EnglishPrettyPrintUtils( out, 3,10 );
        p.setColumnHandler( 0, EnglishPrettyPrintUtils.WRAP );

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
    public void writeColumns_wrapFirstColumnToColumnWidth_ensureWholeWordsMoveToNextLineWherePossible() {
        CapturingCharacterStream out = new CapturingCharacterStream();
        EnglishPrettyPrintUtils p = new EnglishPrettyPrintUtils( out, 3,7 );
        p.setColumnHandler( 1, EnglishPrettyPrintUtils.WRAP );

        p.write( 1,"hello jim" );
        p.write( "over","flow" );

        List<String> expected = Arrays.asList(
            "1   hello",
            "    jim",
            "ove flow"
        );

        assertEquals( expected, out.audit );
    }

    @Test
    public void writeColumns_wrapSecondColumnToColumnWidth() {
        CapturingCharacterStream out = new CapturingCharacterStream();
        EnglishPrettyPrintUtils p = new EnglishPrettyPrintUtils( out, 4,2 );
        p.setColumnHandler( 1, EnglishPrettyPrintUtils.WRAP );

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

        EnglishPrettyPrintUtils.printWrapped( out, "data", 10 );
        EnglishPrettyPrintUtils.printWrapped( out, "over flow", 3 );


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

        EnglishPrettyPrintUtils.printPleural( out, "container", 1 );
        out.newLine();

        EnglishPrettyPrintUtils.printPleural( out, "container", 3 );
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

        EnglishPrettyPrintUtils.englishList( out, new String[]{"a", "b", "c"} );
        out.newLine();

        EnglishPrettyPrintUtils.englishList( out, new String[]{"a", "b", "c"}, 1, 2 );
        out.newLine();

        EnglishPrettyPrintUtils.englishList( out, new String[]{"a", "b", "c"}, 1, 3 );
        out.newLine();

        EnglishPrettyPrintUtils.englishList( out, new String[]{"a", "b", "c"}, 0, 1 );
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
        assertEquals( "Foo.", EnglishPrettyPrintUtils.cleanEnglishSentence( "foo" ) );
        assertEquals( "Foo bar.", EnglishPrettyPrintUtils.cleanEnglishSentence( "foo bar" ) );
        assertEquals( " Foo.", EnglishPrettyPrintUtils.cleanEnglishSentence( " foo " ) );
        assertEquals( " Foo bar.\t ", EnglishPrettyPrintUtils.cleanEnglishSentence( " foo bar \t " ) );
    }

    @Test
    public void underscoreCaseToCamelCase() {
        assertEquals( "", EnglishPrettyPrintUtils.underscoreCaseToCamelCase( "" ) );
        assertEquals( null, EnglishPrettyPrintUtils.underscoreCaseToCamelCase( null ) );
        assertEquals( "F", EnglishPrettyPrintUtils.underscoreCaseToCamelCase( "f" ) );
        assertEquals( "Foo", EnglishPrettyPrintUtils.underscoreCaseToCamelCase( "Foo" ) );
        assertEquals( "Foobar", EnglishPrettyPrintUtils.underscoreCaseToCamelCase( "FooBar" ) );
        assertEquals( "Foo", EnglishPrettyPrintUtils.underscoreCaseToCamelCase( "foo" ) );
        assertEquals( "FooBar", EnglishPrettyPrintUtils.underscoreCaseToCamelCase( "foo_bar" ) );
        assertEquals( "FooBar", EnglishPrettyPrintUtils.underscoreCaseToCamelCase( "foo__bar" ) );
    }

}
