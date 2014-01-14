package com.mosaic.parser;

import com.mosaic.collections.ConsList;
import com.mosaic.collections.trie.CharacterNode;
import com.mosaic.collections.trie.CharacterNodes;
import com.mosaic.lang.CharacterPredicate;
import com.mosaic.lang.Validate;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.reflect.MethodCall;
import com.mosaic.lang.reflect.MethodRef;
import com.mosaic.lang.reflect.ReflectionException;
import com.mosaic.utils.ListUtils;
import com.mosaic.utils.SetUtils;
import com.mosaic.utils.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.mosaic.collections.ConsList.Nil;


/**
 *
 */
@SuppressWarnings("unchecked")
public class Parser {

    private ProductionRule rootRule;
    private ParserListener listener;

    /**
     * The parser explores each possible route through the parse graph in parallel
     * (or in other words, a breath first search).  The rationale being that most
     * ambiguities will be short lived, and we thus avoid the obscurities created
     * by supporting backtracking or look ahead.
     */
    private ConsList<ParserContext> currentCandidateContexts;

    private boolean hasStarted;
    private boolean hasFinished;

    private int lineNumber   = 1;
    private int columnNumber = 1;


    public Parser( ProductionRule rootRule, ParserListener listener ) {
        Validate.notNull( rootRule, "rootRule" );
        Validate.notNull( listener, "listener" );

        this.rootRule = rootRule;
        this.listener = listener;

        reset();
    }

    public void reset() {
        lineNumber   = 1;
        columnNumber = 1;

        hasStarted  = false;
        hasFinished = false;

        currentCandidateContexts = Nil.cons( new ParserContext(rootRule, listener) );
    }

    public boolean parse( char input ) {
        preparseStateChecks();

        ConsList<ParserContext> nextContexts = Nil;

        for ( ParserContext ctx : currentCandidateContexts ) {
            nextContexts = nextContexts.append( ctx.parse(input) );
        }

        if ( nextContexts.isEmpty() ) {
            Set<CharacterPredicate> candidateNextInputs = calculateCandidateNextInputs();

            if ( candidateNextInputs.isEmpty() ) {
                listener.error( lineNumber, columnNumber, "unexpected input '"+input+"', no further input was expected" );
            } else {
                String formattedExpectation = formatCharacterPredicates( candidateNextInputs );

                listener.error( lineNumber, columnNumber, "unexpected input '"+input+"', expected '"+formattedExpectation+"'" );
            }

            return false;
        } else {
            currentCandidateContexts = nextContexts;

            incrementColumnAndLinePositionsGiven( input );

            return true;
        }
    }

    public void endOfStream() {
        preparseStateChecks();


        ConsList<ParserContext> nextContexts = Nil;

        for ( ParserContext ctx : currentCandidateContexts ) {
            nextContexts = nextContexts.append( ctx.endOfStream() );
        }


        hasFinished = true;

        if ( nextContexts.isEmpty() ) {
            Set<CharacterPredicate> candidateNextInputs  = calculateCandidateNextInputs();
            String                  formattedExpectation = formatCharacterPredicates( candidateNextInputs );

            listener.error( lineNumber, columnNumber, "end of stream reached when expecting '"+formattedExpectation+"'" );
        } else {
            currentCandidateContexts = nextContexts;

            for ( MethodCall action : nextContexts.head().actions ) {
                action.invoke();
            }

            listener.finished();
        }
    }



    public int parse( String input ) {
        preparseStateChecks();

        int count = 0;

        for ( int i=0; i<input.length(); i++ ) {
            if ( parse(input.charAt(i)) ) {
                count += 1;
            } else {
                return count;
            }
        }

        return count;
    }

    public List getParsedValue() {
        if ( currentCandidateContexts.isEmpty() ) {
            return Collections.EMPTY_LIST;
        } else {
            return currentCandidateContexts.head().currentValue.toList();
        }
    }



    private void incrementColumnAndLinePositionsGiven( char c ) {
        switch (c) {
            case '\n':
                columnNumber  = 1;
                lineNumber   += 1;
                break;
            case '\r':
                break; // skip
            default:
                columnNumber += 1;
        }
    }

    private String formatCharacterPredicates( Set<CharacterPredicate> predicates ) {
        List<CharacterPredicate> sorted = SetUtils.sort( predicates );

        return StringUtils.join( sorted, "|" );
    }

    private Set<CharacterPredicate> calculateCandidateNextInputs() {
        Set<CharacterPredicate > candidates = new HashSet();

        for ( ParserContext ctx : currentCandidateContexts ) {
            candidates.addAll( ctx.currentNode.getOutPredicates() );
        }

        return candidates;
    }

    private void preparseStateChecks() {
        if ( hasFinished ) {
            throw new IllegalStateException( "the parser has already been notified of EOS" );
        } else if ( !hasStarted ) {
            listener.started();

            hasStarted = true;
        }
    }


    static interface ParserContextOp {

        public void justConsumed( char c, ParserContext mutableNextState );

        /**
         *
         * @return returns true when this is a valid place to finish the rule
         */
        public boolean productionRuleFinished( ParserContext mutableNextState );

    }


    static class ParserContext implements Cloneable {
        private CharacterNode<ParserContextOp> currentNode;
        private ConsList                       currentValue = Nil;
        private ConsList<MethodCall>           actions      = Nil;
        private ParserListener                 listener;

        private int line = 1;
        private int col  = 1;


        public ParserContext( ProductionRule rule, ParserListener listener ) {
            this( rule.startingNode(), listener );
        }

        public ParserContext( CharacterNode node, ParserListener listener ) {
            this.currentNode = node;
            this.listener    = listener;
        }

        public ConsList getValue() {
            return currentValue;
        }

        public void setValue( ConsList newValue ) {
            this.currentValue = newValue;
        }

        public Iterable<ParserContext> parse( final char input ) {
            CharacterNodes<ParserContextOp> nextNodes =  currentNode.fetch( input );

            return ListUtils.map( nextNodes, new Function1<CharacterNode<ParserContextOp>, ParserContext>() {
                public ParserContext invoke( CharacterNode<ParserContextOp> nextNode ) {
                    ParserContext nextContext = ParserContext.this.clone().withNextNode( nextNode );

                    nextNode.getPayload().justConsumed( input, nextContext );

//                    if ( !nextNode.hasOutEdges() ) {
//                        nextNode.getPayload().productionRuleFinished( nextContext );
//                    }

                    return nextContext;
                }
            });
        }

        public Iterable<ParserContext> endOfStream() {
            ParserContext nextContext = ParserContext.this.clone();

            if ( currentNode.getPayload().productionRuleFinished(nextContext) ) {
                return Arrays.asList( nextContext );
            } else {
                return Collections.EMPTY_LIST;
            }
        }


        private ParserContext withNextNode( CharacterNode <ParserContextOp> nextNode ) {
            this.currentNode = nextNode;

            return this;
        }

        public ParserContext clone() {
            try {
                return (ParserContext) super.clone();
            } catch ( CloneNotSupportedException e ) {
                throw ReflectionException.recast( e );
            }
        }

        public void appendInputValue( char c ) {
            currentValue = currentValue.cons(c);
        }

        public void appendAction( MethodRef callbackMethodRef ) {
            actions = actions.cons( new MethodCall(callbackMethodRef, listener, line, col, currentValue.head()) );
        }
    }

}
