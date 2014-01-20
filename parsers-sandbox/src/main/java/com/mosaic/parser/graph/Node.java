package com.mosaic.parser.graph;

import com.mosaic.collections.ConsList;
import com.mosaic.collections.FastStack;
import com.mosaic.collections.InitialValueMap;
import com.mosaic.collections.KV;
import com.mosaic.lang.CharacterPredicate;
import com.mosaic.lang.CharacterPredicates;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.Pair;
import com.mosaic.lang.functional.VoidFunction2;
import com.mosaic.utils.ListUtils;
import com.mosaic.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Represents a graph used to present a finite state machine for parsing characters.
 * The nodes represent state, and the edges are predicates that match characters.
 * Walking the graph involves matching the predicates that represent that edge.
 *
 * The type parameter of the node is the type of the actions stored within the
 * node.  The actions can be anything that you like.
 */
@SuppressWarnings("unchecked")
public class Node implements Iterable<Pair<CharacterPredicate,Node>> {

    private ParserFrameOp actions;

    private List<Pair<CharacterPredicate,Node>> edges = new ArrayList();

    private boolean isEndNode;


    public boolean isEndNode() {
        return isEndNode;
    }

    public void isEndNode( boolean endNode ) {
        isEndNode = endNode;
    }

    /**
     * Custom data stored with this node.
     */
    public ParserFrameOp getActions() {
        return actions;
    }

    /**
     * Custom data stored with this node.
     */
    public void setActions( ParserFrameOp actions ) {
        this.actions = actions;
    }


    /**
     * Retrieves the nodes that can be transitioned to by consuming the specified
     * character.  If there are no edges, then an empty list will be returned.
     */
    public Nodes fetch( char c ) {
        List<Node> nextNodes = new ArrayList(edges.size());

        return fetchInto( nextNodes, c );
    }

    Nodes fetchInto( List<Node> nextNodes, char c ) {
        for ( Pair<CharacterPredicate,Node> edge : edges ) {
            if ( edge.getFirst().matches(c) ) {
                nextNodes.add( edge.getSecond() );
            }
        }

        return new Nodes(nextNodes);
    }


    public boolean hasOutEdges() {
        return !edges.isEmpty();
    }

    public List<Pair<CharacterPredicate,Node>> getOutEdges() {
        return Collections.unmodifiableList( edges );
    }

    public List<CharacterPredicate> getOutPredicates() {
        return ListUtils.map(edges, new Function1<Pair<CharacterPredicate, Node>, CharacterPredicate>() {
            public CharacterPredicate invoke( Pair<CharacterPredicate, Node> edge ) {
                return edge.getFirst();
            }
        });
    }

    public Nodes getOutNodes() {
        List outNodes = ListUtils.map(edges, new Function1<Pair<CharacterPredicate, Node>, Node>() {
            public Node invoke( Pair<CharacterPredicate, Node> edge ) {
                return edge.getSecond();
            }
        });

        return new Nodes(outNodes);
    }



    public void append( char c, Node node ) {
        this.append( CharacterPredicates.constant( c ), node );
    }

    public void append( CharacterPredicate predicate, Node node ) {
        edges.add( new Pair(predicate,node) );
    }

    public void append( CharacterPredicate predicate, Nodes nodes ) {
        for ( Node n : nodes ) {
            append( predicate, n );
        }
    }

    public void append( char c, Nodes nodes ) {
        append( CharacterPredicates.constant( c ), nodes );
    }

    public Nodes append( CharacterPredicate predicate ) {
        Node newNode = new Node();

        append( predicate, newNode );

        return new Nodes( newNode );
    }

    public Nodes append( char c ) {
        return append( CharacterPredicates.constant( c ) );
    }



    /**
     * Scan this nodes out edges for the transition with character c to a specified
     * destination node, then replace the node with a replacement.
     */
    public int replace( CharacterPredicate predicate, Node expectedCurrentNode, Node replacementNode ) {
        int count = 0;

        for ( int i=0; i<edges.size(); i++ ) {
            Pair<CharacterPredicate,Node> edge = edges.get(i);

            if ( edge.getSecond() == expectedCurrentNode && edge.getFirst() == predicate ) {
                edges.set( i, new Pair(edge.getFirst(),replacementNode) );

                count++;
            }
        }

        return count;
    }

    public int replace( CharacterPredicate predicate, Node expectedCurrentNode, Nodes replacementNodes ) {
        int count = remove( predicate, expectedCurrentNode );

        if ( count > 0 ) {
            append( predicate, replacementNodes );
        }

        return count;
    }

    public int remove( CharacterPredicate predicate, Node targetNode ) {
        int count = 0;

        Iterator<Pair<CharacterPredicate,Node>> it = edges.iterator();
        while ( it.hasNext() ) {
            Pair<CharacterPredicate,Node> edge = it.next();

            if ( edge.getSecond() == targetNode && edge.getFirst() == predicate ) {
                it.remove();

                count++;
            }
        }

        return count;
    }

    public Iterator<Pair<CharacterPredicate,Node>> iterator() {
        return edges.iterator();
    }


