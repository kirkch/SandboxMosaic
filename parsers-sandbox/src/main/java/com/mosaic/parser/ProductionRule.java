package com.mosaic.parser;

import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.reflect.MethodRef;
import com.mosaic.lang.reflect.ReflectionException;
import com.mosaic.parser.graph.ParserFrameOp;
import com.mosaic.parser.graph.ParserFrameOps;


/**
 *
 */
@SuppressWarnings("unchecked")
public class ProductionRule<T> {

    private String   name;
    private Node     startingNode;
    private Nodes    endNodes;

    private Class<T> capturedValueType;


    public ProductionRule( String name, Node startingNode, Nodes endNodes, Class<T> capturedValueType ) {
        this.name              = name;
        this.startingNode      = startingNode;
        this.endNodes          = endNodes;
        this.capturedValueType = capturedValueType;
    }


    public String name() {
        return name;
    }

    public Node startingNode() {
        return startingNode;
    }

    public String toString() {
        return "$"+name;
    }

    public ProductionRule<T> withCallback( Class listenerClass, String methodName ) {
        if ( capturedValueType == Void.class ) {
            throw new IllegalStateException( "Unable to append action '"+methodName+"' as the return type of the production rule '"+name+"' is 'Void'" );
        }

        final MethodRef action = MethodRef.create( listenerClass, methodName, Integer.TYPE, Integer.TYPE, capturedValueType );

        endNodes.wrapActions( new Function1<ParserFrameOp, ParserFrameOp>() {
            public ParserFrameOp invoke( ParserFrameOp currentOp ) {
                return ParserFrameOps.callbackOp( currentOp, action );
            }
        } );

        return this;
    }

    protected ProductionRule<T> clone() {
        try {
            return (ProductionRule<T>) super.clone();
        } catch ( CloneNotSupportedException e ) {
            throw ReflectionException.recast(e);
        }
    }

}
