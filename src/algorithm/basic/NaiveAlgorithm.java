package algorithm.basic;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import algorithm.AbstractMDSAlgorithm;
import algorithm.AbstractMDSResult;
import algorithm.MDSResultBackedByIntOpenHashSet;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.cursors.IntCursor;

import datastructure.graph.Graph;

public class NaiveAlgorithm implements AbstractMDSAlgorithm {
	private long runTime = -1L;

	@Override
	public AbstractMDSResult mdsAlg(Graph g) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long start = bean.getCurrentThreadCpuTime();
		IntOpenHashSet linkedResult = gms(new IntArrayList(g.getVertices()), new IntOpenHashSet(), g);
		runTime = bean.getCurrentThreadCpuTime() - start;
		MDSResultBackedByIntOpenHashSet result = new MDSResultBackedByIntOpenHashSet();
		result.setResult(linkedResult);
		return result;
	}

	private IntOpenHashSet gms(IntArrayList choiceVertex,
			IntOpenHashSet chosenVertices, Graph g) {
		if (choiceVertex.size() == 0) {
			// we have made our choice - let's compute it
			IntOpenHashSet neighbours = new IntOpenHashSet(chosenVertices.size() << 1);
			for (IntCursor vertex : chosenVertices) {
				neighbours.addAll(g.getN1(vertex.value));
			}
			/*
			 * for (Integer l : chosenVertices) { System.out.print(l);
			 * System.out.print(" "); } System.out.print(" --> "); for (Integer l :
			 * neighbours) { System.out.print(l); System.out.print(" "); }
			 */
			if (neighbours.size() == g.getNumberOfVertices()) {
				// System.out.println("*");
				return chosenVertices;
			} else {
				// System.out.println("");
				return null;
			}
		} else {
			int v = choiceVertex.get(choiceVertex.size() - 1);
			IntArrayList ch = new IntArrayList(choiceVertex);
			ch.remove(choiceVertex.size() - 1);

			// choose vertices
			// don't choose current
			IntOpenHashSet set1 = gms(ch, chosenVertices, g);
			// choose current
			IntOpenHashSet chV = new IntOpenHashSet(chosenVertices);
			chV.add(v);
			IntOpenHashSet set2 = gms(ch, chV, g);
			if (set1 == null)
				return set2;
			if ((set2 != null) && (set2.size() <= set1.size()))
				return set2;
			return set1;
		}
	}

	@Override
	public long getLastPrepTime() {
		return 0;
	}

	@Override
	public long getLastRunTime() {
		return runTime;
	}
}
