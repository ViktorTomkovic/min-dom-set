package algorithm;

import java.util.HashSet;
import java.util.Set;

import model.Graph;

public class NaiveAlgorithm implements AbstractAlgorithm {

	@Override
	public Set<Long> mdsAlg(Graph g) {
		return gms(g.getNumberOfVertices(), new HashSet<Long>(), g);
	}

	private Set<Long> gms(Long choiceVertex, Set<Long> chosenVertices, Graph g) {
		if (choiceVertex == 0) {
			// we have made our choice - let's compute it
			Set<Long> neighbours = new HashSet<>();
			for (Long vertex : chosenVertices) {
				neighbours.addAll(g.getNeighboursOf2(vertex));
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
			Set<Long> set1 = gms(choiceVertex - 1, chosenVertices, g);
			// choose current
			Set<Long> chV = new HashSet<>(chosenVertices);
			chV.add(choiceVertex);
			Set<Long> set2 = gms(choiceVertex - 1, chV, g);
			if (set1 == null)
				return set2;
			if ((set2 != null) && (set2.size() <= set1.size()))
				return set2;
			return set1;
		}
	}
}
