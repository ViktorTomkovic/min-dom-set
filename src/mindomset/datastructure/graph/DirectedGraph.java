package mindomset.datastructure.graph;

import java.util.ArrayList;

import mindomset.algorithm.AbstractMDSAlgorithm;
import mindomset.algorithm.AbstractMDSResult;

import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;

public class DirectedGraph implements Graph {
	private ObjectArrayList<Edge> edges;
	private IntOpenHashSet vertices;
	private IntObjectOpenHashMap<IntOpenHashSet> neighboursOf;
	private int verticesCount;

	public DirectedGraph(ArrayList<Edge> edges) {
		ObjectArrayList<Edge> gedges = new ObjectArrayList<>(edges.size());
		for (Edge edge : edges) {
			gedges.add(edge);
		}
		this.edges = gedges;
		this.neighboursOf = new IntObjectOpenHashMap<>(edges.size());
		IntOpenHashSet vertices = new IntOpenHashSet();
		for (ObjectCursor<Edge> ecur : getEdges()) {
			vertices.add(ecur.value.from);
			vertices.add(ecur.value.to);
			IntOpenHashSet a = neighboursOf.getOrDefault(ecur.value.from, IntOpenHashSet.newInstance());
			a.add(ecur.value.to);
			neighboursOf.put(ecur.value.from, a);
		}
		this.vertices = vertices;
		this.verticesCount = vertices.size();
	}

	public DirectedGraph(ObjectArrayList<Edge> edges2) {
		this.edges = new ObjectArrayList<>(edges2);
		this.neighboursOf = new IntObjectOpenHashMap<>(edges2.size());
		IntOpenHashSet vertices = new IntOpenHashSet();
		for (ObjectCursor<Edge> ecur : getEdges()) {
			vertices.add(ecur.value.from);
			vertices.add(ecur.value.to);
			IntOpenHashSet a = neighboursOf.getOrDefault(ecur.value.from, IntOpenHashSet.newInstance());
			a.add(ecur.value.to);
			neighboursOf.put(ecur.value.from, a);
		}
		this.vertices = vertices;
		this.verticesCount = vertices.size();
	}

	@Override
	public boolean isDirected() {
		return true;
	}

	@Override
	public ObjectArrayList<Edge> getEdges() {
		return edges;
	}

	@Override
	public IntOpenHashSet getVertices() {
		return vertices;
	}

	@Override
	public int getNumberOfVertices() {
		return verticesCount;
	}

	public IntOpenHashSet getNeighboursOf(int vertex) {
		return neighboursOf.get(vertex);
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
		for (IntCursor icur : vertices) {
			if (!set.contains(icur.value)) {
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

	public void addEdges(ObjectArrayList<Edge> edges) {
		this.edges.addAll(edges);
		for (ObjectCursor<Edge> e : getEdges()) {
			this.vertices.add(e.value.from);
			this.vertices.add(e.value.to);
			IntOpenHashSet a = neighboursOf.getOrDefault(e.value.from, null);
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
		for (IntCursor icur : vertices) {
			if (!set.contains(icur.value)) {
				isContained = false;
				break;
			}
		}
		return isContained;
	}
}
