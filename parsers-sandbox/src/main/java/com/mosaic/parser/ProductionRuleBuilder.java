package com.mosaic.parser;

import com.mosaic.lang.CaseSensitivity;
import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;
import com.mosaic.parser.graph.builder.NodeBuilder;
import com.mosaic.parser.graph.builder.NodeBuilders;
import com.mosaic.parser.graph.builder.RegexpParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mosaic.lang.CaseSensitivity.CaseSensitive;


/**
 *
 */
@SuppressWarnings("unchecked")
public class ProductionRuleBuilder {

    private Map<String,ProductionRule> rules = new HashMap();

    private RegexpParser parser = new RegexpParser();


    public ProductionRule<Void> constant( String name, String constant ) {
        return constant( name, constant, CaseSensitive );
    }

    public ProductionRule<Void> constant( String name, String constant, CaseSensitivity caseSensitivity ) {
        NodeBuilder builder = NodeBuilders.constant( constant, caseSensitivity );

        return makeRule( name, builder, Void.class );
    }

    public ProductionRule<String> terminal( String name, String constant, CaseSensitivity caseSensitivity ) {
        NodeBuilder builder = NodeBuilders.constant( constant, caseSensitivity ).isCapturing( true );

        return makeRule( name, builder, String.class );
    }

    public <T> ProductionRule<T> terminal( String name, String regexp, Class<T> capturedValueType ) {
        NodeBuilder builder = parser.parse( regexp, rules );

        if ( capturedValueType == String.class ) {  // todo support more types
            builder = builder.isCapturing(true);
        }

        return makeRule( name, builder, capturedValueType );
    }

    public ProductionRule<Void> nonTerminal( String name, String regexp ) {
        NodeBuilder builder = parser.parse( regexp, rules );

        return makeRule( name, builder, Void.class );
    }

    @SuppressWarnings("UnusedParameters")
    public <T> ProductionRule<List<T>> nonTerminal( String name, String regexp, Class<T> capturedValueType ) {
        NodeBuilder builder = parser.parse( regexp, rules );

        return (ProductionRule<List<T>>) makeRuleW( name, builder, List.class );
    }




    // used for a type cast hack to get around a compiler error
    private <T> Object makeRuleW( String name, NodeBuilder builder, Class<T> capturedValueType ) {
        return makeRule(name,builder,capturedValueType);
    }

    private <T> ProductionRule<T> makeRule( String name, NodeBuilder builder, Class<T> capturedValueType ) {
        if ( rules.containsKey(name) ) {
            throw new IllegalArgumentException( "'"+name+"' has already been declared" );
        }

        Node  firstNode = new Node();
        Nodes endNodes  = builder.appendTo( firstNode );

        endNodes.isEndNode( true );

        ProductionRule productionRule = new ProductionRule( name, firstNode, endNodes, capturedValueType );

        rules.put( name, productionRule );

        return productionRule;
    }

}
