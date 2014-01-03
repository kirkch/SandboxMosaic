package com.softwaremosaic.parsers;

import com.softwaremosaic.parsers.automata.ProductionRule;

/**
 *
 */
public class CharacterParser extends Parser<Character> {

    public CharacterParser( ProductionRule start, ParserListener listener ) {
        super( start, listener );
    }

    protected void incrementColumnAndLinePositionsGiven( Character input ) {
        switch (input.charValue()) {
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

}
