package algorithm;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import model.Graph;

public class NaiveAlgorithm implements AbstractMDSAlgorithm {
	private long runTime = -1L;

	@Override
	public LinkedHashSet<Integer> mdsAlg(Graph g) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long start = bean.getCurrentThreadCpuTime();
		LinkedHashSet<Integer> result = gms(new ArrayList<>(g.getVertices()), new LinkedHashSet<Integer>(), g);
		runTime = bean.getCurrentThreadCpuTime() - start;
		return result;
	}

	private LinkedHashSet<Integer> gms(ArrayList<Integer> choiceVertex,
			LinkedHashSet<Integer> chosenVertices, Graph g) {
		if (choiceVertex.size() == 0) {
			// we have made our choice - let's compute it
			LinkedHashSet<Integer> neighbours = new LinkedHashSet<>();
			for (Integer vertex : chosenVertices) {
				neighbours.addAll(g.getN1(vertex));
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
			Integer v = choiceVertex.get(choiceVertex.size() - 1);
			ArrayList<Integer> ch = new ArrayList<>(choiceVertex);
			ch.remove(choiceVertex.size() - 1);

			// choose vertices
			// don't choose current
			LinkedHashSet<Integer> set1 = gms(ch, chosenVertices, g);
			// choose current
			LinkedHashSet<Integer> chV = new LinkedHashSet<>(chosenVertices);
			chV.add(v);
			LinkedHashSet<Integer> set2 = gms(ch, chV, g);
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
