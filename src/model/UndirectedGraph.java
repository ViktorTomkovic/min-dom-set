package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import algorithm.AbstractMDSAlgorithm;

public class UndirectedGraph implements Graph {
	public ArrayList<Edge> edges;
	public LinkedHashSet<Long> vertices;
	public HashMap<Long, LinkedHashSet<Long>> neig;
	public HashMap<Long, LinkedHashSet<Long>> neig2;
	public long verticesCount;

	public UndirectedGraph() {
		this.edges = new ArrayList<>();
		this.verticesCount = 0;
	}

	public UndirectedGraph(LinkedHashSet<Long> vertices, ArrayList<Edge> edges) {
		this.edges = edges;
		this.neig = new HashMap<Long, LinkedHashSet<Long>>();
		this.neig2 = new HashMap<Long, LinkedHashSet<Long>>();
		for (Edge e : getEdges()) {
			vertices.add(e.from);
			vertices.add(e.to);
			LinkedHashSet<Long> a;

			a = neig.get(e.from);
			if (a == null)
				a = new LinkedHashSet<Long>();
			a.add(e.to);
			neig.put(e.from, a);

			a = neig.get(e.to);
			if (a == null)
				a = new LinkedHashSet<Long>();
			a.add(e.from);
			neig.put(e.to, a);
		}
		
		for (Long v : neig.keySet()) {
			LinkedHashSet<Long> n1 = neig.get(v);
			LinkedHashSet<Long> n2 = new LinkedHashSet<>(n1);
			for (Long v2 : n1) {
				n2.addAll(neig.get(v2));
			}
			n1.add(v);
			neig2.put(v, n2);
		}
		
		this.vertices = new LinkedHashSet<>(vertices);
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

//	@Override
//	public LinkedHashSet<Long> getNeighboursOf(Long vertex) {
//		return neig.get(vertex);
//	}

	@Override
	public LinkedHashSet<Long> getN1(Long vertex) {
		return neig.get(vertex);
	}

	@Override
	public LinkedHashSet<Long> getVertices() {
		return vertices;
	}

	@Override
	public LinkedHashSet<Long> getN2(Long vertex) {
		return neig2.get(vertex);
	}

	@Override
	public boolean isMDS(LinkedHashSet<Long> mds) {
		HashSet<Long> set = new HashSet<>();
		for (Long v : mds) {
			set.addAll(getN1(v));
		}
		return set.containsAll(getVertices());
	}

}
