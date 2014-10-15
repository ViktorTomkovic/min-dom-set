package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import algorithm.AbstractMDSAlgorithm;

public class DirectedGraph implements Graph {
	private ArrayList<Edge> edges;
	private LinkedHashSet<Long> vertices;
	private HashMap<Long, LinkedHashSet<Long>> neighboursOf;
	private long verticesCount;

	public DirectedGraph(ArrayList<Edge> edges) {
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
		}
		this.vertices = new LinkedHashSet<>(vertices);
		this.verticesCount = vertices.size();
	}
	
	@Override
	public boolean isDirected() {
		return true;
	}

	@Override
	public ArrayList<Edge> getEdges() {
		return edges;
	}

	@Override
	public LinkedHashSet<Long> getVertices() {
		return vertices;
	}

	@Override
	public Long getNumberOfVertices() {
		return verticesCount;
	}

	public LinkedHashSet<Long> getNeighboursOf(Long vertex) {
		return neighboursOf.get(vertex);
	}

	@Override
	public LinkedHashSet<Long> getN1(Long vertex) {
		LinkedHashSet<Long> result = getNeighboursOf(vertex);
		result.add(vertex);
		return result;
	}

	@Override
	public LinkedHashSet<Long> getN2(Long vertex) {
		LinkedHashSet<Long> neig = new LinkedHashSet<>();
		LinkedHashSet<Long> result = new LinkedHashSet<>();
		neig.addAll(getN1(vertex));
		result.addAll(getN1(vertex));
		for (Long v : neig) {
			result.addAll(getNeighboursOf(v));
		}
		return result;
	}

	@Override
	public boolean isMDS(LinkedHashSet<Long> mds) {
		HashSet<Long> set = new HashSet<>(mds);
		for (Long v : mds) {
			set.addAll(getN1(v));
		}
		return set.containsAll(getVertices());
	}

	@Override
	public LinkedHashSet<Long> getMDS(AbstractMDSAlgorithm algorithm) {
		return null;
	}
	
	public void addEdges(ArrayList<Edge> edges) {
		this.edges.addAll(edges);
		for (Edge e : getEdges()) {
			this.vertices.add(e.from);
			this.vertices.add(e.to);
			LinkedHashSet<Long> a;
			a = neighboursOf.get(e.from);
			if (a == null)
				a = new LinkedHashSet<Long>();
			a.add(e.to);
			neighboursOf.put(e.from, a);
		}
		this.verticesCount = vertices.size();
	}
}
