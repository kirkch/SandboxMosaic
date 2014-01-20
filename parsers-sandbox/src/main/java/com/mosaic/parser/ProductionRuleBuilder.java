package com.mosaic.parser;

import com.mosaic.collections.ConsList;
import com.mosaic.collections.KV;
import com.mosaic.lang.CaseSensitivity;
import com.mosaic.lang.CharacterPredicate;
import com.mosaic.lang.functional.VoidFunction2;
import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;
import com.mosaic.parser.graph.ParserFrameOp;
import com.mosaic.parser.graph.builder.TrieBuilderOp;
import com.mosaic.parser.graph.builder.TrieBuilders;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.mosaic.lang.CaseSensitivity.CaseSensitive;
import static com.mosaic.parser.graph.ParserFrameOps.*;


/**
 *
 */
@SuppressWarnings("unchecked")
public class ProductionRuleBuilder {

    private Map<String,ProductionRule> rules = new HashMap();



    public ProductionRule constant( String name, String constant ) {
        return constant( name, constant, CaseSensitive );
    }

    public ProductionRule constant( String name, String constant, CaseSensitivity caseSensitivity ) {
        TrieBuilderOp builder = TrieBuilders.constant( constant, caseSensitivity );

        return makeRule( name, builder, noOp(), popOp() );
    }

    public ProductionRule capturingConstant( String name, String constant ) {
        return capturingConstant( name, constant, CaseSensitive );
    }

    public ProductionRule capturingConstant( String name, String constant, CaseSensitivity caseSensitivity ) {
        TrieBuilderOp builder = TrieBuilders.constant( constant, caseSensitivity );

        return makeRule( name, builder, captureInputOp(), captureEndOp() );
    }

    public ProductionRule regexp( String name, String regexp ) {
        TrieBuilderOp builder = TrieBuilders.regexp( regexp );

        return makeRule( name, builder, noOp(), popOp() );
    }

    public ProductionRule capturingRegexp( String name, String regexp ) {
        TrieBuilderOp builder = TrieBuilders.regexp( regexp );

        return makeRule( name, builder, captureInputOp(), captureEndOp() );
    }




    private ProductionRule makeRule( String name, TrieBuilderOp builder, ParserFrameOp innerNodeOp, ParserFrameOp endNodeOp ) {
        return makeRule( name, builder, innerNodeOp, innerNodeOp, endNodeOp );
    }

    private ProductionRule makeRule( String name, TrieBuilderOp builder, ParserFrameOp firstNodeOp, ParserFrameOp innerNodeOp, ParserFrameOp endNodeOp ) {
        if ( rules.containsKey(name) ) {
            throw new IllegalArgumentException( "'"+name+"' has already been declared" );
        }

        Node firstNode = new Node();
        Nodes endNodes  = builder.appendTo( firstNode );


        setOp( firstNode, innerNodeOp );
        endNodes.setPayloads( endNodeOp );
        firstNode.setActions( firstNodeOp );
        endNodes.isEndNode( true );

        ProductionRule productionRule = new ProductionRule( name, firstNode, endNodes );

        rules.put( name, productionRule );

        return productionRule;
    }

    private void setOp( Node firstNode, final ParserFrameOp op ) {
        firstNode.depthFirstPrefixTraversal( new VoidFunction2<ConsList<KV<Set<CharacterPredicate>, Node>>, Boolean>() {
            public void invoke( ConsList<KV<Set<CharacterPredicate>, Node>> path, Boolean isEndOfPath ) {
                Node node = path.head().getValue();

                node.setActions( op );
            }
        } );
    }

}
