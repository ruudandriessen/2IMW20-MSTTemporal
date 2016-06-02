package computation;

import com.sun.org.apache.xpath.internal.SourceTree;
import model.*;
import transform.TEdge;
import transform.TGraph;
import transform.TVertex;

import java.util.*;

/**
 * Created by ruudandriessen on 25/05/16.
 */
public class MSTwOld extends Algorithm {

    float infinity = Float.POSITIVE_INFINITY;

    @Override
    public void run(AbstractGraph graph) {
        TemporalGraph g;
        if (graph instanceof TemporalGraph) {
            g = (TemporalGraph) graph;
        } else {
            throw new IllegalArgumentException("Cannot use MSTw without temporal graph");
        }

        // Assume interval to be [0, inf)
        ArrayList<Long> vKeys = new ArrayList<>(g.getVertices().keySet());
        Collections.sort(vKeys);
        TemporalVertex root = g.getVertex(vKeys.get(0));

        // Transform graph (page 423)
        System.out.println("Creating transformed graph..");
        TGraph transformed = this.transform(g, root);

        // Create transitive closure
        System.out.println("Creating transative closure..");
        this.createTransitiveClosure(transformed);
        System.out.println(transformed);

        // do algorithm 3 (page 424)

        // do postprocessing (page 424)
    }

    /**
     * Floyd Wharshall algorithm..
     * @param graph
     */
    public void createTransitiveClosure(TGraph graph) {
        // matrix containing all distances
        float[][] dist = new float[graph.getVertices().size()][graph.getVertices().size()];
        float[][] orig = new float[graph.getVertices().size()][graph.getVertices().size()];

        for (int i = 0; i < dist.length; i++) {
            for (int j = 0; j < dist[i].length; j++) {
                dist[i][j] = infinity;
                orig[i][j] = infinity;
            }
            for (TEdge edge : graph.getVertices().get(i).out()) {
                int j = graph.getVertices().indexOf(edge.to());
                if (edge.weight() < dist[i][j]) {
                    dist[i][j] = edge.weight();
                    orig[i][j] = edge.weight();
                }
            }
        }

        for (int k = 0; k < graph.getVertices().size(); k++) {
            for (int i = 0; i < graph.getVertices().size(); i++) {
                for (int j = 0; j < graph.getVertices().size(); j++) {
                    if (dist[i][k] != infinity && dist[k][j] != infinity) {
                        if (dist[i][k] + dist[k][j] < dist[i][j]) {
                            dist[i][j] = dist[i][k] + dist[k][j];
                        }
                    }
                }
            }
        }

        for (int i = 0; i < dist.length; i++) {
            for (int j = 0; j < dist.length; j++) {
                if (dist[i][j] != orig[i][j]) {
                    graph.addEdge(new TEdge(graph.getVertices().get(i),  graph.getVertices().get(j), dist[i][j]));
                }
            }
        }
    }

    public TGraph transform(TemporalGraph graph, TemporalVertex root) {
        TGraph transformed = new TGraph();
        Map<TemporalVertex, List<TVertex>> map = new HashMap<>();
        for (TemporalVertex vertex : graph.getVertices().values()) {
            // Start step 1
            // This is set T(v)
            Set<Float> tv = new TreeSet<>();
            if (vertex == root) {
                tv.add(0f);
            } else {
                for (TemporalEdge e : vertex.in()) {
                    tv.add(e.end());
                }
            }

            // Step 1a
            List<TVertex> vv = new ArrayList<>();
            int count = 1;
            for (float time : tv) {
                TVertex v = new TVertex(vertex.getIdentifier() + "_" + count, vertex.getIdentifier(), time);
                vv.add(v);
                transformed.addVertex(v);
                if (vertex == root) {
                    transformed.root = v;
                }
                count++;
            }
            // Step 1b
            if (vertex != root) {
                TVertex v = new TVertex(Long.toString(vertex.getIdentifier()), vertex.getIdentifier(), infinity);
                vv.add(v);
                transformed.addVertex(v);
            }


            // Start step 2
            // Step 2a
            for (int i = 1; i < vv.size(); i++) {
                TEdge e = new TEdge(vv.get(i - 1), vv.get(i), 0);
                transformed.addEdge(e);
            }

            map.put(vertex, vv);
        }

        for (TemporalEdge edge : graph.edges()) {
            List<TVertex> vertices = map.get(edge.from());
            TVertex from = null;
            for (int i = vertices.size() - 1; i >= 0; i--) {
                if (vertices.get(i).time <= edge.start()) {
                    from = vertices.get(i);
                    break;
                }
            }

            vertices = map.get(edge.to());
            TVertex to = null;
            for (int i = 0; i < vertices.size(); i++) {
                if (vertices.get(i).time == edge.end()) {
                    to = vertices.get(i);
                    break;
                }
            }

            if (from != null && to != null) {
                TEdge e = new TEdge(from, to, edge.weight());
                transformed.addEdge(e);
            }
        }
        return transformed;

    }
}
