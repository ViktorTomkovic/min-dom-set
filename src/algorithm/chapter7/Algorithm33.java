package algorithm.chapter7;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import model.Graph;
import algorithm.AbstractAlgorithm;

public class Algorithm33 implements AbstractAlgorithm {

	Long w(Long v, Graph g, ArrayList<Long> W) {
		Long result = 0L;
		Set<Long> A = g.getNeighboursOf(v);
		A.retainAll(W);
		result = (long) A.size();
		return result;
	}
	
	@Override
	public Set<Long> mdsAlg(Graph g) {
		ArrayList<Long> W = new ArrayList<>(g.getVertices());
		HashSet<Long> S = new HashSet<>();
		while (!W.isEmpty()) {
			Long v = W.get(0);
			Long mv = w(v, g, W);
			for (Long v2 : W) {
				Long mv2 = w(v2, g, W);
				if (mv2 > mv) {
					v = v2;
					mv = mv2;
				}
			}
			S.add(v);
			W.removeAll(g.getNeighboursOfVertexIncluded(v));
		}
		return S;
	}

}
