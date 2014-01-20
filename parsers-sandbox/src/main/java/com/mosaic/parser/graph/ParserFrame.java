package com.mosaic.parser.graph;

import com.mosaic.collections.ConsList;
import com.mosaic.lang.reflect.MethodCall;
import com.mosaic.lang.reflect.MethodRef;
import com.mosaic.lang.reflect.ReflectionException;
import com.mosaic.parser.ProductionRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.mosaic.collections.ConsList.Nil;

/**
*
*/
public class ParserFrame implements Cloneable {
    private String                         productionRuleName;

    private Node<ParserFrameOp> currentNode;
    private ConsList currentValue = Nil;
    private ConsList<MethodCall>           actions      = Nil;
    private ParserListener                 listener;


    private int frameStartedAtLineNumber   = 1;
    private int frameStartedAtColumnNumber = 1;

    private int currentLineNumber   = 1;
    private int currentColumnNumber = 1;


    private ParserFrame parentContext;


    public ParserFrame( ProductionRule rule, ParserListener listener ) {
        this( rule.name(), rule.startingNode(), listener );
    }

    public ParserFrame( String name, Node<ParserFrameOp> node, ParserListener listener ) {
        this.productionRuleName = name;
        this.currentNode        = node;
        this.listener           = listener;
    }

    public boolean isRootFrame() {
        return parentContext == null;
    }

    public ConsList getValue() {
        return currentValue;
    }

    public ParserFrame setValue( ConsList newValue ) {
        ParserFrame clone = this.clone();

        clone.currentValue = newValue;

        return clone;
    }

    public Node<ParserFrameOp> getCurrentNode() {
        return currentNode;
    }

    public ConsList<MethodCall> getActions() {
        return actions;
    }

    public Iterable<ParserFrame> parse( int line, int col, final char c ) {
        Nodes<ParserFrameOp> nextNodes =  currentNode.fetch( c );

        ParserFrame parserState = this.clone();

        parserState.currentLineNumber   = line;
        parserState.currentColumnNumber = col;

        if ( nextNodes.hasContents() ) {
            parserState = currentNode.getPayload().consumed( c, parserState );
        }

        final ParserFrame stateBeforeNotifyingNextNode = parserState;

        List<ParserFrame> nextFrames = new ArrayList( nextNodes.size()*2 );
        for ( Node<ParserFrameOp> n : nextNodes ) {
            ParserFrame nextContext = stateBeforeNotifyingNextNode.withNextNode( n );

            nextContext = n.getPayload().justArrived( nextContext );

            nextFrames.add(nextContext);

            if ( n.isEndNode() && nextContext != null ) {
                nextFrames.add( nextContext.pop() );
            }
        }

        return nextFrames;
    }

    public Iterable<ParserFrame> endOfStream() {
        ParserFrame nextContext = currentNode.getPayload().productionRuleFinished(this);

        if ( nextContext != null ) {
            return Arrays.asList( nextContext );
        } else {
            return Collections.EMPTY_LIST;
        }
    }


    private ParserFrame withNextNode( Node<ParserFrameOp> nextNode ) {
        ParserFrame clone = this.clone();

        clone.currentNode = nextNode;

        return clone;
    }

    protected ParserFrame clone() {
        try {
            return (ParserFrame) super.clone();
        } catch ( CloneNotSupportedException e ) {
            throw ReflectionException.recast( e );
        }
    }

    public ParserFrame appendInputValue( char c ) {
        ParserFrame clone = this.clone();

        clone.currentValue = this.currentValue.cons(c);

        return clone;
    }

    public ParserFrame appendAction( MethodRef callbackMethodRef ) {
        ParserFrame clone  = this.clone();
        MethodCall    action = new MethodCall( callbackMethodRef, listener, frameStartedAtLineNumber, frameStartedAtColumnNumber, currentValue.head() );

        clone.actions = this.actions.cons( action );

        return clone;
    }

    public ParserFrame push( String targetRuleName, Node<ParserFrameOp> nextNode, Node<ParserFrameOp> returnNode ) {
        ParserFrame returnFrame = this.withNextNode( returnNode );

        ParserFrame newFrame = this.clone();

        newFrame.productionRuleName         = targetRuleName;
        newFrame.frameStartedAtLineNumber   = this.currentLineNumber;
        newFrame.frameStartedAtColumnNumber = this.currentColumnNumber;
        newFrame.currentNode                = nextNode;
        newFrame.currentValue               = Nil;
        newFrame.actions                    = Nil;
        newFrame.parentContext              = returnFrame;


        return newFrame;
    }

    public ParserFrame pop() {
        if ( this.parentContext == null ) {
            return this;
        }

        ParserFrame returnFrame = this.parentContext.clone();

        returnFrame.currentLineNumber = this.currentLineNumber;
        returnFrame.currentLineNumber = this.currentColumnNumber;
        returnFrame.currentValue      = returnFrame.currentValue.append( this.currentValue );
        returnFrame.actions           = returnFrame.actions.append( this.actions );

        return returnFrame.currentNode.getPayload().justArrived( returnFrame );

//            return returnFrame;
    }
}
