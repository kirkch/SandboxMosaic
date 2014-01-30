package com.mosaic.parser.graph.builder;

import com.mosaic.parser.graph.NodeFormatter;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class RegexpParserActionTests {

    private NodeBuilderFactory parser    = new NodeBuilderFactory();
    private NodeFormatter formatter = NodeFormatter.DETAILED_FORMATTER;


    @Test
    public void givenNonCapturingA_expectConstantAOpBackWithNoActions() {
        NodeBuilder op = parser.parse( "a" );

        List<String> expected = Arrays.asList( "1:NoOp -a-> 2:NoOp" );
        assertEquals( expected, formatter.format( op.build() ) );
    }

    @Test
    public void givenCapturingA_expectConstantAOpBackWithNoActions() {
        NodeBuilder op = parser.parse( "a" ).isCapturing(true);

        List<String> expected = Arrays.asList( "1:Cap -a-> 2:(Cap,ToStr)" );
        assertEquals( expected, formatter.format( op.build() ) );
    }


//    @Test
//    public void parseNonCapturingRegExpWithName_expect() {
//        NodeBuilder op = parser.parse( "a" ).isCapturing(true);
//
//        List<String> expected = Arrays.asList( "1:Cap -a-> 2:(Cap,ToStr,Pop)" );
//        assertEquals( expected, formatter.format( op.build() ) );
//    }

}
