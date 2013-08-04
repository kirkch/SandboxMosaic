package com.mosaic.io.csv;

import com.mosaic.io.CharacterStream;
import com.mosaic.io.Characters;
import com.mosaic.lang.Validate;
import com.mosaic.parsers.push.Matcher;
import com.mosaic.parsers.push.matchers.ZeroOrMoreCallback;

import java.util.List;

import static com.mosaic.parsers.push.matchers.Matchers.*;

/**
 *
 */
public class CSVPushParser {

    private final Matcher<String>       csvColumn       = skipSpaceOrTab( new CSVColumnValueMatcher() ).withName("csvColumn");
    private final Matcher<String>       columnSeparator = skipSpaceOrTab( constant( "," ) ).withName("columnSeparator");
    private final Matcher<List<String>> row             = listDemarcated( alwaysMatches(), csvColumn, columnSeparator, eol() ).withName("csvRow");


    // csvColumn       = new CSVColumnValueMatcher()
    // columnSeparator = constant(",")
    // comment         = new CommentMatcher("#")

    // csvRow          = or(comment, repeat(csvColumn, columnSeparator).withCallback("rowReceived") )




    // artifactName = javaName().withName("artifactName")
    // colon        = constant(":")
    // artifactType = javaName().withName("artifactType")
    // moduleName   = javaName().withName("moduleName")
    // moduleNames  = repeat(moduleName,",").withName("moduleNames")
    //

    // artifactRow = and( artifactName, colon, artifactType, openBracket, moduleNames, closeBracket ).withCallback(artifactCallback)
    // artifactRow = and( artifactName, ":", artifactType, "(", moduleNames, ")" ).withCallback(artifactCallback)


    private final Matcher rows = zeroOrMoreWithCallbacks( row, new ZeroOrMoreCallback<List<String>>() {
        public void startOfBlockReceived( int lineNumber ) {
          delegate.parsingStarted();
        }

        public void valueReceived( int lineNumber, List<String> row ) {
            rowReceived( lineNumber, row );
        }

        public void endOfBlockReceived( int lineNumber ) {
          delegate.parsingEnded();
        }
    } );



    private CharacterStream       inputStream;
    private CSVPushParserDelegate delegate;

    private int parsedRowCount;

    public CSVPushParser( CSVPushParserDelegate delegate ) {
        Validate.notNull( delegate, "delegate" );

        this.delegate = delegate;
    }

    public int appendCharacters( Characters newCharacters ) {
        initStream();

        inputStream.appendCharacters( newCharacters );

        return processStream();
    }

    public int appendEOS() {
        inputStream.appendEOS();

        return processStream();
    }

    private void initStream() {
        if ( inputStream == null ) {
            inputStream = new CharacterStream();

            rows.withInputStream( inputStream );
        }
    }

    private int processStream() {
        int beforeRowCount = parsedRowCount;

        rows.processInput();

        int afterRowCount = parsedRowCount;

        return afterRowCount - beforeRowCount;
    }


    private void rowReceived( Integer lineNumber, List<String> row ) {
        parsedRowCount++;

        if ( parsedRowCount == 1 ) {
            delegate.headerRead( lineNumber, row );
        } else {
            delegate.rowRead( lineNumber, row );
        }
    }

}
