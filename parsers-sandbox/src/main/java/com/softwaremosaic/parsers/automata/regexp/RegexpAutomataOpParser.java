package com.softwaremosaic.parsers.automata.regexp;

import com.mosaic.collections.FastStack;

import static com.softwaremosaic.parsers.automata.regexp.AutomataOp.CaseSensitivity.*;


/**
 * Converts a text definition of a regular expression into an instance of
 * AutomataOp.  The regular expression can then be appended to any node within
 * an automata, as many times as required and in any place.
 */
@SuppressWarnings("unchecked")
public class RegexpAutomataOpParser {

    private FastStack<AutomataOp> opStack = new FastStack();


    public AutomataOp parse( String regexpString ) {
        int pos = 0;
        do {
            pos = parseNext( pos, regexpString );
        } while ( pos < regexpString.length() );

        return opStack.pop();
    }


    private int parseNext( int i, String str ) {
        if ( i >= str.length() ) {
            return i;
        }

        char c = str.charAt(i);
        switch ( c ) {
            case '*':
                return parseZeroOrMore(i, str);
            case '~':
            default:
                return parseConstant( i, str );
        }
    }

    private int parseZeroOrMore( int i, String str ) {
        assert str.charAt(i) == '*';

        opStack.push( new ZeroOrMoreOp(opStack.pop()) );

        return i+1;
    }

    private int parseConstant( int i, String str ) {
        AutomataOp op;

        int endExc = nextSpecialCharacter( i+1, str );

        if ( str.charAt(i) == '~' ) {
            op = new ConstantOp(str.substring(i+1,endExc), CaseInsensitive );
        } else {
            op = new ConstantOp(str.substring(i,endExc), CaseSensitive );
        }

        opStack.push( op );

        return endExc;
    }

    private int nextSpecialCharacter( int fromInc, String str ) {
        for ( int i=fromInc; i<str.length(); i++ ) {
            char c = str.charAt(i);

            if ( c == '*' ) {
                return i;
            }
        }

        return str.length();
    }

}
