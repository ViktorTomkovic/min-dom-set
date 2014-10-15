package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;

import model.Graph;

public class GreedyQuickAlgorithm implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;

	@Override
	public LinkedHashSet<Long> mdsAlg(Graph g) {
		long start = System.currentTimeMillis();
		ArrayList<Long> W = new ArrayList<>(g.getVertices());
		prepTime = System.currentTimeMillis() - start;
		Collections.sort(W, new  LessByN1AComparator(g));
		LinkedHashSet<Long> S = new LinkedHashSet<>();
		while (!W.isEmpty()) {
			Long pick = W.get(W.size()-1);
			W.removeAll(g.getN1(pick));
			S.add(pick);
		}
		runTime = System.currentTimeMillis() - start;
		return S;
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
