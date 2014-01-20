package com.mosaic.parser.graph;

import com.mosaic.parser.ProductionRule;
import com.mosaic.parser.ProductionRuleBuilder;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Benchmark;
import org.junit.Ignore;
import org.junit.runner.RunWith;

/**
 *
 */
@Ignore
@RunWith(JUnitMosaicRunner.class)
public class CharacterNodeTraversalPerformanceTests {

    private ProductionRule rootRule = new ProductionRuleBuilder().regexp( "NameRule", "[a-zA-Z]+" );
    private Node rootNode  = rootRule.startingNode();


    /**
     * with turbo turned off
     *
     * can we go faster with a custom impl of List that uses Unsafe?
     *
     * NB.  twice the speed of LabelNode
     44.13ns per char    => apx 27m characters/second  (38m with turbo enabled)
     35.97ns per char
     36.16ns per char
     35.65ns per char
     35.32ns per char
     35.51ns per char
     */

    @Benchmark(value=1000000, units = "char", durationResultMultiplier = 1.0/9 )
    public Object f() {
        Nodes n = rootNode.fetch( 'H' );
        n = n.fetch( 'e' );
        n = n.fetch( 'l' );
        n = n.fetch( 'l' );
        n = n.fetch( 'o' );
        n = n.fetch( ' ' );
        n = n.fetch( 'J' );
        n = n.fetch( 'i' );

        return n.fetch( 'm' );
    }

}
