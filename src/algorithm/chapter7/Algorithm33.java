package algorithm.chapter7;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.LinkedHashSet;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.cursors.IntCursor;

import datastructure.graph.Graph;
import algorithm.AbstractMDSAlgorithm;
import algorithm.AbstractMDSResult;
import algorithm.MDSResultBackedByIntOpenHashSet;

// TODO prerobit na HPPC

public class Algorithm33 implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;

	Integer maxByN1(LinkedHashSet<Integer> white,
			HashMap<Integer, LinkedHashSet<Integer>> neig) {
		int m = 0;
		int mc = 0;
		for (Integer c : white) {
			int cc = neig.get(c).size();
			if (cc > mc) {
				m = c.intValue();
				mc = cc;
			}
		}
		return m;
	}

	@Override
	public AbstractMDSResult mdsAlg(Graph g) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long start = bean.getCurrentThreadCpuTime();
		LinkedHashSet<Integer> W = new LinkedHashSet<>();
		for (IntCursor intcur : g.getVertices()) {
			W.add(intcur.value);
		}
		HashMap<Integer, LinkedHashSet<Integer>> neig = new HashMap<>();
		for (Integer v : W) {
			LinkedHashSet<Integer> neighs = new LinkedHashSet<>();
			for (IntCursor intcur : g.getN1(v)) {
				neighs.add(intcur.value);
			}
			neig.put(v, neighs);
		}

		prepTime = bean.getCurrentThreadCpuTime() - start;
		// Collections.sort(W);
		LinkedHashSet<Integer> S = new LinkedHashSet<>();
		while (!W.isEmpty()) {
			Integer pick = maxByN1(W, neig);
			// W.removeAll(g.getNeighboursOfVertexIncluded(pick));
			LinkedHashSet<Integer> neighs = neig.get(pick);
			W.removeAll(neighs);
			for (Integer v : neighs) {
				if (v.equals(pick)) {continue;}
				neig.get(v).remove(pick);
			}
			neig.get(pick).clear();

			/*
			 * Integer mv = w(v, g, W); for (Integer v2 : W) { Integer mv2 = w(v2, g, W);
			 * if (mv2 > mv) { v = v2; mv = mv2; } }
			 */
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
