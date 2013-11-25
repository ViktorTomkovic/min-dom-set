package algorithm;

import java.util.LinkedHashSet;

import model.Graph;

public interface AbstractMDSAlgorithm {
	public LinkedHashSet<Long> mdsAlg(Graph g);
}
