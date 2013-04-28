package model;

import java.util.ArrayList;
import java.util.Set;

import algorithm.AbstractAlgorithm;

public interface Graph {
	public boolean isDirected();
	public ArrayList<Edge> getEdges();
	public Long getNumberOfVertices();
	public Set<Long> getNeighboursOf(Long vertex);
	public Set<Long> getNeighboursOf2(Long vertex);
	public Set<Long> getMDS(AbstractAlgorithm algorithm);
}
