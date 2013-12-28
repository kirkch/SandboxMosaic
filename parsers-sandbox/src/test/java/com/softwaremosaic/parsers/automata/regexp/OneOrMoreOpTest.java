package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.LabelNode;
import org.junit.Test;

import static com.softwaremosaic.parsers.automata.GraphAssertions.assertGraphEquals;
import static com.softwaremosaic.parsers.automata.regexp.GraphBuilder.CaseSensitivity.CaseSensitive;

/**
*
*/
@SuppressWarnings("unchecked")
public class OneOrMoreOpTest {

    @Test
    public void givenBlankStartingNode_appendACaseSensitive_expectOneTransitionThenLoopBack() {
        Node s  = new LabelNode();
        GraphBuilder op = new OneOrMoreOp( new StringOp("a", CaseSensitive) );

        op.appendTo( s );


        String[] expected = new String[] {
                "1 -a-> 2 -a-> 2"
        };

        assertGraphEquals( s, expected );
    }

}
