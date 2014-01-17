package com.mosaic.parser;

import com.mosaic.collections.trie.CharacterNode;
import com.mosaic.collections.trie.CharacterNodeFormatter;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.mosaic.collections.trie.CharacterNodeFormatter.NodeFormatPlugin;
import static com.mosaic.lang.CaseSensitivity.CaseInsensitive;
import static com.mosaic.parser.Parser.ParserFrameOp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
@SuppressWarnings("unchecked")
public class ProductionRuleBuilderTest {

    private ProductionRuleBuilder b = new ProductionRuleBuilder();
    private CharacterNodeFormatter nodeFormatter = new CharacterNodeFormatter();

    private NodeFormatPlugin<ParserFrameOp> plugin = new NodeFormatPlugin<ParserFrameOp>() {
        public String getNodeLabelFor( long nodeId, CharacterNode<ParserFrameOp> node ) {
            StringBuilder buf = new StringBuilder();

            buf.append( nodeId );

            if ( node.isEndNode() ) {
                buf.append( 'e' );
            }

            ParserFrameOp op = node.getPayload();
            if ( op != null ) {
                buf.append( ':' );

                op.appendOpCodesTo(buf);
            }

            return buf.toString();
        }
    };


// CONSTANT

    @Test
    public void givenEmptyBuilder_createConstantRule_expectConstantMatcher() {
        ProductionRule helloRule = b.constant( "rule1", "Hello" );

        assertEquals( "rule1", helloRule.name() );

        List<String> expectedGraph = Arrays.asList("1:NoOp -H-> 2:NoOp -e-> 3:NoOp -l-> 4:NoOp -l-> 5:NoOp -o-> 6e:Pop");
        assertEquals( expectedGraph, nodeFormatter.format(helloRule.startingNode(),plugin) );
    }

    @Test
    public void givenEmptyBuilder_createCaseInsensitiveConstantRule_expectConstantMatcher() {
        ProductionRule helloRule = b.constant( "rule1", "Hello", CaseInsensitive );

        assertEquals( "rule1", helloRule.name() );

        List<String> expectedGraph = Arrays.asList("1:NoOp -[Hh]-> 2:NoOp -[Ee]-> 3:NoOp -[Ll]-> 4:NoOp -[Ll]-> 5:NoOp -[Oo]-> 6e:Pop");
        assertEquals( expectedGraph, nodeFormatter.format(helloRule.startingNode(),plugin) );
    }

    @Test
    public void givenHelloRule_declareAnotherRuleWithTheSameName_expectError() {
        ProductionRule helloRule = b.constant( "rule1", "Hello", CaseInsensitive );

        assertEquals( "rule1", helloRule.name() );

        try {
            b.constant( "rule1", "Hello", CaseInsensitive );
            fail( "expected IllegalArgumentException" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "'rule1' has already been declared", e.getMessage() );
        }
    }


    @Test
    public void givenEmptyBuilder_createCapturingConstantRule_expectConstantMatcher() {
        ProductionRule helloRule = b.capturingConstant( "rule1", "Hello" );

        assertEquals( "rule1", helloRule.name() );

        List<String> expectedGraph = Arrays.asList("1:Cap -H-> 2:Cap -e-> 3:Cap -l-> 4:Cap -l-> 5:Cap -o-> 6e:CapToStrPop");
        assertEquals( expectedGraph, nodeFormatter.format(helloRule.startingNode(),plugin) );
    }

    @Test
    public void givenEmptyBuilder_createCapturingCaseInsensitiveConstantRule_expectConstantMatcher() {
        ProductionRule helloRule = b.capturingConstant( "rule1", "Hello", CaseInsensitive );

        assertEquals( "rule1", helloRule.name() );

        List<String> expectedGraph = Arrays.asList("1:Cap -[Hh]-> 2:Cap -[Ee]-> 3:Cap -[Ll]-> 4:Cap -[Ll]-> 5:Cap -[Oo]-> 6e:CapToStrPop");
        assertEquals( expectedGraph, nodeFormatter.format(helloRule.startingNode(),plugin) );
    }


// RegExp

    @Test
    public void givenEmptyBuilder_createRegexp() {
        ProductionRule rule1 = b.regexp( "rule1", "[a-z]+" );

        assertEquals( "rule1", rule1.name() );

        List<String> expectedGraph = Arrays.asList("1:NoOp -[a-z]-> 2e:Pop -[a-z]-> 2e:Pop");
        assertEquals( expectedGraph, nodeFormatter.format(rule1.startingNode(),plugin) );
    }

    @Test
    public void givenEmptyBuilder_createCapturingRegexp() {
        ProductionRule rule1 = b.capturingRegexp( "rule1", "[a-z]+" );

        assertEquals( "rule1", rule1.name() );

        List<String> expectedGraph = Arrays.asList("1:Cap -[a-z]-> 2e:CapToStrPop -[a-z]-> 2e:CapToStrPop");
        assertEquals( expectedGraph, nodeFormatter.format(rule1.startingNode(),plugin) );
    }

}
