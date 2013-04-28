package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import algorithm.AbstractAlgorithm;

public class UndirectedGraph implements Graph {
	public ArrayList<Edge> edges;
	public long verticesCount;

	public UndirectedGraph() {
		this.edges = new ArrayList<>();
		this.verticesCount = 0;
	}

	public UndirectedGraph(ArrayList<Edge> edges) {
		this.edges = edges;
		long vc = 0;
		for (Edge e : edges) {
			if (e.from > vc)
				vc = e.from;
			if (e.to > vc)
				vc = e.to;
		}
		this.verticesCount = vc;
	}

	@Override
	public boolean isDirected() {
		return false;
	}

	@Override
	public ArrayList<Edge> getEdges() {
		return null;
	}

	@Override
	public Set<Long> getMDS(AbstractAlgorithm algorithm) {
		return algorithm.mdsAlg(this);
	}

	@Override
	public Long getNumberOfVertices() {
		return verticesCount;
	}

	@Override
	public Set<Long> getNeighboursOf(Long vertex) {
		Set<Long> result = new HashSet<>();
		for (Edge e : edges) {
			if (e.to == vertex)
				result.add(e.from);
			if (e.from == vertex)
				result.add(e.to);
		}
		return result;
	}

	@Override
	public Set<Long> getNeighboursOf2(Long vertex) {
		Set<Long> result = getNeighboursOf(vertex);
		result.add(vertex);
		return result;
	}

}
