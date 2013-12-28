package com.softwaremosaic.parsers;

import com.mosaic.collections.KV;
import com.mosaic.io.Formatter;
import com.mosaic.lang.Validate;
import com.softwaremosaic.parsers.automata.Label;
import com.softwaremosaic.parsers.automata.LabelNode;
import com.softwaremosaic.parsers.automata.Labels;
import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;
import com.softwaremosaic.parsers.automata.ProductionRule;
import com.softwaremosaic.parsers.automata.regexp.RegExpCharacterUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;


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

    /**
     * A map of 'next steps' is a map containing all of the out edges from a node
     * grouped by the destination node labels.  Used for formatting what the
     * next step for the parser could be.
     */
    private static final Formatter<Map.Entry<String, List<KV<Character,LabelNode>>>> NEXT_STEPS_FORMATTER = new Formatter<Map.Entry<String, List<KV<Character,LabelNode>>>>() {
        public void write( Appendable buf, Map.Entry<String, List<KV<Character, LabelNode>>> step ) throws IOException {
            String             label      = step.getKey();
            TreeSet<Character> characters = new TreeSet();

            for ( KV<Character, LabelNode> p : step.getValue() ) {
                characters.add( p.getKey() );
            }


            buf.append('\'');
            RegExpCharacterUtils.formatCharacters((StringBuilder) buf, characters);
            buf.append( " -> " );
            buf.append( label );
            buf.append('\'');
        }
    };

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


    private class ParserState {

        private ProductionRule productionRule;
        private Node           currentNode;


        public ParserState( ProductionRule productionRule ) {
            this( productionRule, productionRule.getStartingNode() );
        }

        private ParserState( ProductionRule productionRule, Node node ) {
            this.productionRule = productionRule;
            this.currentNode    = node;
        }

        public Collection<? extends Label> getNextCandidateLabels() {
            return currentNode.getOutLabels();
        }

        public void consume( List<ParserState> nextStates, T input ) {
            Nodes<T> next = currentNode.walk( input );

            for ( Node<T> n : next ) {
                nextStates.add( new ParserState(productionRule,n) );
            }
        }
    }

}
