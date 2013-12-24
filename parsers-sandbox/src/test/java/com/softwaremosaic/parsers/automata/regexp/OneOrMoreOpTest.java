package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.Node;
import org.junit.Test;

import static com.softwaremosaic.parsers.automata.GraphAssertions.assertGraphEquals;
import static com.softwaremosaic.parsers.automata.regexp.AutomataOp.CaseSensitivity.CaseSensitive;

/**
 *
 */
public class OneOrMoreOpTest {

    @Test
    public void givenBlankStartingNode_appendACaseSensitive_expectOneTransitionThenLoopBack() {
        Node s  = new Node("l1");
        AutomataOp op = new OneOrMoreOp( new ConstantOp("a", CaseSensitive) );

        op.appendTo( "l1", s );


        String[] expected = new String[] {
                "l1: 1 -a-> 2 -a-> 2"
        };

        assertGraphEquals( s, expected );
    }

}
