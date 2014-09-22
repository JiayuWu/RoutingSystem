package project1;

import org.rollerjm.graph.AdjacencyMatrixGraph;
import org.rollerjm.graph.IGraph;
import org.rollerjm.graph.Path;

public class RouteGraph {

    public RouteGraph(int numberVertices, int numberTimeIntervals, Node[] nodes) {
        graph = new AdjacencyMatrixGraph(numberVertices, numberTimeIntervals);
        for (int i = 0; i < numberVertices; i++) {
            //graph.addVertex( String.valueOf(i+1) );
            graph.addVertex(nodes[i].nodeID);
        }
    }

    public RouteGraph(IGraph graph) {
        this.graph = graph;
    }

    public IGraph graph;

    public void addPath(String start, String end, int weight, int stindex) {
        graph.addEdge(start, end, weight, stindex);
    }

    public int getEdgeWeight(Path path, int stIndex) {
        return graph.getEdgeWeight(path, stIndex);
    }

    public int getEdgeWeight(String startVertex, int stimeindex, String destinationVertex) {
        return graph.getEdgeWeight(startVertex, stimeindex, destinationVertex);
    }
}
