package com.softwaremosaic.parsers;

import com.mosaic.collections.ConsList;
import com.mosaic.lang.Validate;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.reflect.MethodCall;
import com.mosaic.lang.reflect.MethodRef;
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


/**
 * A non-blocking/incremental parser that supports different input types.  It
 * is as happy parsing bytes, enum or classes as it is characters. <p/>
 *
 * A parser is made up of production rules, where each rule expresses as
 * a finite state machine a series of matches that are valid.
 */
@SuppressWarnings("unchecked")
public abstract class Parser<T extends Comparable<T>> {

    @SuppressWarnings("FieldCanBeLocal")
    private static boolean DEBUG = true;


    private ParserListener listener;


    private boolean isFirstNode = true;
    private boolean hasReachedEOS = false;

    protected int col  = 1;
    protected int line = 1;


    private ProductionRule              startingRule;


    /**
     * There is one state per candidate route through the parse tree at this point
     * in time.  Callbacks will not be made while there is any ambiguity in which
     * state is the state.
     */
    private List<ParserState> currentStates;


    public Parser( ProductionRule start, ParserListener listener ) {
        Validate.notNull( listener, "listener" );

        this.startingRule    = start;
        this.listener        = listener;

        reset();
    }

    public boolean consume( T input ) {
        throwIfNotAcceptingInput();

        if ( isFirstNode ) {
            listener.started();

            isFirstNode = false;
        }

        boolean wasSuccessfullyParsed = walk( input );

        debug( "Consumed '"+input+"': " + currentStates );

        return wasSuccessfullyParsed;
    }

