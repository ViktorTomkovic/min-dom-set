package algorithm.fomin;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import com.carrotsearch.hppc.IntOpenHashSet;

import datastructure.graph.Graph;
import algorithm.AbstractMDSAlgorithm;
import algorithm.AbstractMDSResult;
import algorithm.MDSResultBackedByIntOpenHashSet;
import algorithm.RepresentedSet;

public class AlgorithmFNaive implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;

	@Override
	public AbstractMDSResult mdsAlg(Graph g) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long start = bean.getCurrentThreadCpuTime();
		AlgorithmMSCFNaive fn = new AlgorithmMSCFNaive();
		ArrayList<RepresentedSet> sets = new ArrayList<>();
		for (Integer v : g.getVertices()) {
			sets.add(new RepresentedSet(v, g.getN1(v)));
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
