package com.mosaic.parser.graph.builder;

import com.mosaic.lang.CaseSensitivity;
import com.mosaic.lang.CharacterPredicates;
import com.mosaic.lang.functional.Function0;
import com.mosaic.lang.functional.Pair;
import com.mosaic.lang.functional.Stream;
import com.mosaic.parser.ProductionRule;
import com.mosaic.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Converts a text definition of a regular expression into an instance of
 * GraphBuilder.  The regular expression can then be appended to any node within
 * an automata, as many times as required and in any place.
 *
 * Supports
 *
 * abc             constant str abc
 * abc*            abc zero or more times
 * (abc)*          abc zero or more times
 * abc?            abc zero or once
 * (abc)?          abc zero or once
 * abc+            abc one or more times
 * (abc)+          abc one or more times
 *
 * ~abc            case insensitive abc
 *
 * [abc]           a or b or c
 * [a-d]           a or b or c or d
 * [^abc]          any char other than a, b or c
 * [abc0-9]        a, b, c, 0, 1, 2, 3, 4, 5, 6, 7, 8 or 9
 * .               any character
 *
 *
 * todo
 * ~[abc]
 * review escaping
 */
@SuppressWarnings("unchecked")
public class NodeBuilderFactory {

    private static final PredicateOp ANY_CHARACTER = new PredicateOp( CharacterPredicates.appendAnyCharacter() );


    private Map<String, ProductionRule> productionRuleScope;


    public NodeBuilder parse( String regexpString ) {
        return parse( regexpString, null );
    }

    public NodeBuilder parse( String regexpString, Map<String, ProductionRule> ops ) {
        this.productionRuleScope = ops;

        try {
            Stream <Token> tokens = toTokenStream( 0, regexpString );

            Pair<NodeBuilder,Stream<Token>> result = parseExpression( tokens );

            assert result.getSecond().isEmpty() : "failed to parse the entire string";

            return result.getFirst();
        } finally {
            this.productionRuleScope = null;
        }
    }


    /*  GRAMMER:

              pre   = ~
              post  = [+*?]
              value = . | [...] | constant | $ref

              term = (exp) | value
              exp  = pre* term post* (bi exp)?
     */

    private Pair<NodeBuilder,Stream<Token>> parseExpression( final Stream<Token> input ) {
        List<UnaryOp> unaryOps         = new ArrayList();
        Stream<Token> posAfterUnaryOps = parseAndAppendUnaryPrefixOps( unaryOps, input );


        Pair<NodeBuilder,Stream<Token>> termResult = parseTerm( posAfterUnaryOps );


        Stream<Token> posAfterPostfix         = parseAndAppendUnaryPostfixOps( unaryOps, termResult.getSecond() );
        NodeBuilder   valueAndUnaryOpsBuilder = applyUnaryOps( unaryOps, termResult.getFirst() );

        return parseBinaryOps( valueAndUnaryOpsBuilder, posAfterPostfix );
    }

    private Stream<Token> parseAndAppendUnaryPrefixOps( List<UnaryOp> unaryOps, Stream<Token> input ) {
        while ( input.notEmpty() && input.head().isSpecial ) {
            char c = input.head().c;

            if ( c == '~' ) {
                unaryOps.add( CASE_INSENSITIVE_DECORATOR );

                input = input.tail();
            } else {
                return input;
            }
        }

        return input;
    }

    private Stream<Token> parseAndAppendUnaryPostfixOps( List<UnaryOp> unaryOps, Stream<Token> input ) {
        while ( input.notEmpty() && input.head().isSpecial ) {
            char c = input.head().c;

            switch (c) {
                case '*':
                    unaryOps.add( ZERO_OR_MORE_DECORATOR );
                    break;
                case '+':
                    unaryOps.add( ONE_OR_MORE_DECORATOR );
                    break;
                case '?':
                    unaryOps.add( OPTIONAL_DECORATOR );
                    break;
                default:
                    return input;
            }

            input = input.tail();
        }

        return input;
    }

