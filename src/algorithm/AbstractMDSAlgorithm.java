package algorithm;

import java.util.LinkedHashSet;

import model.Graph;

public interface AbstractMDSAlgorithm {
	public LinkedHashSet<Integer> mdsAlg(Graph g);
	public long getLastPrepTime();
	public long getLastRunTime();
}
