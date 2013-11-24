package algorithm;

import java.util.LinkedHashSet;

import model.Graph;

public class NaiveAlgorithm implements AbstractAlgorithm {

	@Override
	public LinkedHashSet<Long> mdsAlg(Graph g) {
		return gms(g.getNumberOfVertices(), new LinkedHashSet<Long>(), g);
	}

	private LinkedHashSet<Long> gms(Long choiceVertex,
			LinkedHashSet<Long> chosenVertices, Graph g) {
		if (choiceVertex == 0) {
			// we have made our choice - let's compute it
			LinkedHashSet<Long> neighbours = new LinkedHashSet<>();
			for (Long vertex : chosenVertices) {
				neighbours.addAll(g.getNeighboursOfVertexIncluded(vertex));
			}
			for (Long l : chosenVertices) {
				System.out.print(l);
				System.out.print(" ");
			}
			System.out.print(" --> ");
			for (Long l : neighbours) {
				System.out.print(l);
				System.out.print(" ");
			}
			if (neighbours.size() == g.getNumberOfVertices()) {
				System.out.println("*");
				return chosenVertices;
			} else {
				System.out.println("");
				return null;
			}
		} else {
			// choose vertices
			// don't choose current
			LinkedHashSet<Long> set1 = gms(choiceVertex - 1, chosenVertices, g);
			// choose current
			LinkedHashSet<Long> chV = new LinkedHashSet<>(chosenVertices);
			chV.add(choiceVertex);
			LinkedHashSet<Long> set2 = gms(choiceVertex - 1, chV, g);
			if (set1 == null)
				return set2;
			if ((set2 != null) && (set2.size() <= set1.size()))
				return set2;
			return set1;
		}
	}
}