    private NodeBuilder applyUnaryOps( List<UnaryOp> unaryOps, NodeBuilder b ) {
        for ( UnaryOp op : unaryOps ) {
            b = op.decorate( b );
        }

        return b;
    }

    private Pair<NodeBuilder,Stream<Token>> parseTerm( final Stream<Token> input ) {
        NodeBuilder   term;
        Stream<Token> posAfterTerm;

        if ( isOpenBracket(input.head()) ) {
            Pair<NodeBuilder,Stream<Token>> valueResult = parseExpression(input.tail());

            Stream<Token> posAfterExpression = valueResult.getSecond();
            if ( posAfterExpression.isEmpty() || !isCloseBracket(posAfterExpression.head()) ) {
                throw new IllegalArgumentException( "Expected closing bracket ')' but found instead '"+StringUtils.join(posAfterExpression, "")+"'");
            }

            term         = valueResult.getFirst();
            posAfterTerm = posAfterExpression.tail();
        } else {
            Pair<NodeBuilder,Stream<Token>> valueResult = parseValue(input);

            term         = valueResult.getFirst();
            posAfterTerm = valueResult.getSecond();
        }

        return new Pair(term,posAfterTerm);
    }

    private Pair<NodeBuilder, Stream<Token>> parseBinaryOps( NodeBuilder lhs, Stream<Token> pos ) {
        if ( pos.isEmpty() ) {
            return new Pair(lhs,pos);
        } else if ( isCloseBracket(pos.head()) ) {
            return new Pair(lhs,pos);
        } else if ( isOrSymbol(pos.head()) ) {
            Pair<NodeBuilder,Stream<Token>> rhsResult = parseExpression( pos.tail() );

            NodeBuilder op = lhs.or( rhsResult.getFirst() );

            return new Pair( op, rhsResult.getSecond() );
        } else {
            Pair<NodeBuilder,Stream<Token>> rhsResult = parseExpression( pos );

            NodeBuilder op = lhs.and( rhsResult.getFirst() );

            return new Pair( op, rhsResult.getSecond() );
        }
    }

    private boolean isOpenBracket( Token token ) {
        return token.isSpecial && token.c == '(';
    }

    private boolean isCloseBracket( Token token ) {
        return token.isSpecial && token.c == ')';
    }

    private boolean isOrSymbol( Token token ) {
        return token.isSpecial && token.c == '|';
    }


    private Pair<NodeBuilder,Stream<Token>> parseValue( Stream<Token> input ) {
        Token head = input.head();

        if ( head.isSpecial ) {
            switch ( head.c ) {
                case '.':
                    return new Pair( ANY_CHARACTER, input.tail() );
                case '[':
                    return parseCharacterSelection( input.tail() );
                case '$':
                    return parseEmbeddedRuleReference( input.tail() );
                default:
            }
        }

        return parseConstant( input );
    }

    private Pair<NodeBuilder,Stream<Token>> parseCharacterSelection( Stream<Token> input ) {
        CharacterPredicates.CharacterSelectionPredicate label = CharacterPredicates.characterSelection();

        NodeBuilder op = new PredicateOp(label);

        Stream<Token> pos = input;

        if ( pos.isEmpty() ) {
            throw new IllegalArgumentException( "Unable to find closing ']' after '['"  );
        } else if ( pos.head().isSpecial && pos.head().c == '^' ) {
            label.invert();

            pos = pos.tail();
        }

        while ( pos.notEmpty() ) {
            Token token = pos.head();
            char  c     = token.c;

            if ( token.isSpecial ) {
                if ( c == ']' ) {
                    return new Pair(op, pos.tail());
                } else {
                    label.appendCharacter( c );
                }
            } else if ( isRange(pos) ) {
                pos = pos.tail().tail();

                label.appendRange( c, pos.head().c );
            } else {
                label.appendCharacter( c );
            }

            pos = pos.tail();
        }

        throw new IllegalArgumentException( "Unable to find closing ']' after '['"  );
    }

