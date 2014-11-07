package algorithm.basic;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import algorithm.AbstractMDSAlgorithm;
import algorithm.AbstractMDSResult;
import algorithm.MDSResultBackedByIntOpenHashSet;

import com.carrotsearch.hppc.IntCollection;
import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.cursors.IntCursor;

import datastructure.graph.Graph;

// TODO prerobit na HPPC
public class GreedyAlgorithm implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;

	private static class ResultHolder {
		public int result;
		public int neighCount;
		public int iterations;
		public int skipped;
	}

	private ResultHolder maxByN1(IntCollection white,
			IntObjectOpenHashMap<IntOpenHashSet> neig, int oldMaxCount) {
		ResultHolder rh = new ResultHolder();
		int max = 0;
		int maxCount = 0;
		rh.skipped = 0;
		int iterations = 0;
		myloop: for (IntCursor current : white) {
			iterations = iterations + 1;
			int currentCount = neig.get(current.value).size();
			if (currentCount > maxCount) {
				max = current.value;
				maxCount = currentCount;
			}
			if (oldMaxCount == currentCount) {
				rh.skipped = 1;
				break myloop;
			}
		}
		rh.result = max;
		rh.neighCount = maxCount;
		rh.iterations = iterations;
		// if (white.size() - iterations > 10) {
		// System.out.println("("+iterations+","+(white.size()-iterations)+")");
		// }
		// System.out.print("("+oldMaxCount+","+rh.neighCount);
		if (rh.result == 0) {
			System.out.println("Zle je.");
		}
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
		// Collections.sort(vertices, new LessByN1NComparator(neigW));
		IntOpenHashSet W = new IntOpenHashSet(vertices);
		IntOpenHashSet G = new IntOpenHashSet(vertices);

		prepTime = bean.getCurrentThreadCpuTime() - start;
		int initialSize = (int) Math.ceil(g.getNumberOfVertices() * (1 / 0.65)) + 1;
		IntOpenHashSet S = new IntOpenHashSet(initialSize, 0.65f);
		int iterations = 0;
		Integer lastMaxCount = -1;
		int skipped = 0;
		// for (int i = 0; i < 30; i++) {
		// System.out.print("("+G.get(i)+","+g.getN1(G.get(i)).size()+")");
		// }
		// System.out.println();
		// Collections.sort(G, new LessByN1BComparator(g));
		// for (int i = 0; i < 30; i++) {
		// System.out.print("("+G.get(i)+","+g.getN1(G.get(i)).size()+")");
		// }
		// System.out.println();
		// return new LinkedHashSet<>();
		// ResultHolder rh = new ResultHolder();
		while (!G.isEmpty()) {
			iterations = iterations + 1;
			// Collections.sort(W, new LessByN1NComparator(neigW));
			ResultHolder rh = maxByN1(W, neigW, lastMaxCount);
			iterations = iterations + rh.iterations;
			lastMaxCount = rh.neighCount;
			Integer pick = rh.result;
			// System.out.println(pick + " " + G.size() + " " + W.size());
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