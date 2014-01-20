package com.mosaic.parser.graph;

/**
*
*/
public interface ParserFrameOp {

    public ParserFrame justArrived( ParserFrame nextState );

    /**
     * Invoked as the parser arrives at a node, as the result of consuming
     * a character.
     */
    public ParserFrame consumed( char c, ParserFrame nextState );

    /**
     *
     * @return returns true when this is a valid place to finish the rule
     */
    public ParserFrame productionRuleFinished( ParserFrame nextState );

    /**
     * Append description of self to the string buffer for diagnostics.
     */
    public void appendOpCodesTo( StringBuilder buf );
}
