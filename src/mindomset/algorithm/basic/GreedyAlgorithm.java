package mindomset.algorithm.basic;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import mindomset.algorithm.AbstractMDSAlgorithm;
import mindomset.algorithm.AbstractMDSResult;
import mindomset.algorithm.MDSResultBackedByIntOpenHashSet;
import mindomset.datastructure.graph.Graph;

import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.cursors.IntCursor;

public class GreedyAlgorithm implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;

	private static class ResultHolder {
		public int result;
		public int neighCount;
		public int iterations;
		public int skipped;
	}

	private ResultHolder maxByN1(IntOpenHashSet white,
			IntObjectOpenHashMap<IntOpenHashSet> neig, int oldMaxCount) {
		ResultHolder rh = new ResultHolder();
		int max = 0;
		int maxCount = 0;
		rh.skipped = 0;
		int iterations = 0;
		for (IntCursor current : white) {
			iterations = iterations + 1;
			int currentCount = neig.get(current.value).size();
			if (currentCount > maxCount) {
				max = current.value;
				maxCount = currentCount;
			}
			if (oldMaxCount == currentCount) {
				rh.skipped = 1;
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
		IntOpenHashSet vertices = new IntOpenHashSet(g.getVertices());
		IntObjectOpenHashMap<IntOpenHashSet> neigW = new IntObjectOpenHashMap<>(
				vertices.size());
		for (IntCursor v : vertices) {
			neigW.put(v.value, new IntOpenHashSet(g.getN1(v.value)));
		}
		IntOpenHashSet W = new IntOpenHashSet(vertices);
		IntOpenHashSet G = new IntOpenHashSet(vertices);

		prepTime = bean.getCurrentThreadCpuTime() - start;
		int initialSize = (int) Math.ceil(g.getNumberOfVertices() * (1 / 0.65)) + 1;
		IntOpenHashSet S = new IntOpenHashSet(initialSize, 0.65f);
		int iterations = 0;
		int lastMaxCount = -1;
		int skipped = 0;
		while (!G.isEmpty()) {
			iterations = iterations + 1;
			ResultHolder rh = maxByN1(W, neigW, lastMaxCount);
			iterations = iterations + rh.iterations;
			lastMaxCount = rh.neighCount;
			int pick = rh.result;
			skipped = skipped + rh.skipped;
			W.remove(pick);
			IntOpenHashSet greying = new IntOpenHashSet(neigW.get(pick));
			G.removeAll(greying);
			for (IntCursor v : g.getN2(pick)) {
				neigW.get(v.value).removeAll(greying);
			}
			S.add(pick);
		}
		runTime = bean.getCurrentThreadCpuTime() - start;
		System.out.println("Number of iterations: " + iterations);
		System.out.println("Skipped: " + skipped);
		MDSResultBackedByIntOpenHashSet result = new MDSResultBackedByIntOpenHashSet();
		result.setResult(S);
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