package algorithm.basic;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.LinkedHashSet;

import algorithm.AbstractMDSAlgorithm;
import model.Graph;

public class GreedyAlgorithm implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;

	private Integer maxByN1(LinkedHashSet<Integer> white,
			HashMap<Integer, LinkedHashSet<Integer>> neig) {
		int m = 0;
		int mc = 0;
		for (Integer c : white) {
			int cc = neig.get(c).size();
			if (cc > mc) {
				m = c.intValue();
				mc = cc;
			}
		}
		return m;
	}

	@Override
	public LinkedHashSet<Integer> mdsAlg(Graph g) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long start = bean.getCurrentThreadCpuTime();
		LinkedHashSet<Integer> W = new LinkedHashSet<>(g.getVertices());
		LinkedHashSet<Integer> G = new LinkedHashSet<>(g.getVertices());
		HashMap<Integer, LinkedHashSet<Integer>> neigW = new HashMap<>();
		for (Integer v : W) {
			neigW.put(v, new LinkedHashSet<>(g.getN1(v)));
		}

		prepTime = bean.getCurrentThreadCpuTime() - start;
		int initialSize = (int)Math.ceil(g.getNumberOfVertices() * (1/0.65)) + 1;
		LinkedHashSet<Integer> S = new LinkedHashSet<>(initialSize, 0.65f);
		int iterations = 0;
		Integer lastMax = -1;
		while (!G.isEmpty()) {
			iterations = iterations + 1;
			Integer pick = maxByN1(W, neigW, lastMax);
			W.remove(pick);
			LinkedHashSet<Integer> greying = new LinkedHashSet<>(neigW.get(pick));
			G.removeAll(greying);
			for (Integer v : g.getN2(pick)) {
				neigW.get(v).removeAll(greying);
			}
			S.add(pick);
		}
		runTime = bean.getCurrentThreadCpuTime() - start;
		System.out.println("Number of iterations: " + iterations);
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