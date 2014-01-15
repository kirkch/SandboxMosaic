package com.softwaremosaic.parsers.automata;

import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Benchmark;
import com.softwaremosaic.parsers.automata.regexp.RegexpParser;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(JUnitMosaicRunner.class)
@SuppressWarnings("unchecked")
public class LabelNodeTraversalPerformanceTest {

    private LabelNode rootNode  = new LabelNode();
    {
        new RegexpParser().compile("[a-zA-Z]+").appendTo( rootNode );
    }

private static final Character H = 'H';
private static final Character E = 'e';
private static final Character L = 'l';
private static final Character O = 'o';
private static final Character SP = ' ';
private static final Character J = 'J';
private static final Character I = 'i';
private static final Character M = 'm';

    @Benchmark(value=1000000, units = "char", durationResultMultiplier = 1.0/9 )
    public void f() {
        Nodes n = rootNode.walk( 'h' );
        n = n.walk( 'e');
        n = n.walk( 'l');
        n = n.walk( 'l');
        n = n.walk( 'o');
        n = n.walk( ' ');
        n = n.walk( 'J');
        n = n.walk( 'i');
        n = n.walk( 'm');
    }

    @Benchmark(value=1000000, units = "char", durationResultMultiplier = 1.0/9 )
    public void g() {
        Nodes n = rootNode.walk( H );
        n = n.walk( E );
        n = n.walk( L );
        n = n.walk( L );
        n = n.walk( O );
        n = n.walk( SP );
        n = n.walk( J );
        n = n.walk( I );
        n = n.walk( M );
    }

}
