package algorithm;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import model.Graph;

public class NaiveAlgorithm implements AbstractMDSAlgorithm {
	private long runTime = -1L;

	@Override
	public LinkedHashSet<Long> mdsAlg(Graph g) {
		long start = System.currentTimeMillis();
		LinkedHashSet<Long> result = gms(g.getVertices(), new LinkedHashSet<Long>(), g);
		runTime = System.currentTimeMillis() - start;
		return result;
	}

	private LinkedHashSet<Long> gms(ArrayList<Long> choiceVertex,
			LinkedHashSet<Long> chosenVertices, Graph g) {
		if (choiceVertex.size() == 0) {
			// we have made our choice - let's compute it
			LinkedHashSet<Long> neighbours = new LinkedHashSet<>();
			for (Long vertex : chosenVertices) {
				neighbours.addAll(g.getNeighboursOfVertexIncluded(vertex));
			}
			/*
			 * for (Long l : chosenVertices) { System.out.print(l);
			 * System.out.print(" "); } System.out.print(" --> "); for (Long l :
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
			Long v = choiceVertex.get(choiceVertex.size() - 1);
			ArrayList<Long> ch = new ArrayList<>(choiceVertex);
			ch.remove(choiceVertex.size() - 1);

			// choose vertices
			// don't choose current
			LinkedHashSet<Long> set1 = gms(ch, chosenVertices, g);
			// choose current
			LinkedHashSet<Long> chV = new LinkedHashSet<>(chosenVertices);
			chV.add(v);
			LinkedHashSet<Long> set2 = gms(ch, chV, g);
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
