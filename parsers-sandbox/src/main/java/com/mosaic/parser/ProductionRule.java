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
public class ProductionRule {

    private String name;
    private Node   startingNode;
    private Nodes  endNodes;


    public ProductionRule( String name, Node startingNode, Nodes endNodes ) {
        this.name         = name;
        this.startingNode = startingNode;
        this.endNodes     = endNodes;
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

    public ProductionRule withCallback( Class listenerClass, String methodName ) {
        final MethodRef action = MethodRef.create( listenerClass, methodName, Integer.TYPE, Integer.TYPE, String.class );

        endNodes.wrapActions( new Function1<ParserFrameOp, ParserFrameOp>() {
            public ParserFrameOp invoke( ParserFrameOp currentOp ) {
                return ParserFrameOps.callbackOp( currentOp, action );
            }
        } );

        return this;
    }

    protected ProductionRule clone() {
        try {
            return (ProductionRule) super.clone();
        } catch ( CloneNotSupportedException e ) {
            throw ReflectionException.recast(e);
        }
    }

}
