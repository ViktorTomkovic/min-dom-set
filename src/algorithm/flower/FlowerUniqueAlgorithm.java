package algorithm.flower;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;

import model.Graph;
import algorithm.AbstractMDSAlgorithm;

public class FlowerUniqueAlgorithm implements AbstractMDSAlgorithm {
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
		int initialSize = (int)Math.ceil(g.getNumberOfVertices() * (1/0.65)) + 1;

		HashMap<Integer, LinkedHashSet<Integer>> neigW = new HashMap<>(initialSize, 0.65f);
		for (Integer v : W) {
			neigW.put(v, new LinkedHashSet<>(g.getN1(v)));
		}

		prepTime = bean.getCurrentThreadCpuTime() - start;
		LinkedHashSet<Integer> S = new LinkedHashSet<>(initialSize, 0.65f);
		ArrayList<Integer> uniqueFlowers = new ArrayList<>();
		for (Integer v : W) {
			LinkedHashSet<Integer> neighs = new LinkedHashSet<>(neigW.get(v));
			if (neighs.size() == 2) {
				Iterator<Integer> it = neighs.iterator();
				while (it.hasNext()) {
					Integer vv = it.next();
					if (!vv.equals(v)) {
						uniqueFlowers.add(vv);
					}
				}
			}
		}
		System.out.println("Unique flowers: " + uniqueFlowers.size());
		for (Integer flower : uniqueFlowers) {
			W.remove(flower);
			LinkedHashSet<Integer> greying = new LinkedHashSet<>(neigW.get(flower));
			G.removeAll(greying);
			for (Integer v : g.getN2(flower)) {
				neigW.get(v).removeAll(greying);
			}
			S.add(flower);
		}
		while (!G.isEmpty()) {
			Integer pick = maxByN1(W, neigW);
			W.remove(pick);
			LinkedHashSet<Integer> greying = new LinkedHashSet<>(neigW.get(pick));
			G.removeAll(greying);
			for (Integer v : g.getN2(pick)) {
				neigW.get(v).removeAll(greying);
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
