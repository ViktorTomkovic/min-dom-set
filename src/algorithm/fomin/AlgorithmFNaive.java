package algorithm.fomin;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import model.Graph;
import algorithm.AbstractMDSAlgorithm;
import algorithm.RepresentedSet;

public class AlgorithmFNaive implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;

	@Override
	public LinkedHashSet<Long> mdsAlg(Graph g) {
		long start = System.currentTimeMillis();
		AlgorithmMSCFNaive fn = new AlgorithmMSCFNaive();
		ArrayList<RepresentedSet> sets = new ArrayList<>();
		for (Long v : g.getVertices()) {
			sets.add(new RepresentedSet(v, g.getN1(v)));
		}
		prepTime = System.currentTimeMillis() - start;
		LinkedHashSet<Long> result = fn.getMSCforMDS(null, sets, g);
		runTime = System.currentTimeMillis() - start;
		return result;
	}

	@Override
	public long getLastPrepTime() {
		return prepTime;
	}

	@Override
	public long getLastRunTime() {
		return runTime;
	}

}
