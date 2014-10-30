package algorithm.basic;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;

import model.Graph;
import algorithm.AbstractMDSAlgorithm;
import algorithm.GreaterByN1BComparator;
import algorithm.LessByN1BComparator;

public class GreedyAlgorithm implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;
	
	private static class ResultHolder {
		public Integer result;
		public Integer neighCount;
		public Integer iterations;
		public Integer skipped;
	}

	private ResultHolder maxByN1(LinkedHashSet<Integer> white,
			HashMap<Integer, LinkedHashSet<Integer>> neig, Integer oldMaxCount) {
		int max = 0;
		int maxCount = 0;
		ResultHolder rh = new ResultHolder();
		rh.skipped = 0;
		int iterations = 0;
		for (Integer current : white) {
			iterations = iterations + 1;
			int currentCount = neig.get(current).size();
			if (currentCount > maxCount) {
				max = current.intValue();
				maxCount = currentCount;
			}
			if (oldMaxCount.equals(currentCount)) {
				rh.skipped = 1;
				rh.result = max;
				rh.neighCount = maxCount;
				rh.iterations = iterations;
				return rh;
			}
		}
		rh.result = max;
		rh.neighCount = maxCount;
		rh.iterations = iterations;
		// System.out.print("("+oldMaxCount+","+rh.neighCount);
		return rh;
	}

	@Override
	public LinkedHashSet<Integer> mdsAlg(Graph g) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long start = bean.getCurrentThreadCpuTime();
		LinkedHashSet<Integer> W = new LinkedHashSet<>(g.getVertices());
		ArrayList<Integer> G = new ArrayList<>(g.getVertices());
		HashMap<Integer, LinkedHashSet<Integer>> neigW = new HashMap<>();
		for (Integer v : W) {
			neigW.put(v, new LinkedHashSet<>(g.getN1(v)));
		}

		prepTime = bean.getCurrentThreadCpuTime() - start;
		int initialSize = (int)Math.ceil(g.getNumberOfVertices() * (1/0.65)) + 1;
		LinkedHashSet<Integer> S = new LinkedHashSet<>(initialSize, 0.65f);
		int iterations = 0;
		Integer lastMaxCount = -1;
		int skipped = 0;
		Collections.sort(G, new GreaterByN1BComparator(g));
		for (Integer ach : G) {
			System.out.print("("+ach+","+g.getN1(ach).size()+")");
		}
		while (!G.isEmpty()) {
			iterations = iterations + 1;
			Collections.sort(G, new GreaterByN1BComparator(g));
			ResultHolder rh = maxByN1(W, neigW, lastMaxCount);
			iterations = iterations + rh.iterations;
			lastMaxCount = rh.neighCount;
			Integer pick = rh.result;
			skipped = skipped + rh.skipped;
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
		System.out.println("Skipped: " + skipped);
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