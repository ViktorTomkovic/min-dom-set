package mindomset.algorithm;

import mindomset.datastructure.graph.Graph;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.ObjectArrayList;

public interface AbstractMSCAlgorithm {
	public IntOpenHashSet getMSCforMDS(IntOpenHashSet universum,
			ObjectArrayList<RepresentedSet> sets, Graph g);
}
