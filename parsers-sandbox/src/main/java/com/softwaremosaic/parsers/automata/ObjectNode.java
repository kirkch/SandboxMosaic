package com.softwaremosaic.parsers.automata;

import com.mosaic.collections.ConsList;
import com.mosaic.collections.FastStack;
import com.mosaic.collections.InitialValueMap;
import com.mosaic.collections.KV;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.VoidFunction2;
import com.mosaic.utils.ListUtils;

import java.util.*;


/**
 * A Node within a Finite State Automata.  Transitions between nodes are made
 * by traversing characters.  This particular graph supports cycles, multiple
 * edges to the same node, duplicate key edges and self cycles.  In short it is
 * a full fat character graph.
 */
@SuppressWarnings("unchecked")
public class ObjectNode<T extends Comparable<T>> implements Node<T> {

    private Map<T,List<Node<T>>> edges = new HashMap();

    private boolean isValidEndNode;


    public Nodes append( T label ) {
        List<Node<T>> nextNodes = touchEdges( label );
        Node<T> newNode = new ObjectNode();

        nextNodes.add( newNode );

        return new Nodes( newNode );
    }

    public void append( T label, Nodes<T> nodes ) {
        for ( Node<T> n : nodes ) {
            append( label, n );
        }
    }

    public void append( T label, Node<T> node ) {
        List<Node<T>> nextNodes = touchEdges(label);

        nextNodes.add( node );
    }

    public int replace( T label, Node<T> expectedCurrentNode, Node<T> replacementNode ) {
        List<Node<T>> destinationNodes = edges.get( label );
        if ( destinationNodes == null ) {
            return 0;
        }


        int count = 0;
        for ( int i=0; i<destinationNodes.size(); i++ ) {
            if ( destinationNodes.get(i) == expectedCurrentNode ) {
                destinationNodes.set( i, replacementNode );

                count++;
            }
        }

        return count;
    }

    public int replace( T label, Node<T> expectedCurrentNode, Nodes<T> replacementNodes ) {
        int count = remove( label, expectedCurrentNode );

        if ( count > 0 ) {
            append( label, replacementNodes );
        }

        return count;
    }

    public int remove( T label, Node<T> targetNode ) {
        List<Node<T>> destinationNodes = edges.get( label );
        if ( destinationNodes == null ) {
            return 0;
        }

        destinationNodes.remove( targetNode );

        return 1;
    }

    public Nodes<T> walk( T label ) {
        List<Node<T>> nextNodes = edges.get( label );

        if ( nextNodes == null ) {
            return Nodes.EMPTY;
        }

        return new Nodes(nextNodes);
    }

    public boolean isTerminal() {
        return edges.isEmpty();
    }

    public boolean isValidEndNode() {
        return isValidEndNode;
    }

    public void isValidEndNode( boolean flag ) {
        this.isValidEndNode = flag;
    }

    public Nodes<T> walk( T...path ) {
        return this.walk( Arrays.asList(path) );
    }

    public Nodes<T> walk( Iterable<T> path ) {
        List<Node<T>> currentNodes = Arrays.asList( (Node<T>) this );

        for ( final T label : path ) {
            List<Nodes<T>> nextNodes = ListUtils.map(currentNodes, new Function1<Node<T>,Nodes<T>>() {
                public Nodes<T> invoke( Node<T> n ) {
                    return n.walk( label );
                }
            });

            currentNodes = ListUtils.flatten( nextNodes );
        }

        return new Nodes(currentNodes);
    }


    public List<KV<T,Node<T>>> getOutEdges() {
        List<KV<T,Node<T>>> out = new ArrayList<>();

        for ( Map.Entry<T,List<Node<T>>> e : edges.entrySet() ) {
            for ( Node<T> n : e.getValue() ) {
                out.add( new KV(e.getKey(), n) );
            }
        }

        return out;
    }

    public Iterator<KV<T,Node<T>>> iterator() {
        return getOutEdges().iterator();
    }


    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append( "Node(" );
        buf.append( edges.keySet() );
        buf.append( ')' );

