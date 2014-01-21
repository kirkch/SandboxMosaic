package com.mosaic.parser;

import com.mosaic.parser.graph.NodeFormatter;

import com.mosaic.parser.graph.Parser;
import com.mosaic.parser.graph.ParserTest;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.mosaic.lang.CaseSensitivity.CaseInsensitive;
import static org.junit.Assert.assertEquals;


/**
 *
 */
@SuppressWarnings("unchecked")
public class ProductionRuleBuilderTest {

    private ProductionRuleBuilder b = new ProductionRuleBuilder();
    private NodeFormatter nodeFormatter = NodeFormatter.DETAILED_FORMATTER;


// CONSTANT

    @Test
    public void givenEmptyBuilder_createConstantRule_expectConstantMatcher() {
        ProductionRule helloRule = b.constant( "rule1", "Hello" );

        assertEquals( "rule1", helloRule.name() );

        List<String> expectedGraph = Arrays.asList("1:NoOp -H-> 2:NoOp -e-> 3:NoOp -l-> 4:NoOp -l-> 5:NoOp -o-> 6e:NoOp");
        assertEquals( expectedGraph, nodeFormatter.format(helloRule.startingNode()) );
    }

    @Test
    public void givenEmptyBuilder_createCaseInsensitiveConstantRule_expectConstantMatcher() {
        ProductionRule helloRule = b.constant( "rule1", "Hello", CaseInsensitive );

        assertEquals( "rule1", helloRule.name() );

        List<String> expectedGraph = Arrays.asList("1:NoOp -[Hh]-> 2:NoOp -[Ee]-> 3:NoOp -[Ll]-> 4:NoOp -[Ll]-> 5:NoOp -[Oo]-> 6e:NoOp");
        assertEquals( expectedGraph, nodeFormatter.format(helloRule.startingNode()) );
    }

    @Test
    public void givenEmptyBuilder_createTerminalRule_expectConstantMatcher() {
        ProductionRule<String> helloRule = b.terminal( "rule1", "Hello", String.class );

        assertEquals( "rule1", helloRule.name() );

        List<String> expectedGraph = Arrays.asList("1:Cap -H-> 2:Cap -e-> 3:Cap -l-> 4:Cap -l-> 5:Cap -o-> 6e:(Cap,ToStr)");
        assertEquals( expectedGraph, nodeFormatter.format(helloRule.startingNode()) );
    }

    @Test
    public void givenEmptyBuilder_createCapturingCaseInsensitiveConstantRule_expectConstantMatcher() {
        ProductionRule<String> helloRule = b.terminal( "rule1", "Hello", CaseInsensitive );

        assertEquals( "rule1", helloRule.name() );

        List<String> expectedGraph = Arrays.asList("1:Cap -[Hh]-> 2:Cap -[Ee]-> 3:Cap -[Ll]-> 4:Cap -[Ll]-> 5:Cap -[Oo]-> 6e:(Cap,ToStr)");
        assertEquals( expectedGraph, nodeFormatter.format(helloRule.startingNode()) );
    }


// RegExp

    @Test
    public void givenEmptyBuilder_createRegexpConstant() {
        ProductionRule rule1 = b.constant( "rule1", "Hello" );

        assertEquals( "rule1", rule1.name() );

        List<String> expectedGraph = Arrays.asList("1:NoOp -H-> 2:NoOp -e-> 3:NoOp -l-> 4:NoOp -l-> 5:NoOp -o-> 6e:NoOp");
        assertEquals( expectedGraph, nodeFormatter.format(rule1.startingNode()) );
    }

    @Test
    public void givenEmptyBuilder_createNonCapturingTerminal() {
        ProductionRule rule1 = b.terminal( "rule1", "[a-z]+", Void.class );

        assertEquals( "rule1", rule1.name() );

        List<String> expectedGraph = Arrays.asList("1:NoOp -[a-z]-> 2e:NoOp -[a-z]-> 2e:NoOp");
        assertEquals( expectedGraph, nodeFormatter.format(rule1.startingNode()) );
    }

    @Test
    public void givenEmptyBuilder_createCapturingRegexp() {
        ProductionRule rule1 = b.terminal( "rule1", "[a-z]+", String.class );

        assertEquals( "rule1", rule1.name() );

        List<String> expectedGraph = Arrays.asList("1:Cap -[a-z]-> 2e:(Cap,ToStr) -[a-z]-> 2e:(Cap,ToStr)");
        assertEquals( expectedGraph, nodeFormatter.format(rule1.startingNode()) );
    }


// EMBEDDED PRODUCTION RULES



