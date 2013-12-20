package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.NodeFormatter;
import org.junit.Test;

import java.util.Arrays;

import static com.softwaremosaic.parsers.automata.regexp.AutomataOp.CaseSensitivity.*;
import static org.junit.Assert.assertEquals;


/**
 *
 */
public class ConstantOpTest {

    @Test
    public void givenBlankStartingNode_appendACaseSensitive_expectSingleTransition() {
        Node       s  = new Node("l1");
        AutomataOp op = new ConstantOp( "a", CaseSensitive );

        op.appendTo( "l1", s );


        assertGraphEquals( s, "l1: 1 -a-> 2t" );
    }

    @Test
    public void givenBlankStartingNode_appendACaseInsensitive_expectTwoTransitionsToOneNode() {
        Node       s  = new Node("l1");
        AutomataOp op = new ConstantOp( "a", CaseInsensitive );

        op.appendTo( "l1", s );


        assertGraphEquals( s, "l1: 1 -[Aa]-> 2t" );
    }


    //
    // givenBlankStartingNode_appendABCaseSensitive
    // givenBlankStartingNode_appendABCaseInsensitive

    private void assertGraphEquals( Node startingNode, String...graphDescription ) {
        NodeFormatter f = new NodeFormatter();

        assertEquals( Arrays.asList(graphDescription), f.format(startingNode) );
    }

}
