package org.rollerjm.graph;

import java.util.Iterator;

/**
 * <p>Title: graphs</p>
 * <p>Description: the Interface which specifies a weighted oriented graph.
 *  All search algorithms and client classes use that Interface to deal with
 *   graphs. If the implementation changes, no change will be needed in the
 *   clients classes (that's the advantages of using Interfaces !).
 * For doc, see the AdjacencyMatrixGraph class
 * </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Jean-Michel Garnier
 * @version 1.0
 */

public interface IGraph {
    /**
     *
     * @return
     */
    public int getVerticesNumber();

    /**
     *
     * @param vertex
     */
    public void addVertex(Object vertex);

    /**
     *
     * @param startVertex
     * @param destinationVertex
     * @param weight
     */
    public void addEdge(Object startVertex, Object destinationVertex, int weight, int stIndex);

    /**
     *
     * @param startVertex
     * @param destinationVertex
     */
    public void removeEdge(Object startVertex, Object destinationVertex, int stIndex);

    /**
     *
     * @param startVertex
     * @param destinationVertex
     * @return boolean
     */
    public boolean edgeExist(Object startVertex, Object destinationVertex, int stIndex);

    /**
     *
     * @param vertex
     * @return boolean
     */
    public boolean vertexExist(Object vertex);

    /**
     *
     * @param startVertex
     * @param destinationVertex
     * @return int
     */
    public int getEdgeWeight(Object startVertex, int stimeindex, Object destinationVertex);

    /**
     *
     * @param path
     * @return int
     */
    public int getEdgeWeight(Path path, int stIndex);

    /**
     *
     * @param vertex
     * @return Iterator
     */
    public Iterator getAdjacentVertices(Object vertex, int stIndex);

    /**
     *
     * @param vertex
     * @return Iterator
     */
    public Iterator getPredecessors(Object vertex, int stIndex);
    
    /**
     * 
     * @return int
     */
    public int getTimeStep();
}
