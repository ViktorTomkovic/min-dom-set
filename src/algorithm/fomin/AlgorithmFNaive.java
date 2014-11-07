package algorithm.fomin;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.cursors.IntCursor;

import datastructure.graph.Graph;
import algorithm.AbstractMDSAlgorithm;
import algorithm.AbstractMDSResult;
import algorithm.MDSResultBackedByIntOpenHashSet;
import algorithm.RepresentedSet;

// TODO prerobit na HPPC

public class AlgorithmFNaive implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;

	@Override
	public AbstractMDSResult mdsAlg(Graph g) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long start = bean.getCurrentThreadCpuTime();
		AlgorithmMSCFNaive fn = new AlgorithmMSCFNaive();
		ArrayList<RepresentedSet> sets = new ArrayList<>();
		for (IntCursor v : g.getVertices()) {
			LinkedHashSet<Integer> neighs = new LinkedHashSet<>();
			for (IntCursor intcur : g.getN1(v.value)) {
				neighs.add(intcur.value);
			}
			sets.add(new RepresentedSet(v.value, neighs));
		}
		prepTime = bean.getCurrentThreadCpuTime() - start;
		LinkedHashSet<Integer> linkedResult = fn.getMSCforMDS(null, sets, g);
		runTime = bean.getCurrentThreadCpuTime() - start;
		MDSResultBackedByIntOpenHashSet result = new MDSResultBackedByIntOpenHashSet();
		IntOpenHashSet resultData = new IntOpenHashSet(linkedResult.size());
		for (Integer i : linkedResult) {
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
