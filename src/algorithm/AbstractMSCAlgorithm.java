package algorithm;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import model.Graph;

public interface AbstractMSCAlgorithm {
	public LinkedHashSet<Long> getMSCforMDS(LinkedHashSet<Long> universum,
			ArrayList<RepresentedSet> sets, Graph g);
}
