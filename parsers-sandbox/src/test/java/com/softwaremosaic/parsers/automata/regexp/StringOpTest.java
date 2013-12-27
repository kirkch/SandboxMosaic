package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.ObjectNode;
import org.junit.Test;

import static com.softwaremosaic.parsers.automata.GraphAssertions.assertGraphEquals;
import static com.softwaremosaic.parsers.automata.regexp.GraphBuilder.CaseSensitivity.*;
import static org.junit.Assert.assertEquals;


/**
*
*/
@SuppressWarnings("unchecked")
public class StringOpTest {

    @Test
    public void givenBlankStartingNode_appendACaseSensitive_expectSingleTransition() {
        ObjectNode s  = new ObjectNode();
        GraphBuilder op = new StringOp( "a", CaseSensitive );

        op.appendTo( s );


        assertGraphEquals( s, "1 -a-> 2t" );
    }

    @Test
    public void givenBlankStartingNode_appendACaseInsensitive_expectTwoTransitionsToOneNode() {
        ObjectNode s  = new ObjectNode();
        GraphBuilder op = new StringOp( "a", CaseInsensitive );

        op.appendTo( s );

        assertGraphEquals( s, "1 -[Aa]-> 2t" );
    }

    @Test
    public void givenBlankStartingNode_appendABCaseSensitive() {
        ObjectNode s  = new ObjectNode();
        GraphBuilder op = new StringOp( "ab", CaseSensitive );

        op.appendTo( s );


        assertGraphEquals( s, "1 -a-> 2 -b-> 3t" );
    }

    @Test
    public void givenBlankStartingNode_appendABCaseInsensitive() {
        ObjectNode s  = new ObjectNode();
        GraphBuilder op = new StringOp( "ab", CaseInsensitive );

        op.appendTo( s );


        assertGraphEquals( s, "1 -[Aa]-> 2 -[Bb]-> 3t" );
    }



// toString

    @Test
    public void givenCaseSensitive_toString_expectLowercaseString() {
        GraphBuilder op = new StringOp( "aB", CaseSensitive );

        assertEquals( "aB", op.toString() );
    }

    @Test
    public void givenCaseInsensitive_toString_expectLowerAndUppercaseString() {
        GraphBuilder op = new StringOp( "aB", CaseInsensitive );

        assertEquals( "~ab", op.toString() );
    }

}
