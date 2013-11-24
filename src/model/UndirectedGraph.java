package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import algorithm.AbstractAlgorithm;

public class UndirectedGraph implements Graph {
	public ArrayList<Edge> edges;
	public ArrayList<Long> vertices;
	public long verticesCount;

	public UndirectedGraph() {
		this.edges = new ArrayList<>();
		this.verticesCount = 0;
	}

	public UndirectedGraph(ArrayList<Edge> edges) {
		this.edges = edges;
		HashSet<Long> vertices = new HashSet<>();
		for (Edge e : getEdges()) {
			vertices.add(e.from);
			vertices.add(e.to);
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
	public LinkedHashSet<Long> getMDS(AbstractAlgorithm algorithm) {
		return algorithm.mdsAlg(this);
	}

	@Override
	public Long getNumberOfVertices() {
		return verticesCount;
	}

	@Override
	public LinkedHashSet<Long> getNeighboursOf(Long vertex) {
		LinkedHashSet<Long> result = new LinkedHashSet<>();
		for (Edge e : edges) {
			if (e.to == vertex)
				result.add(e.from);
			if (e.from == vertex)
				result.add(e.to);
		}
		return result;
	}

	@Override
	public LinkedHashSet<Long> getNeighboursOfVertexIncluded(Long vertex) {
		LinkedHashSet<Long> result = getNeighboursOf(vertex);
		result.add(vertex);
		return result;
	}

	@Override
	public ArrayList<Long> getVertices() {
		HashSet<Long> vertices = new HashSet<>();
		for (Edge e : getEdges()) {
			vertices.add(e.from);
			vertices.add(e.to);
		}
		return new ArrayList<Long>(vertices);
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

}