    /**
     * Walks the graph calling the specified callbackFunction for each node past,
     * as they are past.  The traversal will take the edges sorted by the traversal
     * characters (lowest first) and grouped so that a traversal to the same node will
     * not be repeated.<p/>
     *
     * The call to the callback function supplies the path walked so far and
     * a boolean which will be true if the path has reached the last node in the
     * traversal before it backtracks to another path.<p/>
     *
     * The first path will have an empty set of characters, then for each traversal
     * taken a set of characters for that edge (or edges if more than one) that
     * lead to the next node.
     */
    public void depthFirstPrefixTraversal( VoidFunction2<ConsList<KV<Set<CharacterPredicate>,Node>>, Boolean> callbackFunction ) {
        DepthFirstTraverser t = new DepthFirstTraverser(this);

        for ( Path path : t ) {
            callbackFunction.invoke( path.edges, path.isEndOfPath );
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();


        if (isEndNode()) {
            buf.append("EndNode(");
        } else {
            buf.append("Node(");
        }

        buf.append( "outEdges='" );
        StringUtils.join( buf, getOutPredicates(), "|" );
        buf.append( "')" );

        return buf.toString();
    }


    private static class Path {

        public static Path createPath( Path pathUpToNode, Set<CharacterPredicate> predicates, Node destinationNode, Set<Node> alreadyVisitedNodes ) {
            boolean isEndOfPath = !destinationNode.hasOutEdges() || alreadyVisitedNodes.contains(destinationNode);

            return new Path( pathUpToNode, predicates, destinationNode, isEndOfPath );
        }

        private ConsList<KV<Set<CharacterPredicate>,Node>> edges;
        private boolean                             isEndOfPath;

        public Path( ConsList<KV<Set<CharacterPredicate>, Node>> edges, boolean isEndOfPath ) {
            this.edges       = edges;
            this.isEndOfPath = isEndOfPath;
        }

        public Path( Path pathUpToNode, Set<CharacterPredicate> labels, Node destinationNode, boolean isEndOfPath ) {
            KV<Set<CharacterPredicate>,Node> newEdge = new KV( labels, destinationNode );

            this.edges       = pathUpToNode.edges.cons(newEdge);
            this.isEndOfPath = isEndOfPath;
        }

        public Node getDestinationNode() {
            return edges.head().getValue();
        }
    }

    private static class DepthFirstTraverser implements Iterable<Path> {

        public static Map<Node,Set<CharacterPredicate>> getDestinationsGroupedByCharacters( Node n ) {
            Map<Node,Set<CharacterPredicate>> map = InitialValueMap.identityMapOfSets();

            for ( Pair<CharacterPredicate,Node> e : n.getOutEdges() ) {
                Set<CharacterPredicate> labelsSoFar = map.get(e.getSecond());

                labelsSoFar.add( e.getFirst() );
            }

            return map;
        }


        private Set<Node> nodesVisitedSoFar = new HashSet();
        private FastStack<Path>    pathsYetToVisit   = new FastStack();


        public DepthFirstTraverser( Node startingNode ) {
            Path path = new Path(
                ConsList.newConsList(new KV<Set<CharacterPredicate>, Node>( Collections.EMPTY_SET, startingNode)),
                !startingNode.hasOutEdges()
            );

            pathsYetToVisit.push(path);
        }

        public Iterator<Path> iterator() {
            return new Iterator<Path>() {
                public boolean hasNext() {
                    return !pathsYetToVisit.isEmpty();
                }

                public Path next() {
                    Path pathSoFar = pathsYetToVisit.pop();

                    scheduleOutEdgesFrom( pathSoFar );

                    return pathSoFar;
                }

                private void scheduleOutEdgesFrom( Path pathUpToNode ) {
                    Node nextNode       = pathUpToNode.getDestinationNode();
                    boolean alreadyVisited = !nodesVisitedSoFar.add(nextNode);
                    if ( alreadyVisited ) {
                        return;
                    }


                    Map<Node,Set<CharacterPredicate>> edges = getDestinationsGroupedByCharacters(nextNode);

                    for ( Map.Entry<Node,Set<CharacterPredicate>> e : sortEdges(edges) ) {
                        pathsYetToVisit.push( Path.createPath( pathUpToNode, e.getValue(), e.getKey(), nodesVisitedSoFar ));
                    }
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }

                private List<Map.Entry<Node,Set<CharacterPredicate>>> sortEdges( Map<Node,Set<CharacterPredicate>> edges ) {
                    List<Map.Entry<Node, Set<CharacterPredicate>>> entries = ListUtils.asList( edges.entrySet() );

                    Collections.sort( entries, new Comparator<Map.Entry<Node, Set<CharacterPredicate>>>() {
                        public int compare( Map.Entry<Node, Set<CharacterPredicate>> a, Map.Entry<Node, Set<CharacterPredicate>> b ) {
                            return b.getValue().iterator().next().compareTo(a.getValue().iterator().next());
                        }
                    });

                    return entries;
                }

            };
        }
    }

}
