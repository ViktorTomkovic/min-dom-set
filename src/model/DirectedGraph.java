package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import algorithm.AbstractMDSAlgorithm;

public class DirectedGraph implements Graph {
	private ArrayList<Edge> edges;
	private LinkedHashSet<Integer> vertices;
	private HashMap<Integer, LinkedHashSet<Integer>> neighboursOf;
	private int verticesCount;

	public DirectedGraph(ArrayList<Edge> edges) {
		this.edges = edges;
		this.neighboursOf = new HashMap<Integer, LinkedHashSet<Integer>>();
		HashSet<Integer> vertices = new HashSet<>();
		for (Edge e : getEdges()) {
			vertices.add(e.from);
			vertices.add(e.to);
			LinkedHashSet<Integer> a;
			a = neighboursOf.get(e.from);
			if (a == null)
				a = new LinkedHashSet<Integer>();
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
	public LinkedHashSet<Integer> getVertices() {
		return vertices;
	}

	@Override
	public Integer getNumberOfVertices() {
		return verticesCount;
	}

	public LinkedHashSet<Integer> getNeighboursOf(Integer vertex) {
		return neighboursOf.get(vertex);
	}

	@Override
	public LinkedHashSet<Integer> getN1(Integer vertex) {
		LinkedHashSet<Integer> result = getNeighboursOf(vertex);
		result.add(vertex);
		return result;
	}

	@Override
	public LinkedHashSet<Integer> getN2(Integer vertex) {
		LinkedHashSet<Integer> neig = new LinkedHashSet<>();
		LinkedHashSet<Integer> result = new LinkedHashSet<>();
		neig.addAll(getN1(vertex));
		result.addAll(getN1(vertex));
		for (Integer v : neig) {
			result.addAll(getNeighboursOf(v));
		}
		return result;
	}

	@Override
	public boolean isMDS(LinkedHashSet<Integer> mds) {
		HashSet<Integer> set = new HashSet<>(mds);
		for (Integer v : mds) {
			set.addAll(getN1(v));
		}
		return set.containsAll(getVertices());
	}

	@Override
	public LinkedHashSet<Integer> getMDS(AbstractMDSAlgorithm algorithm) {
		return null;
	}
	
	public void addEdges(ArrayList<Edge> edges) {
		this.edges.addAll(edges);
		for (Edge e : getEdges()) {
			this.vertices.add(e.from);
			this.vertices.add(e.to);
			LinkedHashSet<Integer> a;
			a = neighboursOf.get(e.from);
			if (a == null)
				a = new LinkedHashSet<Integer>();
			a.add(e.to);
			neighboursOf.put(e.from, a);
		}
		this.verticesCount = vertices.size();
	}
}
