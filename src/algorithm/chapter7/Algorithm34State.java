package algorithm.chapter7;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import datastructure.graph.Graph;

public class Algorithm34State {
	public final Integer v;
	public LinkedHashSet<Integer> W;
	public Integer w = 0;
	public LinkedHashSet<Integer> dist2NotSorG;
	public LinkedHashMap<Integer, Integer> spans;
	public final Object canJoinLock = new Object();
	public Integer canJoin = 0;

	public Algorithm34State(Integer forV, Graph g) {
		this.v = forV;
		this.W = new LinkedHashSet<>(g.getN1(forV));
		this.dist2NotSorG = new LinkedHashSet<>(g.getN2(forV));
		this.spans = new LinkedHashMap<>();
	}

	public void recieveSpan(Integer from, Integer value) {
		synchronized (spans) {
			spans.put(from, value);
		}
		return;
	}

	public void recieveRemoveFromW(Integer v) {
		synchronized (W) {
			W.remove(v);
		}
		return;
	}

	public void recieveRemoveFromSpans(Integer v) {
		synchronized (spans) {
			spans.remove(v);
		}
		return;
	}

	public void recieveRemoveFromDist2(Integer v) {
		synchronized (dist2NotSorG) {
			dist2NotSorG.remove(v);
		}
	}

	public void takeABreakePlease() {
		synchronized (canJoinLock) {
			canJoin = canJoin + 1;
		}
		return;
	}

	public void thankYou() {
		synchronized (canJoinLock) {
			canJoin = canJoin - 1;
		}
		return;
	}
}
