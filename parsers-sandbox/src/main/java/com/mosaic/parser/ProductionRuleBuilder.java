package com.mosaic.parser;

import com.mosaic.lang.CaseSensitivity;
import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;
import com.mosaic.parser.graph.builder.NodeBuilder;
import com.mosaic.parser.graph.builder.NodeBuilders;
import com.mosaic.parser.graph.builder.RegexpParser;

import java.util.HashMap;
import java.util.Map;

import static com.mosaic.lang.CaseSensitivity.CaseSensitive;


/**
 *
 */
@SuppressWarnings("unchecked")
public class ProductionRuleBuilder {

    private Map<String,ProductionRule> rules = new HashMap();

    private RegexpParser parser = new RegexpParser();


    public ProductionRule constant( String name, String constant ) {
        return constant( name, constant, CaseSensitive );
    }

    public ProductionRule constant( String name, String constant, CaseSensitivity caseSensitivity ) {
        NodeBuilder builder = NodeBuilders.constant( constant, caseSensitivity );

        return makeRule( name, builder );
    }

    public ProductionRule capturingConstant( String name, String constant ) {
        return capturingConstant( name, constant, CaseSensitive );
    }

    public ProductionRule capturingConstant( String name, String constant, CaseSensitivity caseSensitivity ) {
        NodeBuilder builder = NodeBuilders.constant( constant, caseSensitivity ).isCapturing( true );

        return makeRule( name, builder );
    }

    public ProductionRule regexp( String name, String regexp ) {
        NodeBuilder builder = parser.parse( regexp, rules );

        return makeRule( name, builder );
    }

    public ProductionRule capturingRegexp( String name, String regexp ) {
        NodeBuilder builder = parser.parse( regexp, rules ).isCapturing(true);

        return makeRule( name, builder );
    }




    private ProductionRule makeRule( String name, NodeBuilder builder ) {
        if ( rules.containsKey(name) ) {
            throw new IllegalArgumentException( "'"+name+"' has already been declared" );
        }

        Node firstNode = new Node();
        Nodes endNodes  = builder.appendTo( firstNode );

        endNodes.isEndNode( true );

        ProductionRule productionRule = new ProductionRule( name, firstNode, endNodes );

        rules.put( name, productionRule );

        return productionRule;
    }

}
