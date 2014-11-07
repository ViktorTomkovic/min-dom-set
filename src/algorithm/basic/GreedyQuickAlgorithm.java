package algorithm.basic;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.cursors.IntCursor;

import datastructure.graph.Graph;
import algorithm.AbstractMDSAlgorithm;
import algorithm.AbstractMDSResult;
import algorithm.LessByN1AComparator;
import algorithm.MDSResultBackedByIntOpenHashSet;

public class GreedyQuickAlgorithm implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;

	// TODO prerobit na HPPC

	@Override
	public AbstractMDSResult mdsAlg(Graph g) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long start = bean.getCurrentThreadCpuTime();
		ArrayList<Integer> W = new ArrayList<>();
		for (IntCursor intcur : g.getVertices()) {
			W.add(intcur.value);
		}
		prepTime = bean.getCurrentThreadCpuTime() - start;
		Collections.sort(W, new LessByN1AComparator(g));
		LinkedHashSet<Integer> S = new LinkedHashSet<>();
		while (!W.isEmpty()) {
			Integer pick = W.get(W.size() - 1);
			for (IntCursor intcur : g.getN1(pick)) {
				W.remove(Integer.valueOf(intcur.value));
			}
			S.add(pick);
		}
		runTime = bean.getCurrentThreadCpuTime() - start;
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
