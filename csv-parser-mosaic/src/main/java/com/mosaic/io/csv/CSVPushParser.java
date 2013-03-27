package com.mosaic.io.csv;

import com.mosaic.parsers.push.Matcher;

import static com.mosaic.parsers.push.matchers.Matchers.constant;
import static com.mosaic.parsers.push.matchers.Matchers.skipWhitespace;

/**
 *
 */
public class CSVPushParser /* extends PushParser<CSVPushParser> */{



    private static final Matcher<String> csvColumn = skipWhitespace(new CSVColumnValueMatcher());
    private static final Matcher<String> comma     = skipWhitespace(constant(","));


//    private static final Matcher<List<String>> row  = listDemarcated( csvColumn, comma, eol() );
    // private static final Matcher               rows = zeroOrMore( issueCallbackAndSkip(row,this,"rowParsed",List<String>.class) )


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
