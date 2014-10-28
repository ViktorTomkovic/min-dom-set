package model;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import algorithm.AbstractMDSAlgorithm;

public interface Graph {
	public boolean isDirected();

	public ArrayList<Edge> getEdges();

	public LinkedHashSet<Integer> getVertices();

	public Integer getNumberOfVertices();

	// public LinkedHashSet<Long> getNeighboursOf(Long vertex);

	public LinkedHashSet<Integer> getN1(Integer vertex);

	public LinkedHashSet<Integer> getN2(Integer vertex);

	public LinkedHashSet<Integer> getMDS(AbstractMDSAlgorithm algorithm);

	public boolean isMDS(LinkedHashSet<Integer> mds);
}
