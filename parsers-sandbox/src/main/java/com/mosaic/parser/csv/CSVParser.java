package com.mosaic.parser.csv;

import com.mosaic.parser.ProductionRule;
import com.mosaic.parser.ProductionRuleBuilder;
import com.mosaic.parser.graph.Parser;

/**
 *
 */
public class CSVParser {

    static {
        ProductionRuleBuilder builder = new ProductionRuleBuilder();

        builder.terminal( "UNQUOTED_COLUMN_VALUE", "[^,]+", String.class );
        builder.terminal( "QUOTED_COLUMN_VALUE", "[^\"]+", String.class );
        builder.nonTerminal( "COLUMN_VALUE", "\"${QUOTED_COLUMN_VALUE}\"|$UNQUOTED_COLUMN_VALUE", String.class );

        rowRule = builder.nonTerminal( "ROW", "$COLUMN_VALUE(,$COLUMN_VALUE)*", String.class ).withCallback( CSVListener.class, "headers" );
    }


    private static ProductionRule rowRule;


    public int parse( String str, CSVListener l ) {
        Parser p = new Parser(rowRule, l );

        int count = p.parse( str );
        p.endOfStream();

        return count;
    }

}
