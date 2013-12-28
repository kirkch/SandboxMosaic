package com.softwaremosaic.parsers.automata;

import com.mosaic.collections.ConsList;
import com.mosaic.collections.KV;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.VoidFunction2;
import com.mosaic.utils.ListUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;


/**
*
*/
@SuppressWarnings("unchecked")
public class Node_TraversalTests {

    private Node<Character>           startingNode     = new LabelNode();
    private TraversalAuditingCallback auditingCallback = new TraversalAuditingCallback();

    
    @Test
    public void givenAnEmptyNode_traverse_expectOnlyOneCallback() {
        startingNode.depthFirstPrefixTraversal( auditingCallback );

        assertEquals( Arrays.asList("[] [leaf]"), auditingCallback.callbacks );
    }

    @Test
    public void givenTwoNodesConnectedInSerial_traverse_expectTwoCallbacks() {
        startingNode.append('a');

        startingNode.depthFirstPrefixTraversal( auditingCallback );

        List<String> expectedAudit = Arrays.asList(
                "[]",
                "[] -> [a] [leaf]"
        );

        assertEquals(expectedAudit, auditingCallback.callbacks );
    }

    @Test
    public void givenThreeNodesConnectedInSerial_traverse_expectThreeCallbacks() {
        Nodes n2 = startingNode.append('a');
        n2.append('b');

        startingNode.depthFirstPrefixTraversal( auditingCallback );

        List<String> expectedAudit = Arrays.asList(
                "[]",
                "[] -> [a]",
                "[] -> [a] -> [b] [leaf]"
        );

        assertEquals(expectedAudit, auditingCallback.callbacks );
    }

    @Test
    public void givenTwoNodesConnectedInSerialTwice_traverse_expectTwoCallbacks() {
        Nodes n2 = startingNode.append('a');
        startingNode.append('b', n2);

        startingNode.depthFirstPrefixTraversal( auditingCallback );

        List<String> expectedAudit = Arrays.asList(
                "[]",
                "[] -> [a, b] [leaf]"
        );

        assertEquals(expectedAudit, auditingCallback.callbacks );
    }

    @Test
    public void givenTwoNodesConnectedThreeTimes_traverse_expectTwoCallbacks() {
        Nodes n2 = startingNode.append('a');
        startingNode.append('c', n2);
        startingNode.append('b', n2);

        startingNode.depthFirstPrefixTraversal( auditingCallback );

        List<String> expectedAudit = Arrays.asList(
                "[]",
                "[] -> [a, b, c] [leaf]"
        );

        assertEquals(expectedAudit, auditingCallback.callbacks );
    }

    @Test
    public void givenTwoNodesConnectedInParallel_traverse_expectThreeCallbacks() {
        startingNode.append('a');
        startingNode.append('b');

        startingNode.depthFirstPrefixTraversal( auditingCallback );

        List<String> expectedAudit = Arrays.asList(
                "[]",
                "[] -> [a] [leaf]",
                "[] -> [b] [leaf]"
        );

        assertEquals(expectedAudit, auditingCallback.callbacks );
    }

    @Test
    public void givenNodeThatPointsToItself_traverse_expectTwoCallbacks() {
        startingNode.append('a', startingNode);

        startingNode.depthFirstPrefixTraversal( auditingCallback );

        List<String> expectedAudit = Arrays.asList(
                "[]",
                "[] -> [a] [leaf]"
        );

        assertEquals(expectedAudit, auditingCallback.callbacks );
    }

    @Test
    public void givenTwoNodesThatCreateACycle_traverse_expectThreeCallbacks() {
        Nodes n2 = startingNode.append('a');
        n2.append('b', startingNode);

        startingNode.depthFirstPrefixTraversal( auditingCallback );

        List<String> expectedAudit = Arrays.asList(
                "[]",
                "[] -> [a]",
                "[] -> [a] -> [b] [leaf]"
        );

        assertEquals(expectedAudit, auditingCallback.callbacks );
    }



    @SuppressWarnings("unchecked")
    private class TraversalAuditingCallback implements VoidFunction2<ConsList<KV<Set<Label<Character>>,Node<Character>>>,Boolean> {
        public List<String> callbacks = new ArrayList();

        public void invoke( ConsList<KV<Set<Label<Character>>, Node<Character>>> traversedPath, Boolean isLeaf ) {
            ConsList<String> nodesVisitedStr = traversedPath.map(new Function1<KV<Set<Label<Character>>, Node<Character>>, String>() {
                public String invoke(KV<Set<Label<Character>>, Node<Character>> edge) {
                    List labels = ListUtils.asList(edge.getKey());
                    Collections.sort(labels);

                    return labels.toString();
                }
            });

            String pathStr = nodesVisitedStr.reverse().join(" -> ");

            if ( isLeaf ) {
                pathStr += " [leaf]";
            }

            callbacks.add( pathStr );
        }
    }

}
