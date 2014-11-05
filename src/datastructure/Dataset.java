package datastructure;

import com.carrotsearch.hppc.IntIntOpenHashMap;
import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.ObjectArrayList;

import datastructure.graph.Edge;

public class Dataset {
	public int[] edgesFrom = new int[0];
	public int[] edgesTo = new int[0];
	public int edgesCount = 0;
	public int maxVertexNumber = 0;
	public boolean areRawEdgesSet = false;
	public ObjectArrayList<Edge> edges = new ObjectArrayList<Edge>();
	public boolean areEdgesSet = false;
	public IntObjectOpenHashMap<IntOpenHashSet> neig1 = new IntObjectOpenHashMap<>();
	public boolean isNeig1Set = false;
	public IntObjectOpenHashMap<IntOpenHashSet> neig2 = new IntObjectOpenHashMap<>();
	public boolean isNeig2Set = false;

	public Dataset() {
	}

	public void setAll() {
		if (!areRawEdgesSet) {
			setRawEdges(new int[0], new int[0], 0);
		}
		if (!areEdgesSet) {
			setEdges();
		}
		if (!isNeig1Set) {
			setNeig1();
		}
		if (!isNeig2Set) {
			setNeig2();
		}
	}

	public void initAll(int[] edgesFrom, int[] edgesTo, int edgesCount) {
		this.edgesCount = edgesCount;
		IntIntOpenHashMap inputVerticesMap = new IntIntOpenHashMap(
				edgesCount >> 4);
		int currentVertexNumber = 1;
		for (int i = 0; i < edgesCount; i++) {
			if (inputVerticesMap.putIfAbsent(edgesFrom[i], currentVertexNumber)) {
				currentVertexNumber++;
			}
			if (inputVerticesMap.putIfAbsent(edgesTo[i], currentVertexNumber)) {
				currentVertexNumber++;
			}
		}
		maxVertexNumber = currentVertexNumber;
		this.edgesFrom = new int[edgesCount];
		this.edgesTo = new int[edgesCount];
		for (int i = 0; i < edgesCount; i++) {
			this.edgesFrom[i] = inputVerticesMap.get(edgesFrom[i]);
			this.edgesTo[i] = inputVerticesMap.get(edgesTo[i]);
		}
		areRawEdgesSet = true;
		// //
		neig1 = new IntObjectOpenHashMap<>(maxVertexNumber);
		for (int i = 1; i <= maxVertexNumber; i++) {
			IntOpenHashSet emptySet1 = new IntOpenHashSet();
			emptySet1.add(i);
			neig1.put(i, emptySet1);
		}
		edges = new ObjectArrayList<Edge>(edgesCount);
		for (int i = 0; i < edgesCount; i++) {
			edges.add(new Edge(edgesFrom[i], edgesTo[i]));
			neig1.get(edgesFrom[i]).add(edgesTo[i]);
			neig1.get(edgesTo[i]).add(edgesFrom[i]);
		}
		areEdgesSet = true;
		isNeig1Set = true;
		// //
		neig2 = new IntObjectOpenHashMap<>(maxVertexNumber);
		for (int i = 1; i <= maxVertexNumber; i++) {
			IntOpenHashSet neig1set = new IntOpenHashSet(neig1.get(i));
			neig2.put(i, neig1set);
		}
		for (int i = 0; i < edgesCount; i++) {
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
					int neigh = keys[j];
					IntOpenHashSet neighNeig1set = neig1.get(neigh);
					int[] neighKeys = neighNeig1set.keys;
					boolean[] neighAllocated = neighNeig1set.allocated;
					for (int k = 0; k < neighAllocated.length; k++) {
						if (neighAllocated[k]) {
							neig2set.add(neighKeys[k]);
						}
					}
				}
			}
		}

		isNeig2Set = true;
	}

	public void setRawEdges(int[] edgesFrom, int[] edgesTo, int edgesCount) {
		this.edgesCount = edgesCount;
		IntIntOpenHashMap inputVerticesMap = new IntIntOpenHashMap(
				edgesCount >> 4);
		int currentVertexNumber = 1;
		for (int i = 0; i < edgesCount; i++) {
			if (inputVerticesMap.putIfAbsent(edgesFrom[i], currentVertexNumber)) {
				currentVertexNumber++;
			}
			if (inputVerticesMap.putIfAbsent(edgesTo[i], currentVertexNumber)) {
				currentVertexNumber++;
			}
		}
		maxVertexNumber = currentVertexNumber;
		this.edgesFrom = new int[edgesCount];
		this.edgesTo = new int[edgesCount];
		// System.out.println(Utils.largeIntArrayToString(edgesFrom));
		// System.out.println(Utils.largeIntArrayToString(edgesTo));
		for (int i = 0; i < edgesCount; i++) {
			this.edgesFrom[i] = inputVerticesMap.get(edgesFrom[i]);
			this.edgesTo[i] = inputVerticesMap.get(edgesTo[i]);
		}
		// System.out.println(Utils.largeIntArrayToString(this.edgesFrom));
		// System.out.println(Utils.largeIntArrayToString(this.edgesTo));
		areRawEdgesSet = true;
	}

	public void setEdges() {
		if (!areRawEdgesSet) {
			throw new IllegalStateException(
					"Raw edges have to be set (this should be done in constructor)");
		}
		edges = new ObjectArrayList<Edge>(edgesCount);
		for (int i = 0; i < edgesCount; i++) {
			edges.add(new Edge(edgesFrom[i], edgesTo[i]));
		}
		areEdgesSet = true;
	}

	public void setNeig1() {
		if (!areRawEdgesSet) {
			throw new IllegalStateException(
					"Raw edges have to be set (this should be done in constructor)");
		}
		neig1 = new IntObjectOpenHashMap<>(maxVertexNumber);
		for (int i = 1; i <= maxVertexNumber; i++) {
			IntOpenHashSet emptySet1 = new IntOpenHashSet();
			emptySet1.add(i);
			neig1.put(i, emptySet1);
		}

		for (int i = 0; i < edgesCount; i++) {
			neig1.get(edgesFrom[i]).add(edgesTo[i]);
			neig1.get(edgesTo[i]).add(edgesFrom[i]);
		}

		isNeig1Set = true;
	}

	public void setNeig2() {
		if (!isNeig1Set) {
			throw new IllegalStateException(
					"Table of neighbours distance 1 has to be created.");
		}
		neig2 = new IntObjectOpenHashMap<>(maxVertexNumber);
		for (int i = 1; i <= maxVertexNumber; i++) {
			IntOpenHashSet neig1set = new IntOpenHashSet(neig1.get(i));
			neig2.put(i, neig1set);
		}

		for (int i = 0; i < edgesCount; i++) {
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
					int neigh = keys[j];
					IntOpenHashSet neighNeig1set = neig1.get(neigh);
					int[] neighKeys = neighNeig1set.keys;
					boolean[] neighAllocated = neighNeig1set.allocated;
					for (int k = 0; k < neighAllocated.length; k++) {
						if (neighAllocated[k]) {
							neig2set.add(neighKeys[k]);
						}
					}
				}
			}
		}

		isNeig2Set = true;
	}

	public ObjectArrayList<Edge> getEdges() {
		if (!areEdgesSet) {
			throw new IllegalStateException("Edges are not set yet.");
		}
		return edges;
	}

	public IntObjectOpenHashMap<IntOpenHashSet> getNeig1() {
		if (!isNeig1Set) {
			throw new IllegalStateException(
					"Table of neighbours distance 1 is not set yet.");
		}
		return neig1;
	}

	public IntObjectOpenHashMap<IntOpenHashSet> getNeig2() {
		if (!isNeig2Set) {
			throw new IllegalStateException(
					"Table of neighbours distance 2 is not set yet.");
		}
		return neig2;
	}

	public Dataset deepCopy() {
		Dataset that = new Dataset();
		that.edgesFrom = new int[this.edgesCount];
		System.arraycopy(this.edgesFrom, 0, that.edgesFrom, 0, this.edgesCount);
		that.edgesTo = new int[this.edgesCount];
		System.arraycopy(this.edgesTo, 0, that.edgesTo, 0, this.edgesCount);
		that.edgesCount = this.edgesCount;
		that.areRawEdgesSet = this.areRawEdgesSet;
		that.maxVertexNumber = this.maxVertexNumber;
		that.edges = new ObjectArrayList<>(this.edgesCount);
		that.areEdgesSet = this.areEdgesSet;
		that.neig1 = new IntObjectOpenHashMap<>(this.neig1);
		that.isNeig1Set = this.isNeig1Set;
		that.neig2 = new IntObjectOpenHashMap<>(this.neig2);
		that.isNeig2Set = this.isNeig2Set;
		return that;
	}

}
