package com.mosaic.parser.graph;

import com.mosaic.collections.ConsList;
import com.mosaic.collections.KV;
import com.mosaic.lang.CharacterPredicate;
import com.mosaic.lang.Validate;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.Function2;
import com.mosaic.lang.functional.VoidFunction2;
import com.mosaic.utils.ListUtils;
import com.mosaic.utils.StringUtils;
import com.mosaic.lang.CharacterPredicates;

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
public class NodeFormatter {

    public static interface NodeFormatPlugin {
        public String getNodeLabelFor( long nodeId, Node node );
    }

    private static final NodeFormatPlugin DEFAULT_PLUGIN = new NodeFormatPlugin() {
        public String getNodeLabelFor( long nodeId, Node node ) {
            return Long.toString(nodeId);
        }
    };

    private static final NodeFormatPlugin DETAILED_PLUGIN = new NodeFormatPlugin() {
        public String getNodeLabelFor( long nodeId, Node node ) {
            StringBuilder buf = new StringBuilder();

            buf.append( nodeId );

            if ( node.isEndNode() ) {
                buf.append( 'e' );
            }

            ParserFrameOp op = node.getActions();
            if ( op != null ) {
                buf.append( ':' );

                StringBuilder buf2 = new StringBuilder();
                op.appendOpCodesTo(buf2);

                String ops = buf2.toString();
                if ( ops.contains(",") ) {
                    buf.append( '(' );
                    buf.append( ops );
                    buf.append( ')' );
                } else {
                    buf.append( ops );
                }
            }

            return buf.toString();
        }
    };

    private static final NodeFormatPlugin DEBUG_PLUGIN = new NodeFormatPlugin() {
        public String getNodeLabelFor( long nodeId, Node node ) {
            StringBuilder buf = new StringBuilder();

//            buf.append( System.identityHashCode(node) );
            buf.append( nodeId );

            if ( node.isEndNode() ) {
                buf.append( 'e' );
            }

            ParserFrameOp op = node.getActions();
            if ( op != null ) {
                buf.append( ':' );

                StringBuilder buf2 = new StringBuilder();
                op.appendOpCodesTo(buf2);

                String ops = buf2.toString();
                if ( ops.contains(",") ) {
                    buf.append( '(' );
                    buf.append( ops );
                    buf.append( ')' );
                } else {
                    buf.append( ops );
                }
            }

            return buf.toString();
        }
    };


    public static final NodeFormatter DEFAULT_FORMATTER  = new NodeFormatter(DEFAULT_PLUGIN);
    public static final NodeFormatter DETAILED_FORMATTER = new NodeFormatter(DETAILED_PLUGIN);
    public static final NodeFormatter DEBUG_FORMATTER    = new NodeFormatter(DEBUG_PLUGIN);




    private NodeFormatPlugin defaultPlugin;

    public NodeFormatter() {
        this( DEFAULT_PLUGIN );
    }

    public NodeFormatter( NodeFormatPlugin defaultPlugin ) {
        Validate.notNull( defaultPlugin, "defaultPlugin" );

        this.defaultPlugin = defaultPlugin;
    }

    public List<String> format( Node startingNode ) {
        return format( startingNode, defaultPlugin );
    }

    public List<String> format( Node startingNode, final NodeFormatPlugin plugin  ) {
        Validate.notNull( startingNode, "startingNode" );

        final List<String> formattedGraph = new ArrayList();
        final NodeLabeler  nodeLabeler    = new NodeLabeler();

        startingNode.depthFirstPrefixTraversal(
            new VoidFunction2<ConsList<KV<Set<CharacterPredicate>, Node>>,Boolean>() {
                public void invoke( ConsList<KV<Set<CharacterPredicate>, Node>> path, Boolean isCompletePath ) {
                    if ( isCompletePath ) {
                        appendPath( nodeLabeler, path.reverse(), formattedGraph, plugin );
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


    public class NodeLabeler implements Function2<Node, NodeFormatPlugin, String> {
        private Map<Node,String> existingLabels = new IdentityHashMap<>();
        private long             nextLabel      = 1;

        public String invoke( Node node, NodeFormatPlugin plugin ) {
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

    private void appendPath( NodeLabeler nodeLabeler, ConsList<KV<Set<CharacterPredicate>, Node>> path, List<String> formattedGraph, NodeFormatPlugin plugin ) {
        StringBuilder buf = new StringBuilder();

        for ( KV<Set<CharacterPredicate>,Node> step : path ) {
            Set<CharacterPredicate>  labels = step.getKey();
            Node node   = step.getValue();

            if ( !labels.isEmpty() ) {
                buf.append( " -" );

                appendLabels( buf, labels );

                buf.append( "-> " );
            }

            buf.append(nodeLabeler.invoke(node, plugin));
        }

        formattedGraph.add( buf.toString() );
    }

    private void appendLabels( StringBuilder buf, Set<CharacterPredicate> predicates ) {
        if ( predicates.size() == 1 ) {
            buf.append( predicates.iterator().next() );
        } else {
            buf.append( CharacterPredicates.orPredicates( predicates ) );
        }
    }

}
