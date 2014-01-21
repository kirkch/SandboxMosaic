package com.mosaic.parser.graph;

import com.mosaic.collections.ConsList;
import com.mosaic.lang.CharacterPredicate;
import com.mosaic.lang.Validate;
import com.mosaic.lang.reflect.MethodCall;
import com.mosaic.parser.ProductionRule;
import com.mosaic.utils.SetUtils;
import com.mosaic.utils.StringUtils;

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

    private static final boolean DEBUG = false;

    private ProductionRule rootRule;
    private ParserListener listener;

    /**
     * The parser explores each possible route through the parse graph in parallel
     * (or in other words, a breath first search).  The rationale being that most
     * ambiguities will be short lived, and we thus avoid the obscurities created
     * by supporting backtracking or look ahead.
     */
    private ConsList<ParserFrame> activeFrames;

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

        Node endNode = new Node().isEndNode( true );
        System.out.println( "endNode = " + System.identityHashCode(endNode) );
        System.out.println( "endNode = " + endNode );
        ParserFrame initialFrame = new ParserFrame( listener ).push( rootRule.name(), rootRule.startingNode(), endNode );
        initialFrame = initialFrame.getCurrentNode().getActions().justArrived( initialFrame );

        activeFrames = Nil.cons( initialFrame );
    }

    public boolean parse( char input ) {
        preparseStateChecks();

        ConsList<ParserFrame> nextFrames = Nil;

        for ( ParserFrame frame : activeFrames ) {
            nextFrames = nextFrames.append( frame.parse( lineNumber, columnNumber, input ) );
        }

        if ( DEBUG ) {
            System.out.println( "Received '"+input+"'" );

            for ( ParserFrame frame : nextFrames ) {
                System.out.println( "    "+frame );
            }
        }


        if ( nextFrames.isEmpty() ) {
            Set<CharacterPredicate> candidateNextInputs = calculateCandidateNextInputs();

            if ( candidateNextInputs.isEmpty() ) {
                listener.error( lineNumber, columnNumber, "unexpected input '"+input+"', no further input was expected" );
            } else {
                String formattedExpectation = formatCharacterPredicates( candidateNextInputs );

                listener.error( lineNumber, columnNumber, "unexpected input '"+input+"', expected '"+formattedExpectation+"'" );
            }

            return false;
        } else {
            activeFrames = nextFrames;

            incrementColumnAndLinePositionsGiven( input );

            return true;
        }
    }

    public void endOfStream() {
        preparseStateChecks();


        ConsList<ParserFrame> nextFrames = Nil;

        for ( ParserFrame frame : activeFrames ) {
            nextFrames = nextFrames.append( frame.endOfStream() );
        }


        if ( DEBUG ) {
            System.out.println( "Received EOS" );

            for ( ParserFrame frame : nextFrames ) {
                System.out.println( "    " + frame );
            }
        }

        hasFinished = true;

        if ( nextFrames.isEmpty() ) {
            Set<CharacterPredicate> candidateNextInputs  = calculateCandidateNextInputs();
            String                  formattedExpectation = formatCharacterPredicates( candidateNextInputs );

            listener.error( lineNumber, columnNumber, "end of stream reached when expecting '"+formattedExpectation+"'" );
        } else {
            activeFrames = nextFrames;

            for ( MethodCall action : nextFrames.head().getActions() ) {
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
        if ( activeFrames.isEmpty() ) {
            return Collections.EMPTY_LIST;
        } else {
            return activeFrames.head().getValue().toList();
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

        for ( ParserFrame ctx : activeFrames ) {
            candidates.addAll( ctx.getCurrentNode().getOutPredicates() );
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


}
