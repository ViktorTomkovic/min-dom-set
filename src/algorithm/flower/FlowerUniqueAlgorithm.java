package algorithm.flower;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.cursors.IntCursor;

import datastructure.graph.Graph;
import algorithm.AbstractMDSAlgorithm;
import algorithm.AbstractMDSResult;
import algorithm.MDSResultBackedByIntOpenHashSet;

// TODO prerobit na HPPC

public class FlowerUniqueAlgorithm implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;

	private static class ResultHolder {
		public Integer result;
		public Integer iterations;
		public Integer neighCount;
	}

	private ResultHolder maxByN1(LinkedHashSet<Integer> white,
			HashMap<Integer, LinkedHashSet<Integer>> neig, Integer oldMax) {
		int max = 0;
		int maxCount = 0;
		ResultHolder rh = new ResultHolder();
		int iterations = 0;
		for (Integer current : white) {
			iterations = iterations + 1;
			int currentCount = neig.get(current).size();
			if (currentCount > maxCount) {
				max = current.intValue();
				maxCount = currentCount;
			}
			if (oldMax.equals(currentCount)) {
				break;
			}
		}
		rh.result = max;
		rh.neighCount = maxCount;
		rh.iterations = iterations;
		return rh;
	}

	@Override
	public AbstractMDSResult mdsAlg(Graph g) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long start = bean.getCurrentThreadCpuTime();
		LinkedHashSet<Integer> W = new LinkedHashSet<>();
		LinkedHashSet<Integer> G = new LinkedHashSet<>();
		for (IntCursor intcur : g.getVertices()) {
			W.add(intcur.value);
			W.add(intcur.value);
		}
		int initialSize = (int)Math.ceil(g.getNumberOfVertices() * (1/0.65)) + 1;

		HashMap<Integer, LinkedHashSet<Integer>> neigW = new HashMap<>(initialSize, 0.65f);
		for (Integer v : W) {
			LinkedHashSet<Integer> neighs = new LinkedHashSet<>();
			for (IntCursor intcur : g.getN1(v)) {
				neighs.add(intcur.value);
			}
			neigW.put(v, neighs);
		}

		prepTime = bean.getCurrentThreadCpuTime() - start;
		LinkedHashSet<Integer> S = new LinkedHashSet<>(initialSize, 0.65f);
		int iterations = 0;
		ArrayList<Integer> uniqueFlowers = new ArrayList<>();
		for (Integer v : W) {
			iterations = iterations + 1;
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
			iterations = iterations + 1;
			W.remove(flower);
			LinkedHashSet<Integer> greying = new LinkedHashSet<>(neigW.get(flower));
			G.removeAll(greying);
			for (IntCursor v : g.getN2(flower)) {
				neigW.get(v.value).removeAll(greying);
			}
			S.add(flower);
		}
		Integer lastMax = -1;
		while (!G.isEmpty()) {
			iterations = iterations + 1;
			ResultHolder rh = maxByN1(W, neigW, lastMax);
			iterations = iterations + rh.iterations;
			lastMax = rh.neighCount;
			Integer pick = rh.result;
			W.remove(pick);
			LinkedHashSet<Integer> greying = new LinkedHashSet<>(neigW.get(pick));
			G.removeAll(greying);
			for (IntCursor v : g.getN2(pick)) {
				neigW.get(v.value).removeAll(greying);
			}
			S.add(pick);
		}
		runTime = bean.getCurrentThreadCpuTime() - start;
		System.out.println("Number of iterations: " + iterations);
		MDSResultBackedByIntOpenHashSet result = new MDSResultBackedByIntOpenHashSet();
		IntOpenHashSet resultData = new IntOpenHashSet(S.size());
		for (Integer i : S) {
			resultData.add(i);
		}
		result.setResult(resultData);
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
