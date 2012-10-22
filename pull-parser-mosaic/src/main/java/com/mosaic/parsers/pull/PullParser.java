package com.mosaic.parsers.pull;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class PullParser {

    private final Map<String, BNFExpression> expressions = new ConcurrentHashMap<String,BNFExpression>();

    public void register( String label, BNFExpression exp ) {
        expressions.put( label, exp );
    }

    public <L,T> Match<T> parse( String startingLabel, L runtimeContext, Tokenizer in ) throws IOException {
        BNFExpression exp = expressions.get(startingLabel);
        if ( exp == null ) {
            throw new IllegalArgumentException( "Unknown expression '%s'".format(startingLabel) );
        }

        return (Match<T>) exp.parseMandatory( runtimeContext, in );
    }

}
