package algorithm;

import datastructure.graph.Graph;

public class DummyAlgorithm implements AbstractMDSAlgorithm {

	public DummyAlgorithm() {
	}

	@Override
	public AbstractMDSResult mdsAlg(Graph g) {
		return new MDSResultBackedByIntOpenHashSet();
	}

	@Override
	public long getLastPrepTime() {
		return 0L;
	}

	@Override
	public long getLastRunTime() {
		return 0L;
	}

}
