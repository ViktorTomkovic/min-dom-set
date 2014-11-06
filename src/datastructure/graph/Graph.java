package datastructure.graph;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import algorithm.AbstractMDSAlgorithm;
import algorithm.AbstractMDSResult;

public interface Graph {
	public boolean isDirected();

	public ArrayList<Edge> getEdges();

	public LinkedHashSet<Integer> getVertices();

	public Integer getNumberOfVertices();

	// public LinkedHashSet<Long> getNeighboursOf(Long vertex);

	public LinkedHashSet<Integer> getN1(Integer vertex);

	public LinkedHashSet<Integer> getN2(Integer vertex);

	public AbstractMDSResult getMDS(AbstractMDSAlgorithm algorithm);

	public boolean isMDS(AbstractMDSResult mds);

	public boolean isMDS(LinkedHashSet<Integer> mds);
}
