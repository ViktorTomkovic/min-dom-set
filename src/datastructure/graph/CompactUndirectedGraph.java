package datastructure.graph;

import algorithm.AbstractMDSAlgorithm;
import algorithm.AbstractMDSResult;

import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.cursors.IntCursor;

import datastructure.Dataset;

public class CompactUndirectedGraph implements Graph {
	private int maxVertexNumber = 0;
	private ObjectArrayList<Edge> edges = new ObjectArrayList<Edge>();
	private IntOpenHashSet vertices = new IntOpenHashSet();
	private IntObjectOpenHashMap<IntOpenHashSet> neig1 = new IntObjectOpenHashMap<>();
	private IntObjectOpenHashMap<IntOpenHashSet> neig2 = new IntObjectOpenHashMap<>();

	public CompactUndirectedGraph(Dataset dataset) {
		dataset.setAll();
		maxVertexNumber = dataset.maxVertexNumber;
		for (int i = 1; i <= maxVertexNumber; i++) {
			vertices.add(i);
		}
		edges = new ObjectArrayList<Edge>(dataset.edges);
		neig1 = new IntObjectOpenHashMap<IntOpenHashSet>(dataset.neig1);
		neig2 = new IntObjectOpenHashMap<IntOpenHashSet>(dataset.neig2);
	}

	@Override
	public boolean isDirected() {
		return false;
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
		return maxVertexNumber;
	}

	@Override
	public IntOpenHashSet getN1(int vertex) {
		return neig1.get(vertex);
	}

	@Override
	public IntOpenHashSet getN2(int vertex) {
		return neig2.get(vertex);
	}

	@Override
	public AbstractMDSResult getMDS(AbstractMDSAlgorithm algorithm) {
		return algorithm.mdsAlg(this);
	}

	@Override
	public boolean isMDS(IntOpenHashSet mds) {
		IntOpenHashSet set = new IntOpenHashSet(maxVertexNumber);
		for (IntCursor v : mds) {
			set.addAll(getN1(v.value));
		}
		boolean isContained = true;
		for (int i = 1; i <= maxVertexNumber; i++) {
			if (!isContained) {
				isContained = false;
				break;
			}
		}
		return isContained;
	}

	@Override
	public boolean isMDS(AbstractMDSResult mds) {
		IntOpenHashSet set = new IntOpenHashSet(maxVertexNumber);
		for (IntCursor v : mds.getIterableStructure()) {
			set.addAll(getN1(v.value));
		}
		boolean isContained = true;
		for (int i = 1; i <= maxVertexNumber; i++) {
			if (!isContained) {
				isContained = false;
				break;
			}
		}
		return isContained;
	}

}
