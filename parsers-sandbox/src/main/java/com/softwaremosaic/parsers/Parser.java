package com.softwaremosaic.parsers;

import com.mosaic.lang.Validate;
import com.softwaremosaic.parsers.automata.Automata;
import com.softwaremosaic.parsers.automata.Node;

/**
 *
 */
public class Parser {

    public static Parser compile( Automata automata, ParserListener l ) {
        return new Parser(automata, l);
    }


    private Automata       automata;
    private ParserListener listener;

    private Node currentNode;

    public Parser( Automata automata, ParserListener listener ) {
        Validate.notNull( automata, "automata" );
        Validate.notNull( listener, "listener" );

        this.automata = automata;
        this.listener = listener;

        this.currentNode = automata.getStartingNode();
    }



    public int append( CharSequence text ) {
        listener.started();

        char c = text.charAt(0);

        if ( currentNode.isTerminal() ) {
            listener.error( 1, 1, "unexpected character '" + c + "', no further input was expected" );
        } else {
            listener.error( 1, 1, "unexpected character '" + c + "', expected 'ConstantA'" );
        }

        return 0;
    }

}
