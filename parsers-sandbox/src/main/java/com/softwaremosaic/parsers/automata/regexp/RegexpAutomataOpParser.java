package com.softwaremosaic.parsers.automata.regexp;

import com.mosaic.collections.FastStack;

import static com.softwaremosaic.parsers.automata.regexp.GraphBuilder.CaseSensitivity.CaseInsensitive;
import static com.softwaremosaic.parsers.automata.regexp.GraphBuilder.CaseSensitivity.CaseSensitive;


/**
 * Converts a text definition of a regular expression into an instance of
 * GraphBuilder.  The regular expression can then be appended to any node within
 * an automata, as many times as required and in any place.
 */
@SuppressWarnings("unchecked")
public class RegexpAutomataOpParser {

    private FastStack<GraphBuilder> opStack = new FastStack();


    public GraphBuilder parse( String regexpString ) {
        int pos = 0;
        do {
            pos = parseNext( pos, regexpString, 0 );
        } while ( pos < regexpString.length() );

        return opStack.pop();
    }


    private int parseNext( int i, String str, int nestedBracketCount ) {
        if ( i >= str.length() ) {
            return i;
        }

        char c = str.charAt(i);
        switch ( c ) {
            case '*':
                return parseZeroOrMore(i, str);
            case '+':
                return parseOneOrMore( i, str );
            case '?':
                return parseOptional( i, str );
            case '[':
                return parseCharacterSelection( i, str );
            case '|':
                int next = parseNext( i+1, str, nestedBracketCount );

                parseOr(i, str);

                return next;
            case '(':
                return parseNext(i+1, str, nestedBracketCount+1);
            case ')':
                return i+1;
            case '~':
            default:
                return parseConstant( i, str, nestedBracketCount );
        }
    }

    private int parseZeroOrMore( int i, String str ) {
        assert str.charAt(i) == '*';

        opStack.push( new ZeroOrMoreOp(opStack.pop()) );

        return i+1;
    }

    private int parseOneOrMore( int i, String str ) {
        assert str.charAt(i) == '+';

        opStack.push( new OneOrMoreOp(opStack.pop()) );

        return i+1;
    }

    private int parseOptional( int i, String str ) {
        assert str.charAt(i) == '?';

        opStack.push( new OptionalOp(opStack.pop()) );

        return i+1;
    }

    private int parseCharacterSelection( int i, String str ) {
        assert str.charAt(i) == '[';

//        Labels.characterRange(  )

        return i+1;
    }

    private void parseOr( int i, String str ) {
        assert str.charAt(i) == '|';

        GraphBuilder b = opStack.pop();
        GraphBuilder a = opStack.pop();

        if ( a.getClass() == OrOp.class ) {
            OrOp op = (OrOp) a;

            op.add( b );

            opStack.push( op );
        } else {
            opStack.push( new OrOp(a,b) );
        }
    }

    private int parseConstant( int i, String str, int nestedBracketCount ) {
        GraphBuilder op;

        int endExc = nextSpecialCharacter( i+1, str );

        if ( str.charAt(i) == '~' ) {
            op = new StringOp(str.substring(i+1,endExc), CaseInsensitive );
        } else {
            op = new StringOp(str.substring(i,endExc), CaseSensitive );
        }

        opStack.push( op );

        return endExc;
    }

    private int nextSpecialCharacter( int fromInc, String str ) {
        for ( int i=fromInc; i<str.length(); i++ ) {
            char c = str.charAt(i);

            if ( c == '*' || c == ')' || c == '+' || c == '?' || c == '|' || c == '[' || c == ']') {
                return i;
            }
        }

        return str.length();
    }

}
