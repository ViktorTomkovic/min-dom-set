package algorithm;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.ObjectArrayList;

import datastructure.graph.Graph;

public interface AbstractMSCAlgorithm {
	public IntOpenHashSet getMSCforMDS(IntOpenHashSet universum,
			ObjectArrayList<RepresentedSet> sets, Graph g);
}
