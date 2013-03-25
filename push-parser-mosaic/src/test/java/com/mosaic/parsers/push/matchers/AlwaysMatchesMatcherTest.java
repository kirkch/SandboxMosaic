package com.mosaic.parsers.push.matchers;

import com.mosaic.io.CharPosition;
import com.mosaic.io.CharacterStream;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class AlwaysMatchesMatcherTest {

    @Test
    public void givenBytes_expectMatchAndYetNoBytesConsumed() {
        CharacterStream stream  = new CharacterStream("const");
        Matcher<String> matcher = new AlwaysMatchesMatcher().withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( "", result.getResult() );
        assertEquals( new CharPosition(0,0,0), stream.getPosition() );
    }

}
