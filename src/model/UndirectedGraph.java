package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import algorithm.AbstractMDSAlgorithm;

public class UndirectedGraph implements Graph {
	public ArrayList<Edge> edges;
	public ArrayList<Long> vertices;
	public HashMap<Long, LinkedHashSet<Long>> neighboursOf;
	public long verticesCount;

	public UndirectedGraph() {
		this.edges = new ArrayList<>();
		this.verticesCount = 0;
	}

	public UndirectedGraph(ArrayList<Edge> edges) {
		this.edges = edges;
		this.neighboursOf = new HashMap<Long, LinkedHashSet<Long>>();
		HashSet<Long> vertices = new HashSet<>();
		for (Edge e : getEdges()) {
			vertices.add(e.from);
			vertices.add(e.to);
			LinkedHashSet<Long> a;
			a = neighboursOf.get(e.from);
			if (a == null)
				a = new LinkedHashSet<Long>();
			a.add(e.to);
			neighboursOf.put(e.from, a);
			a = neighboursOf.get(e.to);
			if (a == null)
				a = new LinkedHashSet<Long>();
			a.add(e.from);
			neighboursOf.put(e.to, a);
		}
		this.vertices = new ArrayList<>(vertices);
		this.verticesCount = vertices.size();
	}

	@Override
	public boolean isDirected() {
		return false;
	}

	@Override
	public ArrayList<Edge> getEdges() {
		return edges;
	}

	@Override
	public LinkedHashSet<Long> getMDS(AbstractMDSAlgorithm algorithm) {
		return algorithm.mdsAlg(this);
	}

	@Override
	public Long getNumberOfVertices() {
		return verticesCount;
	}

	@Override
	public LinkedHashSet<Long> getNeighboursOf(Long vertex) {
		return neighboursOf.get(vertex);
	}

	@Override
	public LinkedHashSet<Long> getNeighboursOfVertexIncluded(Long vertex) {
		LinkedHashSet<Long> result = getNeighboursOf(vertex);
		result.add(vertex);
		return result;
	}

	@Override
	public ArrayList<Long> getVertices() {
		return vertices;
	}

	@Override
	public LinkedHashSet<Long> getNeighboursOfDistance2(Long vertex) {
		LinkedHashSet<Long> neig = new LinkedHashSet<>();
		LinkedHashSet<Long> result = new LinkedHashSet<>();
		neig.addAll(getNeighboursOfVertexIncluded(vertex));
		for (Long v : neig) {
			result.addAll(getNeighboursOf(v));
		}
		return result;
	}

	@Override
	public boolean isMDS(LinkedHashSet<Long> mds) {
		HashSet<Long> set = new HashSet<>(mds);
		for (Long v : mds) {
			set.addAll(getNeighboursOf(v));
		}
		return set.containsAll(getVertices());
	}

}
