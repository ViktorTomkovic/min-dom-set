package algorithm.chapter7;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import model.Graph;
import algorithm.AbstractMDSAlgorithm;

public class Algorithm34OneThread implements AbstractMDSAlgorithm {
	private LinkedHashMap<Long, Algorithm34State> allVertices = new LinkedHashMap<>();
	private LinkedHashSet<Algorithm34State> unfinishedVertices = new LinkedHashSet<>();
	private LinkedHashSet<Long> S = new LinkedHashSet<>();
	public Object joinLock = new Object();

	private boolean hasWhiteNeighbours(Long v, Algorithm34State state) {
		boolean result;
		synchronized (state.W) {
			state.W.remove(v);
			result = !state.W.isEmpty();
			state.W.add(v);
		}
		return result;
	}

	private Long computeSpan(Algorithm34State state) {
		Long w = new Long(state.W.size());
		return w;
	}

	private boolean recievedFromAll(Algorithm34State state) {
		return state.spans.keySet().containsAll(state.dist2NotSorG);
	}

	private void joinS(Algorithm34State state,
			ArrayList<Algorithm34State> deleteThisRound) {
		for (Long v : state.dist2NotSorG) {
			Algorithm34State s = allVertices.get(v);
			allVertices.get(v).recieveRemoveFromW(state.v);
			if (v != state.v)
				allVertices.get(v).recieveRemoveFromDist2(state.v);
			s.spans.remove(state.v);
		}
		S.add(state.v);
		deleteThisRound.add(state);
		return;
	}

	private void joinG(Algorithm34State state,
			ArrayList<Algorithm34State> deleteThisRound) {
		for (Long v : state.dist2NotSorG) {
			allVertices.get(v).recieveRemoveFromW(state.v);
			if (v != state.v)
				allVertices.get(v).recieveRemoveFromDist2(state.v);
			allVertices.get(v).spans.remove(state.v);
		}
		deleteThisRound.add(state);
		return;
	}

	@Override
	public LinkedHashSet<Long> mdsAlg(Graph g) {
		Long nv = g.getNumberOfVertices();
		System.out.println(nv);
		for (Long v : g.getVertices()) {
			Algorithm34State state = new Algorithm34State(v, g);
			unfinishedVertices.add(state);
			allVertices.put(v, state);
		}

		while (!unfinishedVertices.isEmpty()) {
			/*
			 * LinkedHashSet<Algorithm34State> helper = new LinkedHashSet<>
			 * unfinishedVertices);
			 */
			ArrayList<Algorithm34State> deleteThisRound = new ArrayList<>();

			for (Algorithm34State state : unfinishedVertices) {
				// algorithm!
				if (hasWhiteNeighbours(state.v, state)) {
					state.w = computeSpan(state);
					for (Long v2 : state.dist2NotSorG) {
						allVertices.get(v2).recieveSpan(state.v, state.w);
					}
					if (recievedFromAll(state)) {
						boolean isBiggest = true;
						for (Long v : state.dist2NotSorG) {
							if (state.spans.get(v) > state.w || ((state.spans.get(v) == state.w) && (state.v > v))) {
								isBiggest = false;
							}
						}
						if (isBiggest) {
							joinS(state, deleteThisRound);
							// System.out.println("koniec+ " + state.v);
						}
						state.spans.clear();
					}
				} else {
					joinG(state, deleteThisRound);
					// System.out.println("koniec- " + state.v);
				}
			}

			unfinishedVertices.removeAll(deleteThisRound);
		}

		return S;
	}

}
