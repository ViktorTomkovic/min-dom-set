package algorithm.chapter7;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.cursors.IntCursor;

import datastructure.graph.Graph;
import algorithm.AbstractMDSAlgorithm;
import algorithm.AbstractMDSResult;
import algorithm.MDSResultBackedByIntOpenHashSet;

// TODO prerobit na HPPC

public class Algorithm34OneThread implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;
	private LinkedHashMap<Integer, Algorithm34State> allVertices = new LinkedHashMap<>();
	private LinkedHashSet<Algorithm34State> unfinishedVertices = new LinkedHashSet<>();
	private LinkedHashSet<Integer> S = new LinkedHashSet<>();

	// public Object joinLock = new Object();

	private boolean hasWhiteNeighbours(Integer v, Algorithm34State state) {
		boolean result;
		// synchronized (state.W) {
		state.W.remove(v);
		result = !state.W.isEmpty();
		state.W.add(v);
		// }
		return result;
	}

	private Integer computeSpan(Algorithm34State state) {
		Integer w = Integer.valueOf(state.W.size());
		return w;
	}

	private boolean recievedFromAll(Algorithm34State state) {
		return state.spans.keySet().containsAll(state.dist2NotSorG);
	}

	private void joinS(Algorithm34State state,
			ArrayList<Algorithm34State> deleteThisRound) {
		for (Integer v : state.dist2NotSorG) {
			allVertices.get(v).W.remove(state.v);
			if (!v.equals(state.v)) {
				allVertices.get(v).dist2NotSorG.remove(state.v);
			}
			allVertices.get(v).spans.remove(state.v);
		}
		S.add(state.v);
		deleteThisRound.add(state);
		return;
	}

	private void joinG(Algorithm34State state,
			ArrayList<Algorithm34State> deleteThisRound) {
		for (Integer v : state.dist2NotSorG) {
			allVertices.get(v).W.remove(state.v);
			if (!v.equals(state.v)) {
				allVertices.get(v).dist2NotSorG.remove(state.v);
			}
			allVertices.get(v).spans.remove(state.v);
		}
		deleteThisRound.add(state);
		return;
	}

	@Override
	public AbstractMDSResult mdsAlg(Graph g) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long start = bean.getCurrentThreadCpuTime();
		for (IntCursor v : g.getVertices()) {
			Algorithm34State state = new Algorithm34State(v.value, g);
			unfinishedVertices.add(state);
			allVertices.put(v.value, state);
		}
		ArrayList<Algorithm34State> deleteThisRound = new ArrayList<>();
		prepTime = bean.getCurrentThreadCpuTime() - start;
		while (!unfinishedVertices.isEmpty()) {
			deleteThisRound.clear();
			for (Algorithm34State state : unfinishedVertices) {
				state.w = computeSpan(state);
				for (Integer v2 : state.dist2NotSorG) {
					allVertices.get(v2).recieveSpan(state.v, state.w);
				}
			}
			for (Algorithm34State state : unfinishedVertices) {
				// algorithm!
				if (hasWhiteNeighbours(state.v, state)) {
					if (recievedFromAll(state)) {
						boolean isBiggest = true;
						for (Integer v : state.dist2NotSorG) {
							Integer getV = state.spans.get(v);
							if (getV > state.w
									|| (getV.equals(state.w) && (state.v > v))) {
								isBiggest = false;
							}
						}
						if (isBiggest) {
							joinS(state, deleteThisRound);
							// System.out.println("koniec+ " + state.v);
						}
						// state.spans.clear();
					}
				} else {
					joinG(state, deleteThisRound);
					// System.out.println("koniec- " + state.v);
				}
			}
			unfinishedVertices.removeAll(deleteThisRound);
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
