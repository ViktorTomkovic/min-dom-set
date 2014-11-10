package mindomset.algorithm.fomin;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import mindomset.algorithm.AbstractMDSAlgorithm;
import mindomset.algorithm.AbstractMDSResult;
import mindomset.algorithm.MDSResultBackedByIntOpenHashSet;
import mindomset.algorithm.RepresentedSet;
import mindomset.datastructure.graph.Graph;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.cursors.IntCursor;

public class AlgorithmFNaive implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;

	@Override
	public AbstractMDSResult mdsAlg(Graph g) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long start = bean.getCurrentThreadCpuTime();
		AlgorithmMSCFNaive fn = new AlgorithmMSCFNaive();
		ObjectArrayList<RepresentedSet> sets = new ObjectArrayList<>(g.getNumberOfVertices());
		for (IntCursor v : g.getVertices()) {
			IntOpenHashSet neighs = new IntOpenHashSet(g.getN1(v.value));
			sets.add(new RepresentedSet(v.value, neighs));
		}
		prepTime = bean.getCurrentThreadCpuTime() - start;
		IntOpenHashSet linkedResult = fn.getMSCforMDS(null, sets, g);
		runTime = bean.getCurrentThreadCpuTime() - start;
		MDSResultBackedByIntOpenHashSet result = new MDSResultBackedByIntOpenHashSet();
		result.setResult(linkedResult);
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
