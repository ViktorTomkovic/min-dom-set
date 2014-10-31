package algorithm.chapter7;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.LinkedHashSet;

import datastructure.graph.Graph;
import algorithm.AbstractMDSAlgorithm;

public class Algorithm33 implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;

	Integer maxByN1(LinkedHashSet<Integer> white,
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
		HashMap<Integer, LinkedHashSet<Integer>> neig = new HashMap<>();
		for (Integer v : W) {
			neig.put(v, new LinkedHashSet<>(g.getN1(v)));
		}

		prepTime = bean.getCurrentThreadCpuTime() - start;
		// Collections.sort(W);
		LinkedHashSet<Integer> S = new LinkedHashSet<>();
		while (!W.isEmpty()) {
			Integer pick = maxByN1(W, neig);
			// W.removeAll(g.getNeighboursOfVertexIncluded(pick));
			W.removeAll(neig.get(pick));
			for (Integer v : neig.get(pick)) {
				neig.get(v).remove(pick);
			}

			/*
			 * Integer mv = w(v, g, W); for (Integer v2 : W) { Integer mv2 = w(v2, g, W);
			 * if (mv2 > mv) { v = v2; mv = mv2; } }
			 */
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
