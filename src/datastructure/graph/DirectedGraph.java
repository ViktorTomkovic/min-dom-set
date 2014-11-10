package datastructure.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import algorithm.AbstractMDSAlgorithm;
import algorithm.AbstractMDSResult;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;

// TODO prerobit na HPPC

public class DirectedGraph implements Graph {
	private ArrayList<Edge> edges;
	private LinkedHashSet<Integer> vertices;
	private HashMap<Integer, LinkedHashSet<Integer>> neighboursOf;
	private int verticesCount;

	public DirectedGraph(ArrayList<Edge> edges) {
		this.edges = edges;
		this.neighboursOf = new HashMap<Integer, LinkedHashSet<Integer>>();
		HashSet<Integer> vertices = new HashSet<>();
		for (ObjectCursor<Edge> e : getEdges()) {
			vertices.add(e.value.from);
			vertices.add(e.value.to);
			LinkedHashSet<Integer> a;
			a = neighboursOf.get(e.value.from);
			if (a == null)
				a = new LinkedHashSet<Integer>();
			a.add(e.value.to);
			neighboursOf.put(e.value.from, a);
		}
		this.vertices = new LinkedHashSet<>(vertices);
		this.verticesCount = vertices.size();
	}

	public DirectedGraph(ObjectArrayList<Edge> edges2) {
		ArrayList<Edge> edges = new ArrayList<>(edges2.size());
		for (ObjectCursor<Edge> edgecur : edges2) {
			edges.add(edgecur.value);
		}

		this.edges = edges;
		this.neighboursOf = new HashMap<Integer, LinkedHashSet<Integer>>();
		HashSet<Integer> vertices = new HashSet<>();
		for (ObjectCursor<Edge> e : getEdges()) {
			vertices.add(e.value.from);
			vertices.add(e.value.to);
			LinkedHashSet<Integer> a;
			a = neighboursOf.get(e.value.from);
			if (a == null)
				a = new LinkedHashSet<Integer>();
			a.add(e.value.to);
			neighboursOf.put(e.value.from, a);
		}
		this.vertices = new LinkedHashSet<>(vertices);
		this.verticesCount = vertices.size();
	}

	@Override
	public boolean isDirected() {
		return true;
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
	public IntOpenHashSet getVertices() {
		IntOpenHashSet result = new IntOpenHashSet(vertices.size());
		for (Integer vertex : vertices) {
			result.add(vertex);
		}
		return result;
	}

	@Override
	public int getNumberOfVertices() {
		return verticesCount;
	}

	public IntOpenHashSet getNeighboursOf(Integer vertex) {
		LinkedHashSet<Integer> neigh = neighboursOf.get(vertex);
		IntOpenHashSet result = new IntOpenHashSet(neigh.size());
		for (Integer vertex2 : neigh) {
			result.add(vertex2);
		}
		return result;
	}

	@Override
	public IntOpenHashSet getN1(int vertex) {
		IntOpenHashSet result = getNeighboursOf(vertex);
		result.add(vertex);
		return result;
	}

	@Override
	public IntOpenHashSet getN2(int vertex) {
		IntOpenHashSet neig = getN1(vertex);
		IntOpenHashSet result = new IntOpenHashSet();
		for (IntCursor v : neig) {
			result.addAll(getNeighboursOf(v.value));
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
	public AbstractMDSResult getMDS(AbstractMDSAlgorithm algorithm) {
		return null;
	}

	public void addEdges(ArrayList<Edge> edges) {
		this.edges.addAll(edges);
		for (ObjectCursor<Edge> e : getEdges()) {
			this.vertices.add(e.value.from);
			this.vertices.add(e.value.to);
			LinkedHashSet<Integer> a;
			a = neighboursOf.get(e.value.from);
			if (a == null)
				a = new LinkedHashSet<Integer>();
			a.add(e.value.to);
			neighboursOf.put(e.value.from, a);
		}
		this.verticesCount = vertices.size();
	}

	@Override
	public boolean isMDS(AbstractMDSResult mds) {
		IntOpenHashSet set = new IntOpenHashSet();
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
