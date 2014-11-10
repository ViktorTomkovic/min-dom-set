package mindomset.algorithm;

import mindomset.datastructure.graph.Graph;

public interface AbstractMDSAlgorithm {
	public AbstractMDSResult mdsAlg(Graph g);
	public long getLastPrepTime();
	public long getLastRunTime();
}
