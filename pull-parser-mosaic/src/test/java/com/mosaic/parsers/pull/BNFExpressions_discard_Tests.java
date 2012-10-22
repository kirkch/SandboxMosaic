package com.mosaic.parsers.pull;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static com.mosaic.parsers.pull.BNFExpressions.constant;
import static com.mosaic.parsers.pull.BNFExpressions.discard;
import static org.junit.Assert.*;

/**
 *
 */
public class BNFExpressions_discard_Tests {

    @Test
    public void givenNoMatch_parseOption_expectNull() throws IOException {
        BNFExpression exp = discard( constant("public") );
        Tokenizer     in  = new Tokenizer( new StringReader("private Account") );

        Match<String> result = exp.parseOptional( null, in );

        assertNull( result );
    }

    @Test
    public void givenNoMatch_parseMandatory_expectError() throws IOException {
        BNFExpression exp = discard( constant("public") );
        Tokenizer     in  = new Tokenizer( new StringReader("private Account") );

        Match<String> result = exp.parseMandatory( null, in );

        assertTrue( result.isError() );
    }

    @Test
    public void givenMatch_parseMandatory_expectNull() throws IOException {
        BNFExpression exp = discard( constant("public") );
        Tokenizer     in  = new Tokenizer( new StringReader("public Account") );

        Match<String> result = exp.parseMandatory( null, in );

        assertNull( result );
    }

    @Test
    public void givenMatch_parseOptional_expectNull() throws IOException {
        BNFExpression exp = discard( constant("public") );
        Tokenizer     in  = new Tokenizer( new StringReader("public Account") );

        Match<String> result = exp.parseMandatory( null, in );

        assertNull( result );
    }

}
