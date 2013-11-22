package model;

import java.util.ArrayList;
import java.util.Set;

import algorithm.AbstractAlgorithm;

public interface Graph {
	public boolean isDirected();
	public ArrayList<Edge> getEdges();
	public ArrayList<Long> getVertices();
	public Long getNumberOfVertices();
	public Set<Long> getNeighboursOf(Long vertex);
	public Set<Long> getNeighboursOfVertexIncluded(Long vertex);
	public Set<Long> getNeighboursOfDistance2(Long vertex);
	public Set<Long> getMDS(AbstractAlgorithm algorithm);
}
