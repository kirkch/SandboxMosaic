package com.softwaremosaic.parsers.trie;

import com.mosaic.collections.ConsList;
import com.mosaic.collections.KV;
import com.mosaic.io.CharPredicate;
import com.mosaic.lang.Validate;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.Function2;
import com.mosaic.lang.functional.VoidFunction2;
import com.mosaic.utils.ListUtils;
import com.mosaic.utils.StringUtils;
import com.softwaremosaic.parsers.trie.regexp.CharPredicates;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Encodes a graph of nodes into lines of text.
 *
 * Format:
 *
 * 'nodeNumber -(chars)-> nodeNumber[t]
 *
 * The nodes are numbered from 1, in the order that they are traversed.  The
 * traversal order is depth first, in ascending order of the characters on each
 * edge.
 */
@SuppressWarnings("unchecked")
public class CharacterNodeFormatter<T> {

    public static interface NodeFormatPlugin<T> {
        public String getNodeLabelFor( long nodeId, CharacterNode<T> node );
    }

    private static final NodeFormatPlugin DEFAULT_PLUGIN = new NodeFormatPlugin() {
        public String getNodeLabelFor( long nodeId, CharacterNode node ) {
            return Long.toString(nodeId);
        }
    };



    public List<String> format( CharacterNode<T> startingNode ) {
        return format( startingNode, DEFAULT_PLUGIN );
    }

    public List<String> format( CharacterNode <T> startingNode, final NodeFormatPlugin plugin  ) {
        Validate.notNull( startingNode, "startingNode" );

        final List<String> formattedGraph = new ArrayList();

        startingNode.depthFirstPrefixTraversal(
            new VoidFunction2<ConsList<KV<Set<CharPredicate>, CharacterNode<T>>>,Boolean>() {
                public void invoke( ConsList<KV<Set<CharPredicate>, CharacterNode<T>>> path, Boolean isCompletePath ) {
                    if ( isCompletePath ) {
                        appendPath( path.reverse(), formattedGraph, plugin );
                    }
                }
            }
        );

        return blankOutRepeatedPrefixes( formattedGraph );
    }

    private List<String> blankOutRepeatedPrefixes( List<String> formattedGraph ) {
        return ListUtils.map( formattedGraph, new Function1<String, String>() {
            private String previousLine = null;

            public String invoke( String currentLine ) {
                String modifiedLine = blankOutCommonPrefix( previousLine, currentLine );

                previousLine = currentLine;

                return modifiedLine;
            }

            private String blankOutCommonPrefix( String a, String b ) {
                if ( a == null ) {
                    return b;
                }

                int toExc = commonPrefixUpToEdge( a, b );
                if ( toExc == 0 ) {
                    return b;
                }

                int endOfLabelIndex = b.indexOf( ':' ) + 1;
                if ( toExc < endOfLabelIndex ) {  // prevent truncation of similar labels
                    return b;
                }

                StringBuilder buf = new StringBuilder( b.length() );
                StringUtils.repeat( buf, toExc, ' ' );
                buf.append( b.substring( toExc ) );

                return buf.toString();
            }

            /**
             * Given two strings, compares them in order and identifies the index of
             * the first char that does not match.  It then back tracks to ' -',
             * which marks the last edge transition in the string.
             */
            private int commonPrefixUpToEdge( String a, String b ) {
                int sharedPrefixEndExc = StringUtils.endIndexExcOfCommonPrefix( a, b );

                int i = sharedPrefixEndExc;
                while ( i > 0 ) {
                    if ( b.charAt( i ) == '-' && b.charAt( i - 1 ) == ' ' ) {
                        return i;
                    }

                    i--;
                }

                return sharedPrefixEndExc;
            }
        } );
    }


    private Function2<CharacterNode<T>,NodeFormatPlugin,String> nodeLabeler = new Function2<CharacterNode<T>, NodeFormatPlugin, String>() {
        private Map<CharacterNode<T>,String> existingLabels = new IdentityHashMap<>();
        private long             nextLabel      = 1;

        public String invoke( CharacterNode<T> node, NodeFormatPlugin plugin ) {
            String label = existingLabels.get(node);
            if ( label == null ) {
                label = plugin.getNodeLabelFor(nextLabel++, node);

//                if ( node.isValidEndNode() ) {
//                    label += "e";
//                }

                existingLabels.put( node, label );
            }

            return label;
        }
    };

    private void appendPath( ConsList<KV<Set<CharPredicate>, CharacterNode<T>>> path, List<String> formattedGraph, NodeFormatPlugin plugin ) {
        StringBuilder buf = new StringBuilder();

        for ( KV<Set<CharPredicate>,CharacterNode<T>> step : path ) {
            Set<CharPredicate>  labels = step.getKey();
            CharacterNode<T>    node   = step.getValue();

            if ( !labels.isEmpty() ) {
                buf.append( " -" );

                appendLabels( buf, labels );

                buf.append( "-> " );
            }

            buf.append(nodeLabeler.invoke(node, plugin));
        }

        formattedGraph.add( buf.toString() );
    }

    private void appendLabels( StringBuilder buf, Set<CharPredicate> predicates ) {
        if ( predicates.size() == 1 ) {
            buf.append( predicates.iterator().next() );
        } else {
            buf.append( CharPredicates.orPredicates(predicates) );
        }
    }

}
