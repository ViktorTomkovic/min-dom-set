package algorithm;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.LinkedHashSet;

import model.Graph;

public class GreedyAlgorithm implements AbstractMDSAlgorithm {
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
		LinkedHashSet<Long> G = new LinkedHashSet<>(g.getVertices());
		HashMap<Long, LinkedHashSet<Long>> neigW = new HashMap<>();
		// HashMap<Long, LinkedHashSet<Long>> neig2 = new HashMap<>();
		for (Long v : W) {
			neigW.put(v, new LinkedHashSet<>(g.getN1(v)));
			// neig2.put(v, g.getNeighboursOfDistance2(v));
		}

		prepTime = bean.getCurrentThreadCpuTime() - start;
		LinkedHashSet<Long> S = new LinkedHashSet<>();
		while (!G.isEmpty()) {
			Long pick = maxByN1(W, neigW);
			// W.removeAll(g.getNeighboursOfVertexIncluded(pick));
			W.remove(pick);
			LinkedHashSet<Long> greying = new LinkedHashSet<>(neigW.get(pick));
			G.removeAll(greying);
			for (Long v : g.getN2(pick)/* neig2.get(pick) */) {
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