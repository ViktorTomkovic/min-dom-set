package datastructure.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.cursors.IntCursor;

import datastructure.Dataset;
import algorithm.AbstractMDSAlgorithm;
import algorithm.AbstractMDSResult;

public class CompactUndirectedGraph implements Graph {
	private int edgesCount = 0;
	private int maxVertexNumber = 0;
	private ObjectArrayList<Edge> edges = new ObjectArrayList<Edge>();
	private IntObjectOpenHashMap<IntOpenHashSet> neig1 = new IntObjectOpenHashMap<>();
	private IntObjectOpenHashMap<IntOpenHashSet> neig2 = new IntObjectOpenHashMap<>();

	public CompactUndirectedGraph(Dataset dataset) {
		dataset.setAll();
		edgesCount = dataset.edgesCount;
		maxVertexNumber = dataset.maxVertexNumber;
		edges = new ObjectArrayList<Edge>(dataset.edges);
		neig1 = new IntObjectOpenHashMap<IntOpenHashSet>(dataset.neig1);
		neig2 = new IntObjectOpenHashMap<IntOpenHashSet>(dataset.neig2);
	}

	@Override
	public boolean isDirected() {
		return false;
	}

	@Override
	public ArrayList<Edge> getEdges() {
		ArrayList<Edge> result = new ArrayList<>(edgesCount * 2);
		Object[] values = edges.buffer;
		for (int i = 0; i < edges.elementsCount; i++) {
			Edge edge = (Edge)values[i];
			result.add(new Edge(edge.from, edge.to));
			result.add(new Edge(edge.to, edge.from));
		}
		return result;
	}

	@Override
	public LinkedHashSet<Integer> getVertices() {
		LinkedHashSet<Integer> result = new LinkedHashSet<>(maxVertexNumber);
		for (int i = 1; i < maxVertexNumber; i++) {
			result.add(i);
		}
		return result;
	}

	@Override
	public Integer getNumberOfVertices() {
		return getVertices().size();
	}

	@Override
	public LinkedHashSet<Integer> getN1(Integer vertex) {
		LinkedHashSet<Integer> result = new LinkedHashSet<>();
		IntOpenHashSet cres = neig1.get(vertex);
		int[] keys = cres.keys;
		boolean[] allocated = cres.allocated;
		for (int i = 0; i < allocated.length; i++) {
			if (allocated[i]) {
				result.add(keys[i]);
			}
		}
		return result;
	}

	@Override
	public LinkedHashSet<Integer> getN2(Integer vertex) {
		LinkedHashSet<Integer> result = new LinkedHashSet<>();
		IntOpenHashSet cres = neig2.get(vertex);
		int[] keys = cres.keys;
		boolean[] allocated = cres.allocated;
		for (int i = 0; i < allocated.length; i++) {
			if (allocated[i]) {
				result.add(keys[i]);
			}
		}
		return result;
	}

	@Override
	public AbstractMDSResult getMDS(AbstractMDSAlgorithm algorithm) {
		return algorithm.mdsAlg(this);
	}

	@Override
	public boolean isMDS(LinkedHashSet<Integer> mds) {
		HashSet<Integer> set = new HashSet<>();
		for (Integer v : mds) {
			set.addAll(getN1(v));
		}
		return set.containsAll(getVertices());
	}

	@Override
	public boolean isMDS(AbstractMDSResult mds) {
		HashSet<Integer> set = new HashSet<>();
		for (IntCursor v : mds.getIterableStructure()) {
			set.addAll(getN1(v.value));
		}
		return set.containsAll(getVertices());
	}

}
