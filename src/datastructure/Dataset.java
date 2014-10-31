package datastructure;

import com.carrotsearch.hppc.IntIntOpenHashMap;
import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.ObjectArrayList;

import datastructure.graph.Edge;

public class Dataset {
	public long[] packedEdges;
	public ObjectArrayList<Edge> edges;
	public IntObjectOpenHashMap<IntOpenHashSet> neig1;
	public IntObjectOpenHashMap<IntOpenHashSet> neig2;
	

	public Dataset(int[] edgesFrom, int[] edgesTo) {
		if (edgesFrom.length != edgesTo.length) {
			throw new IllegalArgumentException("Not equal size of edges.");
		}
		int edgeCount = edgesFrom.length;
		IntIntOpenHashMap inputVerticesMap = new IntIntOpenHashMap(
				edgeCount >> 4);
		int currentVertexNumber = 1;
		for (int i = 0; i < edgeCount; i++) {
			if (inputVerticesMap.putIfAbsent(edgesFrom[i], currentVertexNumber)) {
				currentVertexNumber++;
			}
			if (inputVerticesMap.putIfAbsent(edgesTo[i], currentVertexNumber)) {
				currentVertexNumber++;
			}
		}
		int maxVertexNumber = currentVertexNumber;
		IntObjectOpenHashMap<IntOpenHashSet> neig1 = new IntObjectOpenHashMap<>(
				currentVertexNumber);
		for (int i = 1; i <= maxVertexNumber; i++) {
			IntOpenHashSet emptySet1 = new IntOpenHashSet();
			emptySet1.add(i);
			neig1.put(i, emptySet1);
		}

		for (int i = 0; i < edgeCount; i++) {
			edgesFrom[i] = inputVerticesMap.get(edgesFrom[i]);
			edgesTo[i] = inputVerticesMap.get(edgesTo[i]);
			neig1.get(edgesFrom[i]).add(edgesTo[i]);
			neig1.get(edgesTo[i]).add(edgesFrom[i]);
		}

		IntObjectOpenHashMap<IntOpenHashSet> neig2 = new IntObjectOpenHashMap<>(
				currentVertexNumber);

		for (int i = 1; i <= maxVertexNumber; i++) {
			IntOpenHashSet emptySet2 = new IntOpenHashSet();
			neig2.put(i, emptySet2);
		}

		for (int i = 0; i < edgeCount; i++) {
			neig2.get(edgesFrom[i]).add(edgesTo[i]);
			neig2.get(edgesTo[i]).add(edgesFrom[i]);
		}

		for (int i = 1; i <= maxVertexNumber; i++) {
			IntOpenHashSet neig1set = neig1.get(i);
			IntOpenHashSet neig2set = neig2.get(i);
			int[] keys = neig1set.keys;
			boolean[] allocated = neig1set.allocated;
			for (int j = 0; j < allocated.length; j++) {
				if (allocated[j]) {
					neig2set.add(keys[j]);
				}
			}
		}

	}

}
