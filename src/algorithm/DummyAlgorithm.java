package algorithm;

import java.util.LinkedHashSet;

import datastructure.graph.Graph;

public class DummyAlgorithm implements AbstractMDSAlgorithm {

	public DummyAlgorithm() {
	}

	@Override
	public LinkedHashSet<Integer> mdsAlg(Graph g) {
		return new LinkedHashSet<>();
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
