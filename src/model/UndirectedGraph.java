package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import algorithm.AbstractMDSAlgorithm;

public class UndirectedGraph implements Graph {
	public static final int LONG_OFFSET = 32;
	public ArrayList<Edge> edges;
	public LinkedHashSet<Integer> vertices;
	public HashMap<Integer, LinkedHashSet<Integer>> neig;
	public HashMap<Integer, LinkedHashSet<Integer>> neig2;
	public int verticesCount;

	public UndirectedGraph() {
		this.edges = new ArrayList<>();
		this.verticesCount = 0;
	}

	public UndirectedGraph(LinkedHashSet<Integer> vertices, long[] aedges,
			int size) {
		ArrayList<Edge> edges = new ArrayList<>(size + 16);
		int mask = -1;
		for (int i = 0; i < size; i++) {
			// System.out.print(Long.toBinaryString(aedges[i]) + " ");
			int aa = (int) ((aedges[i] >> LONG_OFFSET) & mask);
			// System.out.print(Integer.toBinaryString(aa) + " ");
			int bb = (int) (aedges[i] & mask);
			// System.out.println(Integer.toBinaryString(bb));
			//System.out.print(aa + "," + bb + ";");
			edges.add(new Edge(aa, bb));
		}
		this.edges = edges;
		this.neig = new HashMap<Integer, LinkedHashSet<Integer>>();
		this.neig2 = new HashMap<Integer, LinkedHashSet<Integer>>();
		for (Edge e : getEdges()) {
			vertices.add(e.from);
			vertices.add(e.to);
			LinkedHashSet<Integer> a;

			a = neig.get(e.from);
			if (a == null)
				a = new LinkedHashSet<Integer>();
			a.add(e.to);
			neig.put(e.from, a);

			a = neig.get(e.to);
			if (a == null)
				a = new LinkedHashSet<Integer>();
			a.add(e.from);
			neig.put(e.to, a);
		}

		for (Integer v : neig.keySet()) {
			LinkedHashSet<Integer> n1 = neig.get(v);
			LinkedHashSet<Integer> n2 = new LinkedHashSet<>(n1);
			for (Integer v2 : n1) {
				n2.addAll(neig.get(v2));
			}
			n1.add(v);
			neig2.put(v, n2);
		}

		this.vertices = new LinkedHashSet<>(vertices);
		this.verticesCount = vertices.size();

	}

	public UndirectedGraph(LinkedHashSet<Integer> vertices,
			ArrayList<Edge> edges) {
		this.edges = edges;
		this.neig = new HashMap<Integer, LinkedHashSet<Integer>>();
		this.neig2 = new HashMap<Integer, LinkedHashSet<Integer>>();
		for (Edge e : getEdges()) {
			vertices.add(e.from);
			vertices.add(e.to);
			LinkedHashSet<Integer> a;

			a = neig.get(e.from);
			if (a == null)
				a = new LinkedHashSet<Integer>();
			a.add(e.to);
			neig.put(e.from, a);

			a = neig.get(e.to);
			if (a == null)
				a = new LinkedHashSet<Integer>();
			a.add(e.from);
			neig.put(e.to, a);
		}

		for (Integer v : neig.keySet()) {
			LinkedHashSet<Integer> n1 = neig.get(v);
			LinkedHashSet<Integer> n2 = new LinkedHashSet<>(n1);
			for (Integer v2 : n1) {
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
	public LinkedHashSet<Integer> getMDS(AbstractMDSAlgorithm algorithm) {
		return algorithm.mdsAlg(this);
	}

	@Override
	public Integer getNumberOfVertices() {
		return verticesCount;
	}

	// @Override
	// public LinkedHashSet<Integer> getNeighboursOf(Integer vertex) {
	// return neig.get(vertex);
	// }

	@Override
	public LinkedHashSet<Integer> getN1(Integer vertex) {
		return neig.get(vertex);
	}

	@Override
	public LinkedHashSet<Integer> getVertices() {
		return vertices;
	}

	@Override
	public LinkedHashSet<Integer> getN2(Integer vertex) {
		return neig2.get(vertex);
	}

	@Override
	public boolean isMDS(LinkedHashSet<Integer> mds) {
		HashSet<Integer> set = new HashSet<>();
		for (Integer v : mds) {
			set.addAll(getN1(v));
		}
		return set.containsAll(getVertices());
	}

}
