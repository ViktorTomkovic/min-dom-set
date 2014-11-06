package algorithm;

import datastructure.graph.Graph;

public interface AbstractMDSAlgorithm {
	public AbstractMDSResult mdsAlg(Graph g);
	public long getLastPrepTime();
	public long getLastRunTime();
}
