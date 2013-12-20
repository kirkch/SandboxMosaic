package com.softwaremosaic.parsers;

import com.mosaic.collections.KV;
import com.mosaic.io.Formatter;
import com.mosaic.lang.Validate;
import com.mosaic.lang.functional.Function1;
import com.mosaic.utils.ListUtils;
import com.mosaic.utils.MapUtils;
import com.mosaic.utils.StringUtils;
import com.softwaremosaic.parsers.automata.Automata;
import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.regexp.RegExpCharacterUtils;

import java.io.IOException;
import java.util.*;


/**
 *
 */
@SuppressWarnings("unchecked")
public class Parser {

    public static Parser compile( Automata automata, ParserListener l ) {
        return new Parser(automata, l);
    }


    private Automata       automata;
    private ParserListener listener;

    private List<Node> currentNodes;
    private boolean isFirstNode = true;
    private boolean hasReachedEOS = false;

    private int col  = 1;
    private int line = 1;

    public Parser( Automata automata, ParserListener listener ) {
        Validate.notNull( automata, "automata" );
        Validate.notNull( listener, "listener" );

        this.automata = automata;
        this.listener = listener;

        reset();
    }



    public int append( CharSequence text ) {
        throwIfNotAcceptingAppends();

        if ( isFirstNode ) {
            listener.started();

            isFirstNode = false;
        }

        final int numChars = text.length();
        for ( int i=0; i<numChars; i++ ) {
            char c = text.charAt(i);

            if ( !walk(c) ) {
                return i;
            }
        }

        return numChars;
    }

    /**
     *
     * @DesignNote this method generates the listener.finished() call; it could
     *   be done as we append characters however the efficiency saving of
     *   not explicitly checking and then rechecking is worth while
     */
    public void appendEOS() {
        throwIfNotAcceptingAppends();

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

        this.currentNodes  = Arrays.asList(automata.getStartingNode());
    }

    private boolean walk( char c ) {
        List<Node> nextNodes = considerNextStep(c);

        if ( nextNodes.isEmpty() ) {
            reportUnexpectedCharacter(c);
            return false;
        }

        incrementColumnAndLinePositionsGiven(c);

        currentNodes = nextNodes;

        return true;
    }

    private void throwIfNotAcceptingAppends() {
        Validate.isFalseState(hasReachedEOS, "the parser has already been notified of EOS");
    }

    private void incrementColumnAndLinePositionsGiven( char c ) {
        switch (c) {
            case '\n':
                col   = 1;
                line += 1;
                break;
            case '\r':
                break; // skip
            default:
                col += 1;
        }
    }

    private void reportUnexpectedCharacter( char c ) {
        Map<String, List<KV<Character,Node>>> candidateNextSteps = fetchAllCandidateNextSteps();

        switch ( candidateNextSteps.size() ) {
            case 0:
                listener.error( line, col, "unexpected character '" + c + "', no further input was expected" );
                break;
            default:
                listener.error( line, col, "unexpected character '" + c + "', expected "+prettyPrint(candidateNextSteps, " or ") );
        }
    }

    private String prettyPrint( Map<String, List<KV<Character, Node>>> candidateNextSteps, String separator ) {
        Set<Map.Entry<String, List<KV<Character, Node>>>> elements = MapUtils.toTreeMap(candidateNextSteps).entrySet();

        return StringUtils.join(elements, separator, NEXT_STEPS_FORMATTER );
    }

    /**
     * A map of 'next steps' is a map containing all of the out edges from a node
     * grouped by the destination node labels.  Used for formatting what the
     * next step for the parser could be.
     */
    private static final Formatter<Map.Entry<String, List<KV<Character,Node>>>> NEXT_STEPS_FORMATTER = new Formatter<Map.Entry<String, List<KV<Character,Node>>>>() {
        public void write( Appendable buf, Map.Entry<String, List<KV<Character, Node>>> step ) throws IOException {
            String             label      = step.getKey();
            TreeSet<Character> characters = new TreeSet();

            for ( KV<Character, Node> p : step.getValue() ) {
                characters.add( p.getKey() );
            }


            buf.append('\'');
            RegExpCharacterUtils.formatCharacters((StringBuilder) buf, characters);
            buf.append( " -> " );
            buf.append( label );
            buf.append('\'');
        }
    };

    private Map<String, List<KV<Character,Node>>> fetchAllCandidateNextSteps() {
        List<List<KV<Character,Node>>> allOutEdges = new ArrayList();

        for ( Node n : currentNodes ) {
            allOutEdges.add(n.getOutEdges());
        }

        return ListUtils.groupBy(
                ListUtils.flatten(allOutEdges),
                new Function1<KV<Character,Node>,String>() {
                    public String invoke( KV<Character,Node> pair ) {
                        return pair.getValue().getLabel();
                    }
                });
    }

    /**
     * Returns the next nodes if char c was to be taken.
     */
    private List<Node> considerNextStep(char c) {
        List<List<Node>> nextNodes = new ArrayList();
        for ( Node n : currentNodes ) {
            nextNodes.add(n.walk(c));
        }

        return ListUtils.flatten(nextNodes);
    }

}
