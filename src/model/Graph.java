package model;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import algorithm.AbstractMDSAlgorithm;

public interface Graph {
	public boolean isDirected();

	public ArrayList<Edge> getEdges();

	public LinkedHashSet<Long> getVertices();

	public Long getNumberOfVertices();

	// public LinkedHashSet<Long> getNeighboursOf(Long vertex);

	public LinkedHashSet<Long> getN1(Long vertex);

	public LinkedHashSet<Long> getN2(Long vertex);

	public LinkedHashSet<Long> getMDS(AbstractMDSAlgorithm algorithm);

	public boolean isMDS(LinkedHashSet<Long> mds);
}
