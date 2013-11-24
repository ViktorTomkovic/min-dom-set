package model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import algorithm.AbstractAlgorithm;

public interface Graph {
	public boolean isDirected();
	public ArrayList<Edge> getEdges();
	public ArrayList<Long> getVertices();
	public Long getNumberOfVertices();
	public LinkedHashSet<Long> getNeighboursOf(Long vertex);
	public LinkedHashSet<Long> getNeighboursOfVertexIncluded(Long vertex);
	public LinkedHashSet<Long> getNeighboursOfDistance2(Long vertex);
	public LinkedHashSet<Long> getMDS(AbstractAlgorithm algorithm);
}
