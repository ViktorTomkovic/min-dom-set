package algorithm.basic;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.cursors.IntCursor;

import algorithm.AbstractMDSAlgorithm;
import algorithm.AbstractMDSResult;
import algorithm.MDSResultBackedByIntOpenHashSet;
import datastructure.graph.Graph;

// TODO prerobit na HPPC
public class GreedyAlgorithm implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;

	private static class ResultHolder {
		public Integer result;
		public Integer neighCount;
		public Integer iterations;
		public Integer skipped;
	}

	private ResultHolder maxByN1(ArrayList<Integer> white,
			HashMap<Integer, LinkedHashSet<Integer>> neig, Integer oldMaxCount) {
		ResultHolder rh = new ResultHolder();
		int max = 0;
		int maxCount = 0;
		rh.skipped = 0;
		int iterations = 0;
		myloop: for (Integer current : white) {
			iterations = iterations + 1;
			int currentCount = neig.get(current).size();
			if (currentCount > maxCount) {
				max = current.intValue();
				maxCount = currentCount;
			}
			if (oldMaxCount.equals(currentCount)) {
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
		return rh;
	}

	@Override
	public AbstractMDSResult mdsAlg(Graph g) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long start = bean.getCurrentThreadCpuTime();
		ArrayList<Integer> vertices = new ArrayList<>();
		for (IntCursor vertex : g.getVertices()) {
			vertices.add(vertex.value);
		}
		HashMap<Integer, LinkedHashSet<Integer>> neigW = new HashMap<>();
		for (Integer v : vertices) {
			LinkedHashSet<Integer> neighs = new LinkedHashSet<>();
			for (IntCursor intcur : g.getN1(v)) {
				neighs.add(intcur.value);
			}
			neigW.put(v, neighs);
		}
		// Collections.sort(vertices, new LessByN1NComparator(neigW));
		ArrayList<Integer> W = new ArrayList<>(vertices);
		ArrayList<Integer> G = new ArrayList<>(vertices);

		prepTime = bean.getCurrentThreadCpuTime() - start;
		int initialSize = (int) Math.ceil(g.getNumberOfVertices() * (1 / 0.65)) + 1;
		LinkedHashSet<Integer> S = new LinkedHashSet<>(initialSize, 0.65f);
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
			skipped = skipped + rh.skipped;
			W.remove(pick);
			LinkedHashSet<Integer> greying = new LinkedHashSet<>(
					neigW.get(pick));
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