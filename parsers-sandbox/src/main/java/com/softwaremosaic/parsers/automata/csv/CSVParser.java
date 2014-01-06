package com.softwaremosaic.parsers.automata.csv;

import com.softwaremosaic.parsers.CharacterParser;
import com.softwaremosaic.parsers.ProductionRuleBuilder;
import com.softwaremosaic.parsers.automata.ProductionRule;

import java.util.List;

/**
 *
 */
public class CSVParser extends CharacterParser {

    private static ProductionRule columnRule = ProductionRuleBuilder.terminalRegexp( "[a-zA-Z]+" ).withLabel( "ColumnRule" )
        .withCapture( true );


    private static ProductionRule rowRule = ProductionRule.nonTerminal( columnRule ).withLabel( "RowRule" )
        .withCapture( true )
        .withCallback( CSVListener.class, "headers", List.class );


    public CSVParser( CSVListener listener ) {
        super( rowRule, listener );
    }

}
