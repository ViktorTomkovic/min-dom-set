package algorithm.chapter7;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import model.Graph;

public class Algorithm34State {
	public final Long v;
	public LinkedHashSet<Long> W;
	public Long w = 0L;
	public LinkedHashSet<Long> dist2NotSorG;
	public LinkedHashMap<Long, Long> spans;
	public final Object canJoinLock = new Object();
	public Long canJoin = 0L;

	public Algorithm34State(Long forV, Graph g) {
		this.v = forV;
		this.W = new LinkedHashSet<>(g.getN1(forV));
		this.dist2NotSorG = new LinkedHashSet<>(g.getN2(forV));
		this.spans = new LinkedHashMap<>();
	}

	public void recieveSpan(Long from, Long value) {
		synchronized (spans) {
			spans.put(from, value);
		}
		return;
	}

	public void recieveRemoveFromW(Long v) {
		synchronized (W) {
			W.remove(v);
		}
		return;
	}

	public void recieveRemoveFromSpans(Long v) {
		synchronized (spans) {
			spans.remove(v);
		}
		return;
	}

	public void recieveRemoveFromDist2(Long v) {
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
