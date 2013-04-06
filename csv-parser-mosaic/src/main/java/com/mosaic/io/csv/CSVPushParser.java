package com.mosaic.io.csv;

import com.mosaic.io.CharacterStream;
import com.mosaic.lang.function.VoidFunction1;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;

import java.util.List;

import static com.mosaic.parsers.push.matchers.Matchers.*;

/**
 *
 */
public class CSVPushParser /* extends PushParser<CSVPushParser> */{



    private final Matcher<String> csvColumn = skipWhitespace(new CSVColumnValueMatcher()).withName("csvColumn");
    private final Matcher<String> comma     = skipWhitespace(constant(","));


    private final Matcher<List<String>> row  = listDemarcated( alwaysMatches(), csvColumn, comma, eol() ).withName("csvRow");
    private final Matcher               rows = zeroOrMore( issueCallbackAndSkip(row, new VoidFunction1<List<String>>() {
        @Override
        public void invoke( List<String> row ) {
            System.out.println( "row = " + row );
        }
    }) );


    public void appendCharacters( CharacterStream stream ) {
        rows.withInputStream( stream );

        System.out.println( "row = " + rows.toString() );
        MatchResult r = rows.processInput();
        System.out.println( "r = " + r );
    }


    // public CSVPushParser() {
    //   super( rows );
    // }

    // protected CSVPushParser( currentMatcher, characterBuffer ) {
    //   super( currentMatcher, characterBuffer );
    // }




// implemented in parent
//    public SELF processCharacters( Characters in ) {
//        Matcher newMatcher = currentMatcher.processCharacters(in);
//
//        return myConstructor.newInstance(newMatcher);
//    }
//
//    public int getLineNumber() {
//        return currentMatcher.getLineNumber();
//    }
//
//    public int getColumnNumber() {
//        return currentMatcher.getColumnNumber();
//    }

}
