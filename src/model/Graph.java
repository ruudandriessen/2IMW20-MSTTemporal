package model;

/**
 * Created by ruudandriessen on 11/05/16.
 */
public class Graph extends AbstractGraph {

    /**
     * Adds a new vertex in the graph
     * @param identifier The identifier of the vertex
     * @return The vertex instance
     */
    public Vertex addVertex(long identifier) {
        return (Vertex) vertices.put(identifier, new Vertex(identifier));
    }

    /**
     * Gets the vertex instance based on an identifier
     * @param identifier The identifier of the vertex
     * @return The vertex instance if it is contained, null otherwise
     */
    public AbstractVertex getVertex(long identifier) {
        return vertices.get(identifier);
    }
}