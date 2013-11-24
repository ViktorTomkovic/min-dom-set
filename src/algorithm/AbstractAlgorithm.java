package algorithm;

import java.util.LinkedHashSet;

import model.Graph;

public interface AbstractAlgorithm {
	public LinkedHashSet<Long> mdsAlg(Graph g);
}