    public int consume( Iterable<T> input ) {
        int count = 0;

        for ( T v : input ) {
            if ( this.consume(v) ) {
                count++;
            } else {
                return count;
            }
        }

        return count;
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


        currentStates = consumeEOS();

        debug( "Consumed EOS: " + currentStates );

        int numStates = currentStates.size();
        if ( numStates > 1 ) {
            throw new IllegalStateException( "Ambiguous parser state: " + currentStates );
        } else if ( numStates == 1 ) {
            currentStates.get(0).fireActions();
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


        debug( "Reset: " + currentStates );
    }

    private boolean walk( T input ) {
        List<ParserState> nextStates = consumeInput( input );

        if ( nextStates.isEmpty() ) {
            reportUnexpectedCharacter( input );
            return false;
        } else if ( nextStates.size() == 1 ) {
            nextStates.set( 0, nextStates.get( 0 ).fireActions() );
        }

        incrementColumnAndLinePositionsGiven(input);

        currentStates = nextStates;

        return true;
    }

    private void throwIfNotAcceptingInput() {
        Validate.isFalse(hasReachedEOS, "the parser has already been notified of EOS");
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
    private List<ParserState> consumeInput( T input ) {
        List<ParserState> nextStates = new ArrayList();

        for ( ParserState state : currentStates ) {
            state.consume( nextStates, input );
        }

        return nextStates;
    }

    private List<ParserState> consumeEOS() {
        List<ParserState> nextStates = new ArrayList();

        for ( ParserState state : currentStates ) {
            state.consumeEOS( nextStates );
        }

        return nextStates;
    }


    private void debug( String msg ) {
        if ( DEBUG ) {
            System.out.println( msg );
        }
    }





    private class ParserState {

        private final ConsList<StackFrame> stack;



        // constructor used when starting out
        public ParserState( ProductionRule firstRule ) {
            this( ConsList.Nil.cons( new StackFrame( line, col, firstRule ) ) );
        }

        private ParserState( ConsList<StackFrame> stack ) {
            this.stack = stack;
        }


        public void consume( List<ParserState> nextStatesOutput, T input ) {
            StackFrame currentFrame = stack.head();

            if ( currentFrame.currentNode != null) {
                traverseNextEdgeInGraph( nextStatesOutput, input, currentFrame );
            } else if ( currentFrame.nextRules.hasContents() ) {
                ParserState newState = pushNextRule();

                newState.consume( nextStatesOutput, input );
            } else if ( stack.tail().isEmpty() ) {
                // skip
            } else { // pass value back up the stack
                ParserState newState = popCurrentRule();

                newState.consume( nextStatesOutput, input );
            }
        }

        public void consumeEOS( List<ParserState> nextStatesOutput ) {
            ConsList<StackFrame> s = this.stack;

            // while each stack frame is at a valid end node, pop its value and roll it up the stack
            while ( s.hasContents() && s.head().currentNode != null && s.head().currentNode.isValidEndNode() ) {
                s = popHeadRule(s);
            }


            if ( s.hasContents() && s.tail().isEmpty() && s.head().currentNode == null ) {
                nextStatesOutput.add( new ParserState( s ) );   // return the current value (parse was successful)
            }
        }

        /**
         * Retrieves the candidate next nodes given the current position.  Used
         * for error reporting.
         */
        public Collection<? extends Label> getNextCandidateLabels() {
            return stack.head().currentNode.getOutLabels();
        }

        public ParserState fireActions() {
            for ( MethodCall m : stack.head().actions ) {
                debug( "FIRE on: " + m );

                m.invoke();
            }

            return this;  // todo return a version that has the actions removed!
        }

        private void traverseNextEdgeInGraph( List<ParserState> nextStatesOutput, T input, StackFrame currentFrame ) {
            Nodes nextNodes = currentFrame.currentNode.walk( input );

            if ( nextNodes.isEmpty() && currentFrame.currentNode.isValidEndNode() ) {
                ParserState poppedState = this.popCurrentRule();

                poppedState.consume( nextStatesOutput, input );
            } else {
                appendNextNodes( nextStatesOutput, input, currentFrame, nextNodes );
            }
        }

        private void appendNextNodes( List<ParserState> nextStatesOutput, T input, StackFrame currentFrame, Nodes nextNodes ) {
            Iterator it = nextNodes.iterator();
            while ( it.hasNext() ) {
                Node n = (Node) it.next();

                StackFrame updatedFrame = currentFrame.consumedValue( input, n );

                ParserState newState = this.replaceHeadWith( updatedFrame );

//                if ( n.hasOutEdges() ) {
                nextStatesOutput.add( newState );
//                }
            }
        }

        private ParserState pushNextRule() {
            ConsList<StackFrame> tail = stack.tail();
            StackFrame updatedOldHead = stack.head().popNextRule();
            StackFrame newHead = new StackFrame( line, col, stack.head().nextRules.head() );

            return new ParserState( tail.cons(updatedOldHead).cons(newHead) );
        }

        private ParserState popCurrentRule() {
            return new ParserState( popHeadRule(this.stack) );
        }

        private ConsList<StackFrame> popHeadRule( ConsList<StackFrame> s ) {
            ConsList<StackFrame> tail = s.tail();
            StackFrame           head = s.head();

            if ( tail.isEmpty() ) {
                ConsList<MethodCall> actions = head.getActions( listener );

                StackFrame newStackFrame = new StackFrame( head.returnValueCollectedSoFar, actions );

                return ConsList.newConsList( newStackFrame );
            } else {
                StackFrame newHead = tail.head().consume( head, listener );

                return tail.tail().cons(newHead);
            }
        }

        private ParserState replaceHeadWith( StackFrame updatedFrame ) {
            return new ParserState( this.stack.tail().cons( updatedFrame ) );
        }

        public String toString() {
            return stack.toString();
        }
    }


    private static class StackFrame {
        public final ProductionRule rule;
        public final Node           currentNode;

        public final ConsList<ProductionRule> nextRules;

        public final ConsList returnValueCollectedSoFar;

        private final ConsList<MethodCall> actions;


        private int startedAtLine;
        private int startedAtColumn;


        public String toString() {
            String ruleStr = rule == null ? "nullstr" : rule.toString();

            return ruleStr + "@(" + ( currentNode == null ? nextRules : currentNode)+ ",v="+returnValueCollectedSoFar+",a="+actions +")";
        }


        // invoked when the last stack frame has been popped  ---- hmmmm todo
        public StackFrame( ConsList value, ConsList<MethodCall> callbacks ) {
            rule        = null;
            currentNode = null;
            nextRules   = ConsList.Nil;
            actions     = callbacks;

            this.returnValueCollectedSoFar = value;
        }

        // used when going deeper into the tree
        public StackFrame( int line, int col, ProductionRule rule ) {
            this.rule                      = rule;
            this.returnValueCollectedSoFar = ConsList.Nil;
            this.actions                   = ConsList.Nil;

            this.startedAtLine   = line;
            this.startedAtColumn = col;

            if ( rule.isTerminal() ) {
                currentNode = rule.getNode();
                nextRules   = ConsList.Nil;
            } else {
                currentNode = null;
                nextRules   = rule.getChildRules();
            }
        }

        private StackFrame( StackFrame frameToDuplicate, Object input, Node n, ConsList<MethodCall> mergedActions ) {
            this.rule                      = frameToDuplicate.rule;
            this.currentNode               = n;
            this.actions                   = mergedActions;

            this.nextRules                 = frameToDuplicate.nextRules;


            this.startedAtLine   = frameToDuplicate.startedAtLine;
            this.startedAtColumn = frameToDuplicate.startedAtColumn;

            if ( rule.isCapture() ) {
                this.returnValueCollectedSoFar = frameToDuplicate.returnValueCollectedSoFar.cons( input );
            } else {
                this.returnValueCollectedSoFar = frameToDuplicate.returnValueCollectedSoFar;
            }
        }

        // called by popNextFrame
        private StackFrame( StackFrame frameToDuplicate ) {
            this.rule                      = frameToDuplicate.rule;
            this.currentNode               = frameToDuplicate.currentNode;
            this.nextRules                 = frameToDuplicate.nextRules.tail();
            this.actions                   = frameToDuplicate.actions;


            this.returnValueCollectedSoFar = frameToDuplicate.returnValueCollectedSoFar;
        }

        public StackFrame consumedValue( Object input, Node nextNode ) {
            return new StackFrame( this, input, nextNode, this.actions );
        }

        public StackFrame popNextRule() {
            return new StackFrame( this );
        }

        public StackFrame consume( StackFrame oldFrameBeingPopped, ParserListener listener ) {
            ConsList<MethodCall> mergedActions = this.actions.append( oldFrameBeingPopped.getActions(listener) );

            ConsList v = oldFrameBeingPopped.getValue();

            return new StackFrame( this, v, this.currentNode, mergedActions );
        }

        private ConsList getValue() {
            Function1<ConsList,ConsList> postProcess = rule.getPostProcess();

            return postProcess.invoke( returnValueCollectedSoFar );
        }

        public ConsList<MethodCall> getActions( ParserListener listener ) {
            ConsList<MethodCall> actions = this.actions;

            MethodRef callback = rule.getListenerCallback();
            if ( callback != null ) {
                Object[] args = createCallbackArgs();

                actions = actions.cons( new MethodCall(callback, listener, args ) );
            }

            return actions;
        }

        private Object[] createCallbackArgs() {
            ConsList argsList = getValue();
            List     result   = new ArrayList();


            result.add( startedAtLine );
            result.add( startedAtColumn );

            for ( Object v : argsList ) {
                result.add( v );
            }

            return result.toArray();
        }
    }



}
