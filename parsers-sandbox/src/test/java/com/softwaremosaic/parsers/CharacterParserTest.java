package com.softwaremosaic.parsers;

import com.mosaic.utils.MapUtils;
import com.softwaremosaic.parsers.automata.Label;
import com.softwaremosaic.parsers.automata.LabelNode;
import com.softwaremosaic.parsers.automata.Labels;
import com.softwaremosaic.parsers.automata.Nodes;
import com.softwaremosaic.parsers.automata.ProductionRule;
import com.softwaremosaic.parsers.automata.regexp.GraphBuilder;
import com.softwaremosaic.parsers.automata.regexp.StringOp;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


/**
*
*/
@SuppressWarnings("unchecked")
public class CharacterParserTest {

    private RecordingParserListener l = new RecordingParserListener();


    @Test
    public void givenBlankAutomata_matchA_expectErrorCallback() {
        ProductionRule    rule1  = ProductionRule.terminal( new LabelNode() );
        Parser<Character> parser = new CharacterParser(rule1, Collections.EMPTY_MAP, l);


        parser.consume( 'a' );

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected input 'a', no further input was expected"
        );

        assertEquals( expectedAudit, l.audit );
    }

    @Test
    public void givenBlankAutomata_matchB_expectErrorCallback() {
        ProductionRule    rule1  = ProductionRule.terminal( new LabelNode() );
        Parser<Character> parser = new CharacterParser(rule1, Collections.EMPTY_MAP, l);

        int numCharactersConsumed = consume(parser, "b");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected input 'b', no further input was expected"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenBlankAutomata_matchAB_expectErrorCallback() {
        ProductionRule    rule1  = ProductionRule.terminal( new LabelNode() );
        Parser<Character> parser = new CharacterParser(rule1, Collections.EMPTY_MAP, l);

        int numCharactersConsumed = consume(parser, "ab");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected input 'a', no further input was expected"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingA_parseB_expectError() {
        LabelNode<Character> n = new LabelNode();
        n.append( 'a' );


        ProductionRule    rule1  = ProductionRule.terminal( n );
        Parser<Character> parser = new CharacterParser(rule1, Collections.EMPTY_MAP, l);



        int numCharactersConsumed = consume( parser, "b" );

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected input 'b', expected 'a'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingB_parseA_expectError() {
        LabelNode<Character> n = new LabelNode();
        n.append( 'b' );


        ProductionRule    rule1  = ProductionRule.terminal( n );
        Parser<Character> parser = new CharacterParser(rule1, Collections.EMPTY_MAP, l);



        int numCharactersConsumed = consume(parser, "a");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected input 'a', expected 'b'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAorB_parseC_expectError() {
        LabelNode<Character> n = new LabelNode();
        n.append( 'a' );
        n.append( 'b' );


        ProductionRule    rule1  = ProductionRule.terminal( n );
        Parser<Character> parser = new CharacterParser(rule1, Collections.EMPTY_MAP, l);


        int numCharactersConsumed = consume(parser, "c");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): unexpected input 'c', expected 'a|b'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAB_parseAC_expectErrorAtCol2() {
        LabelNode<Character> n1 = new LabelNode();
        Nodes n2 = n1.append( 'a' );
        n2.append('b');


        ProductionRule    rule1  = ProductionRule.terminal( n1 );
        Parser<Character> parser = new CharacterParser(rule1, Collections.EMPTY_MAP, l);


        int numCharactersConsumed = consume(parser,"ac");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,2): unexpected input 'c', expected 'b'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 1, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAnewlineB_parseAnewlineC_expectErrorAtLine2Col1() {
        LabelNode<Character> n1 = new LabelNode();
        new StringOp( "a\nb", GraphBuilder.CaseSensitivity.CaseSensitive ).appendTo( n1 );


        ProductionRule    rule1  = ProductionRule.terminal( n1 );
        Parser<Character> parser = new CharacterParser(rule1, Collections.EMPTY_MAP, l);


        int numCharactersConsumed = consume(parser,"a\nc");

        List<String> expectedAudit = Arrays.asList(
            "started",
            "(2,1): unexpected input 'c', expected 'b'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 2, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAB_parseAB_expectSuccessfulParsingAndEndOfParsingEvent() {
        LabelNode<Character> n1 = new LabelNode();
        new StringOp( "ab", GraphBuilder.CaseSensitivity.CaseSensitive ).appendTo( n1 );


        ProductionRule    rule1  = ProductionRule.terminal( n1 );
        Parser<Character> parser = new CharacterParser(rule1, Collections.EMPTY_MAP, l);


        int numCharactersConsumed = consume(parser,"ab");

        parser.appendEOS();

        List<String> expectedAudit = Arrays.asList(
                "started",
                "finished"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 2, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAB_parseAThenB_expectStartedParsingEventOnlyOnce() {
        LabelNode<Character> n1 = new LabelNode();
        new StringOp( "ab", GraphBuilder.CaseSensitivity.CaseSensitive ).appendTo( n1 );


        ProductionRule    rule1  = ProductionRule.terminal( n1 );
        Parser<Character> parser = new CharacterParser(rule1, Collections.EMPTY_MAP, l);


        int numCharactersConsumed = consume(parser,"a") + consume(parser,"b");

        List<String> expectedAudit = Arrays.asList(
                "started"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 2, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingAB_appendEOS_expectStartFinishEvents() {
        LabelNode<Character> n1 = new LabelNode();
        new StringOp( "ab", GraphBuilder.CaseSensitivity.CaseSensitive ).appendTo( n1 );


        ProductionRule    rule1  = ProductionRule.terminal( n1 );
        Parser<Character> parser = new CharacterParser(rule1, Collections.EMPTY_MAP, l);



        parser.appendEOS();

        List<String> expectedAudit = Arrays.asList(
                "started",
                "finished"
        );

        assertEquals( expectedAudit, l.audit );
    }

    @Test
    public void givenAutomataExpectingAB_appendEOSTwice_expectException() {
        LabelNode<Character> n1 = new LabelNode();
        new StringOp( "ab", GraphBuilder.CaseSensitivity.CaseSensitive ).appendTo( n1 );


        ProductionRule    rule1  = ProductionRule.terminal( n1 );
        Parser<Character> parser = new CharacterParser(rule1, Collections.EMPTY_MAP, l);


        parser.appendEOS();


        try {
            parser.appendEOS();

            Assert.fail( "expected IllegalStateException" );
        } catch (IllegalStateException e) {
            Assert.assertEquals("the parser has already been notified of EOS", e.getMessage());
        }
    }

    @Test
    public void givenAutomataExpectingAB_appendEOSThenA_expectException() {
        LabelNode<Character> n1 = new LabelNode();
        new StringOp( "a\nb", GraphBuilder.CaseSensitivity.CaseSensitive ).appendTo( n1 );


        ProductionRule    rule1  = ProductionRule.terminal( n1 );
        Parser<Character> parser = new CharacterParser(rule1, Collections.EMPTY_MAP, l);


        parser.appendEOS();


        try {
            consume(parser,"a");

            Assert.fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            Assert.assertEquals("the parser has already been notified of EOS", e.getMessage());
        }
    }

    @Test
    public void givenParserABThatHasAlreadyParsedA_resetThenAppendB_expectSuccessfulReset() {
        LabelNode<Character> n1 = new LabelNode();
        new StringOp( "ab", GraphBuilder.CaseSensitivity.CaseSensitive ).appendTo( n1 );


        ProductionRule    rule1  = ProductionRule.terminal( n1 );
        Parser<Character> parser = new CharacterParser(rule1, Collections.EMPTY_MAP, l);


        consume(parser,"a");
        parser.reset();
        l.audit.clear();


        int numCharactersConsumed = consume(parser,"b");

        parser.appendEOS();

        List<String> expectedAudit = Arrays.asList(
                "started",
                "(1,1): unexpected input 'b', expected 'a'",
                "finished"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }

    @Test
    public void givenAutomataExpectingABCorD_parseE_expectADRangeInErrorMessage() {
        LabelNode<Character> n1 = new LabelNode();
        Label<Character> label = Labels.orLabels( Labels.characterRange( 'a', 'd' ), Labels.singleValue('f') );

        n1.append( label );


        ProductionRule    rule1  = ProductionRule.terminal( n1 );
        Parser<Character> parser = new CharacterParser(rule1, Collections.EMPTY_MAP, l);



        int numCharactersConsumed = consume(parser,"e");

        List<String> expectedAudit = Arrays.asList(
                "started",
                "(1,1): unexpected input 'e', expected '[a-d]|f'"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }


// custom actions

    // name = [a-zA-Z]+
    // helloName = Hello $NAME

//    @Test
    public void givenHelloParser_parseHelloJim_expectHelloCallback() {
        ProductionRule nameRule = new ProductionRuleBuilder()
            .appendRegexp("[a-zA-Z]+")
            .build();

        ProductionRule helloNameRule = new ProductionRuleBuilder()
            .appendConstant("Hello")
            .skipWhitespace()
            .appendRef( "NAME" )
            .withCallback( RecordingParserListener.class, "hello" )
            .build();

        Map<String,ProductionRule> productionRules = MapUtils.asMap(
            "name", nameRule
        );

        Parser<Character> parser = new CharacterParser(helloNameRule, productionRules, l);




        int numCharactersConsumed = consume(parser,"Hello Jim");
        parser.appendEOS();

        List<String> expectedAudit = Arrays.asList(
                "started",
                "(1,7): Welcome 'Jim'",
                "finished"
        );

        assertEquals( expectedAudit, l.audit );
        assertEquals( 0, numCharactersConsumed );
    }



    // custom error messages
    // recoveries
    // custom events



    private int consume( Parser parser, String input ) {
        int count = 0;

        for ( char c : input.toCharArray() ) {
            if ( parser.consume(c) ) {
                count++;
            } else {
                return count;
            }
        }

        return count;
    }

    @SuppressWarnings({"unchecked", "UnusedDeclaration"})
    private static class RecordingParserListener implements ParserListener {
        public final List<String> audit = new ArrayList();

        public void started() {
            audit.add("started");
        }

        public void error( int line, int col, String message ) {
            audit.add( String.format("(%d,%d): %s", line, col, message) );
        }

        public void hello( int line, int col, String name ) {
            audit.add(  String.format("(%d,%d): Welcome '%s'", line, col, name) );
        }

        public void finished() {
            audit.add("finished");
        }
    }

}