    @Test
    public void givenBuilderWithRule1_declareAnotherRule1_expectError() {
        b.terminal( "rule1", "[a-z]+", Void.class );

        try {
            b.terminal( "rule1", "[0-9]+", Void.class );

            Assert.fail( "expected IllegalArgumentException" );
        } catch ( IllegalArgumentException e ) {
            Assert.assertEquals( "'rule1' has already been declared", e.getMessage() );
        }
    }

    @Test
    public void givenEmptyBuilder_declareRuleWithSingleEmbeddedRuleThatDoesNotExist_expectError() {
        try {
            b.terminal( "rule1", "$rule2", Void.class );

            Assert.fail( "expected IllegalArgumentException" );
        } catch ( IllegalArgumentException e ) {
            Assert.assertEquals( "'rule2' has not been declared yet; forward references are not supported", e.getMessage() );
        }
    }

    @Test
    public void givenExistingRule_reuseRuleInAnotherRule_expectNewGraph() {
        ProductionRule rule1 = b.terminal( "rule1", "[a-z]+", Void.class );
        ProductionRule rule2 = b.nonTerminal( "rule2", "$rule1" );

        assertEquals( "rule1", rule1.name() );
        assertEquals( "rule2", rule2.name() );


        List<String> expectedGraph = Arrays.asList("1:Push -$rule1-> 2e:NoOp");
        assertEquals( expectedGraph, nodeFormatter.format(rule2.startingNode()) );
    }

    @Test
    public void embedThreeRulesInARow() {
        b.terminal( "rule1", "Hello", Void.class );
        b.terminal( "rule2", "[ \t]+", Void.class );
        b.terminal( "rule3", "[a-z]+", Void.class );

        ProductionRule rootRule = b.nonTerminal( "rootRule", "$rule1$rule2$rule3", Void.class );


        List<String> expectedGraph = Arrays.asList("1:Push -$rule1-> 2:Push -$rule2-> 3:Push -$rule3-> 4e:NoOp");
        assertEquals( expectedGraph, nodeFormatter.format(rootRule.startingNode()) );
    }

//    @Test   TODO auto consume whitespace between non-terminals
    public void csvRowsExample_nestedCapturingRules() {
        ProductionRule<String>       columnRule = b.terminal( "COLUMN_VALUE", "[^, \t]+", String.class );
        ProductionRule<List<String>> rowRule    = b.nonTerminal( "ROW", "$COLUMN_VALUE (,$COLUMN_VALUE)*", String.class ).withCallback( ParserTest.RecordingParserListener.class, "list" );


        ProductionRule<List<String>> rootRule = b.nonTerminal( "rootRule", "$ROW", String.class );
        ParserTest.RecordingParserListener l = new ParserTest.RecordingParserListener();

        Parser p = new Parser(rootRule, l);

        assertEquals( Arrays.asList("1:Cap -[^, \t]-> 2e:(Cap,ToStr) -[^, \t]-> 2e:(Cap,ToStr)"), nodeFormatter.format(columnRule.startingNode()) );
        assertEquals( Arrays.asList("1:Push -$COLUMN_VALUE-> 2e:Cb -,-> 3:Push -$COLUMN_VALUE-> 2e:Cb"), nodeFormatter.format(rowRule.startingNode()) );


        List<String> expectedGraph = Arrays.asList("1:Push -$ROW-> 2e:NoOp");
        assertEquals( expectedGraph, nodeFormatter.format(rootRule.startingNode()) );


        System.out.println( "columnRule = " + nodeFormatter.format( columnRule.startingNode() ) );
        System.out.println( "rowRule = " + nodeFormatter.format( rowRule.startingNode() ) );
        System.out.println( "rootRule = " + nodeFormatter.format( rootRule.startingNode() ) );

        int numCharactersConsumed = p.parse( "header1, header2" );
        assertEquals( 16, numCharactersConsumed );

        p.endOfStream();


        List<String> expectedAudit = Arrays.asList(
            "started",
            "(1,1): list=[header1, header2]",
            "finished"
        );

        assertEquals( expectedAudit, l.audit );
    }

}
