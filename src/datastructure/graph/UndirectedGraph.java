package datastructure.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

import algorithm.AbstractMDSAlgorithm;
import algorithm.AbstractMDSResult;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;

public class UndirectedGraph implements Graph {
	public ArrayList<Edge> edges;
	public LinkedHashSet<Integer> vertices;
	public HashMap<Integer, LinkedHashSet<Integer>> neig;
	public HashMap<Integer, LinkedHashSet<Integer>> neig2;
	public int verticesCount;

	public UndirectedGraph() {
		this.edges = new ArrayList<>();
		this.verticesCount = 0;
	}

	public UndirectedGraph(LinkedHashSet<Integer> vertices,
			ArrayList<Edge> edges) {
		this.edges = edges;
		this.neig = new HashMap<Integer, LinkedHashSet<Integer>>();
		this.neig2 = new HashMap<Integer, LinkedHashSet<Integer>>();
		for (ObjectCursor<Edge> e : getEdges()) {
			vertices.add(e.value.from);
			vertices.add(e.value.to);
			LinkedHashSet<Integer> a;

			a = neig.get(e.value.from);
			if (a == null)
				a = new LinkedHashSet<Integer>();
			a.add(e.value.to);
			neig.put(e.value.from, a);

			a = neig.get(e.value.to);
			if (a == null)
				a = new LinkedHashSet<Integer>();
			a.add(e.value.from);
			neig.put(e.value.to, a);
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
	public ObjectArrayList<Edge> getEdges() {
		ObjectArrayList<Edge> result = new ObjectArrayList<>(edges.size());
		for (Edge edge : edges) {
			result.add(edge);
		}
		return result;
	}

	@Override
	public AbstractMDSResult getMDS(AbstractMDSAlgorithm algorithm) {
		return algorithm.mdsAlg(this);
	}

	@Override
	public int getNumberOfVertices() {
		return verticesCount;
	}

	// @Override
	// public LinkedHashSet<Integer> getNeighboursOf(Integer vertex) {
	// return neig.get(vertex);
	// }

	@Override
	public IntOpenHashSet getN1(int vertex) {
		LinkedHashSet<Integer> neigh = neig.get(vertex);
		IntOpenHashSet result = new IntOpenHashSet(neigh.size());
		for (Integer i : neigh) {
			result.add(i);
		}
		return result;
	}

	@Override
	public IntOpenHashSet getVertices() {
		IntOpenHashSet result = new IntOpenHashSet(verticesCount);
		for (Integer i : vertices) {
			result.add(i);
		}
		return result;
	}

	@Override
	public IntOpenHashSet getN2(int vertex) {
		LinkedHashSet<Integer> neigh = neig2.get(vertex);
		IntOpenHashSet result = new IntOpenHashSet(neigh.size());
		for (Integer i : neigh) {
			result.add(i);
		}
		return result;
	}

	@Override
	public boolean isMDS(IntOpenHashSet mds) {
		IntOpenHashSet set = new IntOpenHashSet(verticesCount);
		for (IntCursor v : mds) {
			set.addAll(getN1(v.value));
		}
		boolean isContained = true;
		for (Integer i : vertices) {
			if (!set.contains(i)) {
				isContained = false;
				break;
			}
		}
		return isContained;
	}

	@Override
	public boolean isMDS(AbstractMDSResult mds) {
		IntOpenHashSet set = new IntOpenHashSet(verticesCount);
		for (IntCursor v : mds.getIterableStructure()) {
			set.addAll(getN1(v.value));
		}
		boolean isContained = true;
		for (Integer i : vertices) {
			if (!set.contains(i)) {
				isContained = false;
				break;
			}
		}
		return isContained;
	}

}
