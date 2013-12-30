package com.softwaremosaic.parsers;

import com.mosaic.collections.ConsList;
import com.mosaic.lang.Validate;
import com.softwaremosaic.parsers.automata.Label;
import com.softwaremosaic.parsers.automata.Labels;
import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;
import com.softwaremosaic.parsers.automata.ProductionRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A non-blocking/incremental parser that supports different input types.  It
 * is as happy parsing bytes, enum or classes as it is characters. <p/>
 *
 * A parser is made up of production rules, where each rule expresses as
 * a finite state machine a series of matches that are valid.
 */
@SuppressWarnings("unchecked")
public abstract class Parser<T extends Comparable<T>> {


    private ParserListener listener;


    private boolean isFirstNode = true;
    private boolean hasReachedEOS = false;

    protected int col  = 1;
    protected int line = 1;


    private ProductionRule              startingRule;
    private Map<String, ProductionRule> productionRules;


    /**
     * There is one state per candidate route through the parse tree at this point
     * in time.  Callbacks will not be made while there is any ambiguity in which
     * state is the state.
     */
    private List<ParserState> currentStates;


    public Parser( ProductionRule start, Map<String, ProductionRule> productionRules, ParserListener listener ) {
        Validate.notNull( listener, "listener" );

        this.startingRule    = start;
        this.productionRules = productionRules;
        this.listener        = listener;

        reset();
    }

    public boolean consume( T input ) {
        throwIfNotAcceptingInput();

        if ( isFirstNode ) {
            listener.started();

            isFirstNode = false;
        }

        return walk(input);
    }

    /**
     *
     * @DesignNote this method generates the listener.finished() call; it could
     *   be done as we append characters however the efficiency saving of
     *   not explicitly checking and then rechecking is worth while
     */
    public void appendEOS() {
        throwIfNotAcceptingInput();

        if ( isFirstNode ) {
            listener.started();

            isFirstNode = false;
        }

        listener.finished();

        hasReachedEOS = true;
    }

    public void reset() {
        this.col           = 1;
        this.line          = 1;

        this.isFirstNode   = true;
        this.hasReachedEOS = false;

        this.currentStates  = startingRule == null ? Collections.EMPTY_LIST : Arrays.asList( new ParserState(startingRule) );
    }

    private boolean walk( T input ) {
        List<ParserState> nextStates = considerNextStep(input);

        if ( nextStates.isEmpty() ) {
            reportUnexpectedCharacter( input );
            return false;
        }

        incrementColumnAndLinePositionsGiven(input);

        currentStates = nextStates;

        return true;
    }

    private void throwIfNotAcceptingInput() {
        Validate.isFalseState(hasReachedEOS, "the parser has already been notified of EOS");
    }

    protected void incrementColumnAndLinePositionsGiven( T input ) {
        col++;
    }

    private void reportUnexpectedCharacter( T input ) {
        List<Label> candidateLabels = fetchAllCandidateNextSteps();

        switch ( candidateLabels.size() ) {
            case 0:
                listener.error( line, col, "unexpected input '" + input + "', no further input was expected" );
                break;
            default:
                listener.error( line, col, "unexpected input '" + input + "', expected '"+prettyPrint(candidateLabels) + "'" );
        }
    }

    private String prettyPrint( List<Label> labels ) {
        return Labels.orValues( labels ).toString();
    }

    private List<Label> fetchAllCandidateNextSteps() {
        List<Label> allOutEdges = new ArrayList();

        for ( ParserState state : currentStates ) {
            allOutEdges.addAll( state.getNextCandidateLabels() );
        }

        return allOutEdges;
    }

    /**
     * Returns the next nodes if char c was to be taken.
     */
    private List<ParserState> considerNextStep( T input ) {
        List<ParserState> nextStates = new ArrayList();

        for ( ParserState state : currentStates ) {
            state.consume( nextStates, input );
        }

        return nextStates;
    }


    private static class StackFrame {
        public final ProductionRule rule;
        public final Node           currentNode;

        public final ConsList<ProductionRule> nextRules;

        public final ConsList returnValueCollectedSoFar;

        public StackFrame( ProductionRule rule ) {
            this.rule                      = rule;
            this.returnValueCollectedSoFar = ConsList.Nil;

            if ( rule.isTerminal() ) {
                currentNode = rule.getNode();
                nextRules   = ConsList.Nil;
            } else {
                currentNode = null;
                nextRules   = rule.getChildRules();
            }
        }

        private StackFrame( StackFrame frameToDuplicate, Object input, Node n ) {
            this.rule                      = frameToDuplicate.rule;
            this.currentNode               = n;

            this.nextRules                 = frameToDuplicate.nextRules;


            this.returnValueCollectedSoFar = frameToDuplicate.returnValueCollectedSoFar;
            if ( rule.isCapture() ) {
                this.returnValueCollectedSoFar.cons( input );
            }
        }

        public StackFrame consumedValue( Object input, Node nextNode ) {
            return new StackFrame( this, input, nextNode );
        }
    }


    private class ParserState {

        private final ConsList<StackFrame> stack;


        public ParserState( ProductionRule firstRule ) {
            this( ConsList.Nil.cons( new StackFrame( firstRule ) ) );
        }

        private ParserState( ConsList<StackFrame> stack ) {
            this.stack = stack;
        }


        public void consume( List<ParserState> nextStatesOutput, T input ) {
            StackFrame currentFrame = stack.head();

            if ( currentFrame.currentNode != null) {
                traverseNextEdgeInGraph( nextStatesOutput, input, currentFrame );
            }
//            else if ( currentFrame.nextRules.hasContents() ) {
//                ParserState newState = pushNextRule();
//
//                newState.consume( nextStatesOutput, input );
//            } else { // pass value back up the stack
//                ParserState newState = pushNextRule();
//
//                newState.consume( nextStatesOutput, input );
//            }
        }

        private void traverseNextEdgeInGraph( List<ParserState> nextStatesOutput, T input, StackFrame currentFrame ) {
            Nodes nextNodes = currentFrame.currentNode.walk( input );

            Iterator it = nextNodes.iterator();
            while ( it.hasNext() ) {
                Node n = (Node) it.next();

                if ( n.isValidEndNode() ) {

                } else {
                    StackFrame updatedFrame = currentFrame.consumedValue( input, n );

                    ParserState newState = this.replaceHeadWith( updatedFrame );
                    nextStatesOutput.add( newState );
                }
            }
        }

        private ParserState replaceHeadWith( StackFrame updatedFrame ) {
            return new ParserState( this.stack.tail().cons( updatedFrame ) );
        }

        /**
         * Retrieves the candidate next nodes given the current position.  Used
         * for error reporting.
         */
        public Collection<? extends Label> getNextCandidateLabels() {
            return stack.head().currentNode.getOutLabels();
        }
    }

}
