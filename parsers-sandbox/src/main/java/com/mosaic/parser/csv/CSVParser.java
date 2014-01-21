package com.mosaic.parser.csv;

import com.mosaic.parser.ProductionRuleBuilder;

/**
 *
 */
public class CSVParser {

    static {
        ProductionRuleBuilder builder = new ProductionRuleBuilder();

//        builder.terminal( "COLUMN_VALUE", "[^,]+" );
//        builder.terminal( "ROW", "$COLUMN_VALUE (, $COLUMN_VALUE)*" ).withCallback( CSVListener.class, "row" );
    }
}
