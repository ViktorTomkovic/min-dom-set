package algorithm.fomin;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import model.Graph;
import algorithm.AbstractMDSAlgorithm;
import algorithm.RepresentedSet;

public class AlgorithmFProper implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;

	@Override
	public LinkedHashSet<Integer> mdsAlg(Graph g) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long start = bean.getCurrentThreadCpuTime();
		AlgorithmMSCFProper fn = new AlgorithmMSCFProper();
		ArrayList<RepresentedSet> sets = new ArrayList<>();
		for (Integer v : g.getVertices()) {
			sets.add(new RepresentedSet(v, g.getN1(v)));
		}
		prepTime = bean.getCurrentThreadCpuTime() - start;
		LinkedHashSet<Integer> result = new LinkedHashSet<>(fn.getMSCforMDS(null,
				sets, g));
		runTime = bean.getCurrentThreadCpuTime() - start;
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
