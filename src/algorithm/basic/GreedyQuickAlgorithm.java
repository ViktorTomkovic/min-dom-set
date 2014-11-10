package algorithm.basic;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;

import algorithm.AbstractMDSAlgorithm;
import algorithm.AbstractMDSResult;
import algorithm.LessByN1AComparator;
import algorithm.MDSResultBackedByIntOpenHashSet;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntOpenHashSet;

import datastructure.graph.Graph;

public class GreedyQuickAlgorithm implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;

	@Override
	public AbstractMDSResult mdsAlg(Graph g) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long start = bean.getCurrentThreadCpuTime();
		IntArrayList W = new IntArrayList(g.getVertices());
		prepTime = bean.getCurrentThreadCpuTime() - start;
		Integer[] sorted = new Integer[W.size()];
		for (int i = 0; i < W.size(); i++) {
			sorted[i] = Integer.valueOf(W.buffer[i]);
		}
		Arrays.sort(sorted, new LessByN1AComparator(g));
		for (int i = 0; i < W.size(); i++) {
			W.buffer[i] = sorted[i].intValue();
		}
		IntOpenHashSet S = new IntOpenHashSet(W.size() / 10);
		while (!W.isEmpty()) {
			int pick = W.get(W.size() - 1);
			W.removeAll(g.getN1(pick));
			S.add(pick);
		}
		runTime = bean.getCurrentThreadCpuTime() - start;
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
