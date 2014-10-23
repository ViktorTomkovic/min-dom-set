package algorithm.chapter7;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import model.Graph;
import algorithm.AbstractMDSAlgorithm;

public class Algorithm35OneThread implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;
	private LinkedHashMap<Long, Algorithm35State> allVertices = new LinkedHashMap<>();
	private LinkedHashSet<Algorithm35State> unfinishedVertices = new LinkedHashSet<>();
	private LinkedHashSet<Long> S = new LinkedHashSet<>();
	// public Object joinLock = new Object();

	private boolean hasWhiteNeighbours(Long v, Algorithm35State state) {
		boolean result;
		synchronized (state.W) {
			state.W.remove(v);
			result = !state.W.isEmpty();
			state.W.add(v);
		}
		return result;
	}

	private Long computeSpan(Algorithm35State state) {
		int w = 0;
		synchronized (state.W) {
			w = state.W.size();
		}
		Double a = Math.pow(2, Math.floor(Math.log(w) / Math.log(2)));
		long result = a.intValue();
		return result;
	}

	private boolean recievedFromAll(Algorithm35State state) {
		return state.spans.keySet().containsAll(state.dist2NotSorG);
	}

	private void joinS(Algorithm35State state,
			ArrayList<Algorithm35State> deleteThisRound) {
		for (Long v : state.dist2NotSorG) {
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

	private void joinG(Algorithm35State state,
			ArrayList<Algorithm35State> deleteThisRound) {
		for (Long v : state.dist2NotSorG) {
			allVertices.get(v).W.remove(state.v);
			if (!v.equals(state.v)) {
				allVertices.get(v).dist2NotSorG.remove(state.v);
			}
			allVertices.get(v).spans.remove(state.v);
		}
		deleteThisRound.add(state);
		return;
	}

	private long maxFromOthers(Algorithm35State state) {
		long max = 0L;
		for (Long l : state.spans.values()) {
			if (l > max) {
				max = l;
			}
		}
		return max;
	}

	private boolean joinTest(Algorithm35State state) {
		boolean b = state.isCandidate;
		long sum = 0L;
		for (Long v : state.W) {
			sum = sum + allVertices.get(v).c;
		}
		sum -= state.c;
		boolean c;
		c = (sum <= 3 * state.w);
		return b && c;
	}

	private Long computeC(Algorithm35State state) {
		long c = 0L;
		for (Long v : state.N) {
			Algorithm35State s = allVertices.get(v);
			if (s.isCandidate)
				c++;
		}
		return c;
	}

	@Override
	public LinkedHashSet<Long> mdsAlg(Graph g) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long start = bean.getCurrentThreadCpuTime();
		for (Long v : g.getVertices()) {
			Algorithm35State state = new Algorithm35State(v, g);
			unfinishedVertices.add(state);
			allVertices.put(v, state);
		}
		ArrayList<Algorithm35State> deleteThisRound = new ArrayList<>();
		prepTime = bean.getCurrentThreadCpuTime() - start;
		while (!unfinishedVertices.isEmpty()) {
			deleteThisRound.clear();
			for (Algorithm35State state : unfinishedVertices) {
				// algorithm!
				if (hasWhiteNeighbours(state.v, state)) {
					state.w = computeSpan(state);
					for (Long v2 : state.dist2NotSorG) {
						allVertices.get(v2).recieveSpan(state.v, state.w);
					}
					if (recievedFromAll(state)) {
						long maxOtherSpan = maxFromOthers(state);
						/*
						 * if (maxOtherSpan <= state.w) { state.isCandidate =
						 * true; }
						 */
						state.isCandidate = (maxOtherSpan <= state.w);
						state.c = computeC(state);
						if (joinTest(state)) {
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
		runTime = bean.getCurrentThreadCpuTime() - start;
		return S;
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
