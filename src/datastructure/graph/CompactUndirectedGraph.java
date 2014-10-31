package datastructure.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

import algorithm.AbstractMDSAlgorithm;

public class CompactUndirectedGraph implements Graph {
	private static final int LONG_OFFSET = 32;
	private static final long MASK_B = (1L << 32) - 1;
	private static final long MASK_A = (-1L) ^ MASK_B;
	private int edgesCount;
	private long[] edges;

	public CompactUndirectedGraph(LinkedHashSet<Integer> vertices, long[] aedges) {
		if (aedges.length > (Integer.MAX_VALUE >> 1)) {
			throw new IllegalArgumentException("Too much edges.");
		}
		edgesCount = aedges.length;
		edges = new long[aedges.length];
		System.arraycopy(aedges, 0, edges, 0, aedges.length);
//		Map<Integer, Integer> Verte = new HashMap<>();
//		for (int i = 0; i < this.edges.length; i++) {
//			int aa = (int) ((edges[i] & MASK_A) >> LONG_OFFSET);
//			int bb = (int) (edges[i] & MASK_B);
//		}
	}

	@Override
	public boolean isDirected() {
		return false;
	}

	@Override
	public ArrayList<Edge> getEdges() {
		ArrayList<Edge> result = new ArrayList<>(edgesCount * 2);
		for (int i = 0; i < edges.length; i++) {
			int aa = (int) ((edges[i] & MASK_A) >> LONG_OFFSET);
			int bb = (int) (edges[i] & MASK_B);
			result.add(new Edge(aa, bb));
			result.add(new Edge(bb, aa));
		}
		return result;
	}

	@Override
	public LinkedHashSet<Integer> getVertices() {
		LinkedHashSet<Integer> result = new LinkedHashSet<>();
		for (int i = 0; i < edges.length; i++) {
			int aa = (int) ((edges[i] & MASK_A) >> LONG_OFFSET);
			int bb = (int) (edges[i] & MASK_B);
			result.add(aa);
			result.add(bb);
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
		for (int i = 0; i < edges.length; i++) {
			int aa = (int) ((edges[i] & MASK_A) >> LONG_OFFSET);
			int bb = (int) (edges[i] & MASK_B);
			if (aa == vertex) {
				result.add(bb);
			}
			if (bb == vertex) {
				result.add(aa);
			}
		}
		result.add(vertex);
		return result;
	}

	@Override
	public LinkedHashSet<Integer> getN2(Integer vertex) {
		LinkedHashSet<Integer> n1 = getN1(vertex);
		LinkedHashSet<Integer> result = new LinkedHashSet<>();
		for (int i = 0; i < edges.length; i++) {
			int aa = (int) ((edges[i] & MASK_A) >> LONG_OFFSET);
			int bb = (int) (edges[i] & MASK_B);
			if (n1.contains(aa)) {
				result.add(bb);
			}
			if (n1.contains(bb)) {
				result.add(aa);
			}
		}
		return result;
	}

	@Override
	public LinkedHashSet<Integer> getMDS(AbstractMDSAlgorithm algorithm) {
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

}
