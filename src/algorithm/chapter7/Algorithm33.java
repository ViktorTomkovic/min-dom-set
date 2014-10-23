package algorithm.chapter7;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.LinkedHashSet;

import model.Graph;
import algorithm.AbstractMDSAlgorithm;

public class Algorithm33 implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;

	Long maxByN1(LinkedHashSet<Long> white,
			HashMap<Long, LinkedHashSet<Long>> neig) {
		int m = 0;
		int mc = 0;
		for (Long c : white) {
			int cc = neig.get(c).size();
			if (cc > mc) {
				m = c.intValue();
				mc = cc;
			}
		}
		return (long) m;
	}

	@Override
	public LinkedHashSet<Long> mdsAlg(Graph g) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long start = bean.getCurrentThreadCpuTime();
		LinkedHashSet<Long> W = new LinkedHashSet<>(g.getVertices());
		HashMap<Long, LinkedHashSet<Long>> neig = new HashMap<>();
		for (Long v : W) {
			neig.put(v, new LinkedHashSet<>(g.getN1(v)));
		}

		prepTime = System.currentTimeMillis() - start;
		// Collections.sort(W);
		LinkedHashSet<Long> S = new LinkedHashSet<>();
		while (!W.isEmpty()) {
			Long pick = maxByN1(W, neig);
			// W.removeAll(g.getNeighboursOfVertexIncluded(pick));
			W.removeAll(neig.get(pick));
			for (Long v : neig.get(pick)) {
				neig.get(v).remove(pick);
			}

			/*
			 * Long mv = w(v, g, W); for (Long v2 : W) { Long mv2 = w(v2, g, W);
			 * if (mv2 > mv) { v = v2; mv = mv2; } }
			 */
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
