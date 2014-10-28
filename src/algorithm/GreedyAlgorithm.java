package algorithm;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.LinkedHashSet;

import model.Graph;

public class GreedyAlgorithm implements AbstractMDSAlgorithm {
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
		LinkedHashSet<Integer> G = new LinkedHashSet<>(g.getVertices());
		HashMap<Integer, LinkedHashSet<Integer>> neigW = new HashMap<>();
		// HashMap<Integer, LinkedHashSet<Integer>> neig2 = new HashMap<>();
		for (Integer v : W) {
			neigW.put(v, new LinkedHashSet<>(g.getN1(v)));
			// neig2.put(v, g.getNeighboursOfDistance2(v));
		}

		prepTime = bean.getCurrentThreadCpuTime() - start;
		LinkedHashSet<Integer> S = new LinkedHashSet<>();
		while (!G.isEmpty()) {
			Integer pick = maxByN1(W, neigW);
			// W.removeAll(g.getNeighboursOfVertexIncluded(pick));
			W.remove(pick);
			LinkedHashSet<Integer> greying = new LinkedHashSet<>(neigW.get(pick));
			G.removeAll(greying);
			for (Integer v : g.getN2(pick)/* neig2.get(pick) */) {
				neigW.get(v).removeAll(greying);
				// neig2.get(v).remove(pick);
			}
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