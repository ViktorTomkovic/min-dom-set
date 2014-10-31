package algorithm;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import datastructure.graph.Graph;

public interface AbstractMSCAlgorithm {
	public LinkedHashSet<Integer> getMSCforMDS(LinkedHashSet<Integer> universum,
			ArrayList<RepresentedSet> sets, Graph g);
}