    private boolean isRange( Stream<Token> pos ) {
        if ( pos.isEmpty() || pos.tail().isEmpty() ) {
            return false;
        }

        Stream<Token> tail = pos.tail();

        Token nextToken = tail.head();
        return tail.notEmpty() && nextToken.isSpecial && nextToken.c == '-';
    }

    private Pair<NodeBuilder,Stream<Token>> parseConstant( Stream<Token> in ) {
        StringBuilder buf = new StringBuilder();

        Stream<Token> pos = in;
        while ( pos.notEmpty() ) {
            Token token = pos.head();

            if ( token.isSpecial ) {
                break;
            } else {
                buf.append( token.c );
            }

            pos = pos.tail();
        }

        StringOp stringOp = new StringOp( buf.toString(), CaseSensitivity.CaseSensitive );

        return new Pair(stringOp,pos);
    }

    private Pair<NodeBuilder,Stream<Token>> parseEmbeddedRuleReference( Stream<Token> input ) {
        if ( this.productionRuleScope == null ) {
            throw new IllegalArgumentException( "By definition, a terminal cannot reference any other production rules" );
        }

        StringBuilder buf = new StringBuilder();

        Stream<Token> pos = input;
        while ( pos.notEmpty() ) {
            Token token = pos.head();

            if ( token.isSpecial ) {
                break;
            } else {
                buf.append( token.c );
            }

            pos = pos.tail();
        }

        String         opName       = buf.toString().trim();
        ProductionRule embeddedRule = this.productionRuleScope.get(opName);

        if ( embeddedRule == null ) {
            throw new IllegalArgumentException( "'"+opName+"' has not been declared yet; forward references are not supported" );
        }


        EmbeddedProductionRuleOp op = new EmbeddedProductionRuleOp( embeddedRule );
        return new Pair(op,pos);
    }




    private static class Token {
        public final char    c;
        public final boolean isSpecial;

        public Token( char c, boolean special ) {
            this.c = c;
            isSpecial = special;
        }

        public String toString() {
            return Character.toString( c );
        }
    }

    private static Stream<Token> toTokenStream( int i, final String str ) {
        if ( i >= str.length() ) {
            return Stream.EMPTY;
        }

              char    c = str.charAt( i );
              boolean isSpecial;
        final int     nextIndex;
        if ( c == '\\' ) {
            c = str.charAt( i+1 );

            nextIndex = i+2;
            isSpecial = false;
        } else {
            isSpecial = RegExpCharacterUtils.isSpecialChar( c );
            nextIndex = i + 1;
        }

        Token token = new Token( c,isSpecial );
        return Stream.create( token, new Function0<Stream<Token>>() {
            public Stream<Token> invoke() {
                return toTokenStream( nextIndex, str );
            }
        } );
    }



    private static UnaryOp CASE_INSENSITIVE_DECORATOR = new CaseInsensitiveDecorator();
    private static UnaryOp ZERO_OR_MORE_DECORATOR     = new ZeroOrMoreDecorator();
    private static UnaryOp ONE_OR_MORE_DECORATOR      = new OneOrMoreDecorator();
    private static UnaryOp OPTIONAL_DECORATOR         = new OptionalDecorator();

    private static interface UnaryOp {
        public NodeBuilder decorate( NodeBuilder b );
    }

    private static class CaseInsensitiveDecorator implements UnaryOp {
        public NodeBuilder decorate( NodeBuilder b ) {
            b.insensitive( true );

            return b;
        }
    }

    private static class ZeroOrMoreDecorator implements UnaryOp {
        public NodeBuilder decorate( NodeBuilder b ) {
            return new ZeroOrMoreOp( b );
        }
    }

    private static class OneOrMoreDecorator implements UnaryOp {
        public NodeBuilder decorate( NodeBuilder b ) {
            return new OneOrMoreOp( b );
        }
    }

    private static class OptionalDecorator implements UnaryOp {
        public NodeBuilder decorate( NodeBuilder b ) {
            return new OptionalOp( b );
        }
    }
}
