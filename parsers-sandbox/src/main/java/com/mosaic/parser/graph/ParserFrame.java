package com.mosaic.parser.graph;

import com.mosaic.collections.ConsList;
import com.mosaic.lang.reflect.MethodCall;
import com.mosaic.lang.reflect.MethodRef;
import com.mosaic.lang.reflect.ReflectionException;
import com.mosaic.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.mosaic.collections.ConsList.Nil;

/**
*
*/
@SuppressWarnings("unchecked")
public class ParserFrame implements Cloneable {
    private String               productionRuleName;

    private Node                 currentNode;
    private ConsList             currentValue = Nil;
    private ConsList<MethodCall> actions      = Nil;
    private ParserListener       listener;


    private int frameStartedAtLineNumber   = 1;
    private int frameStartedAtColumnNumber = 1;

    private int currentLineNumber   = 1;
    private int currentColumnNumber = 1;


    private ParserFrame parentContext;

    public String toString() {
        String next = StringUtils.join( currentNode.getOutPredicates(), "," );
        String v = StringUtils.join( currentValue, "," );
        String a = StringUtils.join( actions, "," );

        return productionRuleName + "(next=" + next + ", value="+v+", actions="+a+")   -->  " + parentContext;
    }

    public ParserFrame( ParserListener listener ) {
        this( "ROOT", null, listener );
    }

    public ParserFrame( String name, Node node, ParserListener listener ) {
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

    public Node getCurrentNode() {
        return currentNode;
    }

    public ConsList<MethodCall> getActions() {
        return actions;
    }

    public Iterable<ParserFrame> parse( int line, int col, final char c ) {
        Nodes nextNodes =  currentNode.fetch( c );
        if ( nextNodes.isEmpty() ) {
            return Collections.EMPTY_LIST;
        }

        ParserFrame parserState = this.clone();

        parserState.currentLineNumber   = line;
        parserState.currentColumnNumber = col;

        if ( nextNodes.hasContents() ) {
            parserState = currentNode.getActions().consumed( c, parserState );
        }

        final ParserFrame stateBeforeNotifyingNextNode = parserState;

        List<ParserFrame> nextFrames = new ArrayList( nextNodes.size()*2 );
        for ( Node n : nextNodes ) {
            ParserFrame nextContext = stateBeforeNotifyingNextNode.withNextNode( n );

            nextContext = n.getActions().justArrived( nextContext );

            nextFrames.add(nextContext);

            // when on an end node, start a new frame in case it has been popped
            if ( n.isEndNode() && nextContext != null ) {
                nextFrames.add( nextContext.pop() );
            }
        }

        return nextFrames;
    }

    public Iterable<ParserFrame> endOfStream() {
        ParserFrame nextContext = currentNode.getActions().productionRuleFinished(this);

        if ( nextContext != null ) {
            return Arrays.asList( nextContext );
        } else {
            return Collections.EMPTY_LIST;
        }
    }


    private ParserFrame withNextNode( Node nextNode ) {
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
        if ( currentValue.isEmpty() ) {
            throw new IllegalStateException("Unable to append action '"+callbackMethodRef+"' as no value has been captured by line "+frameStartedAtLineNumber+" column "+frameStartedAtColumnNumber+" as part of production rule '"+productionRuleName+"'.");
        }

        ParserFrame clone  = this.clone();
        MethodCall  action = new MethodCall( callbackMethodRef, listener, frameStartedAtLineNumber, frameStartedAtColumnNumber, currentValue.head() );

        clone.actions = this.actions.cons( action );

        return clone;
    }

    public ParserFrame push( String targetRuleName, Node nextNode, Node returnNode ) {
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

        return returnFrame.currentNode.getActions().justArrived( returnFrame );

//            return returnFrame;
    }
}
