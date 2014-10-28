package algorithm;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;

import model.Graph;

public class GreedyQuickAlgorithm implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;

	@Override
	public LinkedHashSet<Integer> mdsAlg(Graph g) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long start = bean.getCurrentThreadCpuTime();
		ArrayList<Integer> W = new ArrayList<>(g.getVertices());
		prepTime = bean.getCurrentThreadCpuTime() - start;
		Collections.sort(W, new  LessByN1AComparator(g));
		LinkedHashSet<Integer> S = new LinkedHashSet<>();
		while (!W.isEmpty()) {
			Integer pick = W.get(W.size()-1);
			W.removeAll(g.getN1(pick));
			S.add(pick);
		}
		runTime = bean.getCurrentThreadCpuTime() - start;
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