        return buf.toString();
    }

    private List<Node<T>> touchEdges( T label ) {
        List<Node<T>> nodes = edges.get( label );
        if ( nodes == null ) {
            nodes = new ArrayList<>();

            edges.put( label, nodes );
        }

        return nodes;
    }

    public Nodes<T> getOutNodes() {
        List<Node<T>> outNodes = new ArrayList<>();

        for ( List<Node<T>> nodes : edges.values() ) {
            outNodes.addAll( nodes );
        }

        return new Nodes(outNodes);
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
    public void depthFirstPrefixTraversal( VoidFunction2<ConsList<KV<Set<T>,Node<T>>>, Boolean> callbackFunction ) {
        DepthFirstTraverser<T> t = new DepthFirstTraverser(this);

        for ( Path path : t ) {
            callbackFunction.invoke( path.edges, path.isEndOfPath );
        }
    }


    private static class Path<T extends Comparable<T>> {

        public static <T extends Comparable<T>> Path createPath( Path pathUpToNode, Set<T> labels, Node<T> destinationNode, Set<Node<T>> alreadyVisitedNodes ) {
            boolean isEndOfPath = destinationNode.isTerminal() || alreadyVisitedNodes.contains(destinationNode);

            return new Path( pathUpToNode, labels, destinationNode, isEndOfPath );
        }

        private ConsList<KV<Set<T>,Node<T>>> edges;
        private boolean                isEndOfPath;

        public Path( ConsList<KV<Set<T>, Node<T>>> edges, boolean isEndOfPath ) {
            this.edges       = edges;
            this.isEndOfPath = isEndOfPath;
        }

        public Path( Path pathUpToNode, Set<T> labels, Node<T> destinationNode, boolean isEndOfPath ) {
            assert !pathUpToNode.isEndOfPath : "cannot extend a path that is already marked as complete";

            KV<Set<T>,Node<T>> newEdge = new KV( labels, destinationNode );

            this.edges       = pathUpToNode.edges.cons(newEdge);
            this.isEndOfPath = isEndOfPath;
        }

        public Node getDestinationNode() {
            return edges.head().getValue();
        }
    }

    private static class DepthFirstTraverser<T extends Comparable<T>> implements Iterable<Path<T>> {


        public static <T extends Comparable<T>> Map<Node<T>,Set<T>> getDestinationsGroupedByCharacters( Node<T> n ) {
            Map<Node<T>,Set<T>> map = InitialValueMap.identityMapOfSets();

            for ( KV<T,Node<T>> e : n.getOutEdges() ) {
                Set<T> charactersSoFar = map.get(e.getValue());

                charactersSoFar.add( e.getKey() );
            }

            return map;
        }


        private Set<Node<T>>       nodesVisitedSoFar = new HashSet();
        private FastStack<Path<T>> pathsYetToVisit   = new FastStack();


        public DepthFirstTraverser( Node startingNode ) {
            Path path = new Path(
                    ConsList.newConsList(new KV<Set<T>, Node<T>>(Collections.EMPTY_SET, startingNode)),
                    startingNode.isTerminal()
            );

            pathsYetToVisit.push(path);
        }

        public Iterator<Path<T>> iterator() {
            return new Iterator<Path<T>>() {
                public boolean hasNext() {
                    return !pathsYetToVisit.isEmpty();
                }

                public Path<T> next() {
                    Path pathSoFar = pathsYetToVisit.pop();

                    scheduleOutEdgesFrom( pathSoFar );

                    return pathSoFar;
                }

                private void scheduleOutEdgesFrom( Path<T> pathUpToNode ) {
                    Node<T> nextNode       = pathUpToNode.getDestinationNode();
                    boolean alreadyVisited = !nodesVisitedSoFar.add(nextNode);
                    if ( alreadyVisited ) {
                        return;
                    }


                    Map<Node<T>,Set<T>> edges = getDestinationsGroupedByCharacters(nextNode);

                    for ( Map.Entry<Node<T>,Set<T>> e : sortEdges(edges) ) {
//                    for ( KV<T,Node<T>> edge : nextNode ) {
                        pathsYetToVisit.push( Path.createPath( pathUpToNode, e.getValue(), e.getKey(), nodesVisitedSoFar ));
                    }
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }

                private List<Map.Entry<Node<T>,Set<T>>> sortEdges( Map<Node<T>,Set<T>> edges ) {
//                    return ListUtils.asList(edges.entrySet());

                    List<Map.Entry<Node<T>, Set<T>>> entries = ListUtils.asList(edges.entrySet());

                    Collections.sort( entries, new Comparator<Map.Entry<Node<T>, Set<T>>>() {
                        public int compare( Map.Entry<Node<T>, Set<T>> a, Map.Entry<Node<T>, Set<T>> b ) {
                            return b.getValue().iterator().next().compareTo(a.getValue().iterator().next());
                        }
                    });

                    return entries;
                }

            };
        }

    }

}
