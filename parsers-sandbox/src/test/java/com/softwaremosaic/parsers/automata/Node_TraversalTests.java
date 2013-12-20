package com.softwaremosaic.parsers.automata;

import com.mosaic.collections.ConsList;
import com.mosaic.collections.KV;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.VoidFunction1;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class Node_TraversalTests {

    private Node                      startingNode     = new Node("s");
    private TraversalAuditingCallback auditingCallback = new TraversalAuditingCallback();

    @Test
    public void givenAnEmptyNode_traverse_expectOnlyOneCallback() {
        startingNode.depthFirstPrefixTraversal( auditingCallback );

        assertEquals( Arrays.asList("([], s)"), auditingCallback.callbacks );
    }

    @Test
    public void givenTwoNodesConnectedInSerial_traverse_expectTwoCallbacks() {
        startingNode.appendCharacter("n2", 'a');

        startingNode.depthFirstPrefixTraversal( auditingCallback );

        List<String> expectedAudit = Arrays.asList(
                "([], s)",
                "([], s) -> ([a], n2)"
        );

        assertEquals(expectedAudit, auditingCallback.callbacks );
    }

    @Test
    public void givenThreeNodesConnectedInSerial_traverse_expectThreeCallbacks() {
        Nodes n2 = startingNode.appendCharacter("n2", 'a');
        n2.appendCharacter("n3", 'b');

        startingNode.depthFirstPrefixTraversal( auditingCallback );

        List<String> expectedAudit = Arrays.asList(
                "([], s)",
                "([], s) -> ([a], n2)",
                "([], s) -> ([a], n2) -> ([b], n3)"
        );

        assertEquals(expectedAudit, auditingCallback.callbacks );
    }

    @Test
    public void givenTwoNodesConnectedInSerialTwice_traverse_expectTwoCallbacks() {
        Nodes n2 = startingNode.appendCharacter("n2", 'a');
        startingNode.appendEdge('b', n2);

        startingNode.depthFirstPrefixTraversal( auditingCallback );

        List<String> expectedAudit = Arrays.asList(
                "([], s)",
                "([], s) -> ([a, b], n2)"
        );

        assertEquals(expectedAudit, auditingCallback.callbacks );
    }

    @Test
    public void givenTwoNodesConnectedThreeTimes_traverse_expectTwoCallbacks() {
        Nodes n2 = startingNode.appendCharacter("n2", 'a');
        startingNode.appendEdge('c', n2);
        startingNode.appendEdge('b', n2);

        startingNode.depthFirstPrefixTraversal( auditingCallback );

        List<String> expectedAudit = Arrays.asList(
                "([], s)",
                "([], s) -> ([a, b, c], n2)"
        );

        assertEquals(expectedAudit, auditingCallback.callbacks );
    }

    @Test
    public void givenTwoNodesConnectedInParallel_traverse_expectThreeCallbacks() {
        startingNode.appendCharacter("n2", 'a');
        startingNode.appendCharacter("n3", 'b');

        startingNode.depthFirstPrefixTraversal( auditingCallback );

        List<String> expectedAudit = Arrays.asList(
                "([], s)",
                "([], s) -> ([a], n2)",
                "([], s) -> ([b], n3)"
        );

        assertEquals(expectedAudit, auditingCallback.callbacks );
    }



    @SuppressWarnings("unchecked")
    private class TraversalAuditingCallback implements VoidFunction1<ConsList<KV<Set<Character>,Node>>> {
        public List<String> callbacks = new ArrayList();

        public void invoke( ConsList<KV<Set<Character>, Node>> traversedPath ) {
            ConsList<String> nodesVisitedStr = traversedPath.map(new Function1<KV<Set<Character>, Node>, String>() {
                public String invoke(KV<Set<Character>, Node> edge) {
                    return "(" + edge.getKey() + ", " + edge.getValue().getLabel() + ")";
                }
            });

            String pathStr = nodesVisitedStr.reverse().join(" -> ");

            callbacks.add( pathStr );
        }
    }

}
