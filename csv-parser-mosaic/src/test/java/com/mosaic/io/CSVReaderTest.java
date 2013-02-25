package com.mosaic.io;

/**
 *
 */
public class CSVReaderTest {

    // givenNullReader_expectErrorFromCSVReaderConstructor
    // givenEmptyReader_expectEmptyHeaders
    // givenEmptyReader_expectFalseForNext
    // givenEmptyReader_expectForLoopOverCSVReaderToIterateOverNoRows
    // givenIOExceptionReadingHeaders_expectIOException
    // givenIOExceptionReadingRow_expectIOException

    // givenSingleRowSingleColumnCSVFile_expectSingleHeaderNoRows






    // push parser   (ala incremental push parser/itteratee)
    //   pros:  async ready, very efficient
    //   cons:  code readability; often requires a state machine diagram along side the code to help make sense of it

    // pull parser (ala incremental pull parser)
    //   pros: state machine encoded in procedual code; easy to read and maintain, and is efficient
    //   cons: not async ready (blocking by nature)



    // pushParser.pushBytes(bytes) -> PushParser


    // requires use of callback



    //  columnValue:String ->     \"([^\",]*)\"|([^\",]*)
    //  row:List<String>   -> columnValue [, columnValue]* EOL


    // IMatcher
    // (ability to print out grammer)

    // regexp("[^\".]")


    // columnValue -> (comma -> columnValue)*
    // quotedColumnValue ->


    // IMatcher[String] columnValueMatcher     = createRegexpIMatcher( "[^\",]*" );
    // IMatcher[String] columnSeparatorMatcher = createConstantIMatcher( "," );


    // IMatcher[List[String]] rowMatcher = createAndMatcher(
    //     columnValueMatcher,
    //     createOptionalMatcher(createAndMatcher(columnSeparatorMatcher,columnValueMatcher))
    // );

    // rowMatcher.


    // IMatcher[List[String]] rowMatcher = columnValueMatcher.and( optional(columnSeparatorMatcher.and(columnValueMatcher)) );
    // rowMatcher.or( EOLMatcher )



// The PUSH framework (Java)


    // IMatcher[String] columnValueMatcher     = new CSVColumnValueIMatcher();
    // IMatcher[String] whitespaceMatcher      = new WhiteSpaceIMatcher().skip();
    // IMatcher[String] columnSeparatorMatcher = new ConstantIMatcher(",");
    // IMatcher[String] eolMatcher             = new EOLMatcher();



    // IMatcher[String]       tokenizer            = new OrIMatcher( columnValueMatcher, whitespaceMatcher, columnSeparatorMatcher, eolMatcher );
    // IMatcher[List[String]] columnValueCollector = new ListBuilderIMatcher().completeListOn( eolMatcher );






// new ParserChain( tokenizer, columnValueCollector, columns2BeanMapper )
//      .withCallbackHandler(
//          new ParserChainDelegate(
//              public void valueEmittedFromParserChain( B value ) {
//              }
//          )
//      )
//      .withErrorRecovery( logErrorHandler.andThen(skipToEndOfLineErrorHandler) );




    // FSM/Automata  PUSH Automata

    // Neurons



}