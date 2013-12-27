package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.Node;
import org.junit.Test;

import static com.softwaremosaic.parsers.automata.GraphAssertions.assertGraphEquals;
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

    @Test
    public void givenBlankStartingNode_appendABCaseSensitive() {
        Node       s  = new Node("l1");
        AutomataOp op = new ConstantOp( "ab", CaseSensitive );

        op.appendTo( "l1", s );


        assertGraphEquals( s, "l1: 1 -a-> 2 -b-> 3t" );
    }

    @Test
    public void givenBlankStartingNode_appendABCaseInsensitive() {
        Node       s  = new Node("l1");
        AutomataOp op = new ConstantOp( "ab", CaseInsensitive );

        op.appendTo( "l1", s );


        assertGraphEquals( s, "l1: 1 -[Aa]-> 2 -[Bb]-> 3t" );
    }



// toString

    @Test
    public void givenCaseSensitive_toString_expectLowercaseString() {
        AutomataOp op = new ConstantOp( "aB", CaseSensitive );

        assertEquals( "aB", op.toString() );
    }

    @Test
    public void givenCaseInsensitive_toString_expectLowerAndUppercaseString() {
        AutomataOp op = new ConstantOp( "aB", CaseInsensitive );

        assertEquals( "~ab", op.toString() );
    }

}
