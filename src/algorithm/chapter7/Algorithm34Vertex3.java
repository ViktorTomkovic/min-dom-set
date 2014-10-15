package algorithm.chapter7;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import model.Graph;

public class Algorithm34Vertex3 extends Thread {
	private Graph g;
	private LinkedHashSet<Long> W;
	private Set<Long> S;
	private Set<Long> G;
	private Long v;
	private Object lock;
	private Object waitingForSpans = new Object();
	private Map<Long, Long> spans;
	private LinkedHashMap<Long, Algorithm34Vertex3> instances;
	private boolean canJoin = true;
	private LinkedHashSet<Long> dist2notSorG;

	public Algorithm34Vertex3(Graph g, Long v, Set<Long> S, Set<Long> G,
			LinkedHashMap<Long, Algorithm34Vertex3> instances, Object lock) {
		this.g = g;
		this.v = v;
		this.S = S;
		this.G = G;
		this.instances = instances;
		this.W = new LinkedHashSet<>(g.getN1(v));
		this.spans = new LinkedHashMap<>();
		this.dist2notSorG = new LinkedHashSet<>(g.getN2(v));
		this.lock = lock;
	}

	private boolean hasWhiteNeighbours(Long v) {
		Set<Long> A;
		synchronized (W) {
			A = new LinkedHashSet<>(W);
		}
		A.remove(v);
		return !A.isEmpty();
	}

	private Long computeSpan(Long v) {
		Long result = 0L;
		synchronized (W) {
			result = (long) W.size();
		}
		return result;
	}

	public void recieveSpan(Long from, Long span) {
		synchronized (spans) {
			spans.put(from, span);
			// System.out.println("span recieved from " + from + " at " + v +
			// " value " + span);
		}
		synchronized (waitingForSpans) {
			waitingForSpans.notifyAll();
		}
	}

	public void recieveRemoveFromW(Long from) {
		synchronized (W) {
			W.remove(from);
			// System.out.println("W remove recieved from " + from + " at " +
			// v);
		}
	}

	public void recieveJoin(Long from) {
		synchronized (dist2notSorG) {
			dist2notSorG.remove(from);
		}
	}

	private void joinS(Long v, Graph g) {
		System.out.print(v + " <-- ");
		System.out.print(dist2notSorG);
		for (Long v2 : dist2notSorG) {
			instances.get(v2).takeABreakPlease();
			// System.out.println(instances.get(v2).getW());
		}
		for (Long v2 : dist2notSorG) {
			Algorithm34Vertex3 alg = instances.get(v2);
			alg.recieveRemoveFromW(v);
			// alg.recieveSpan(v, 0L);
		}
		dist2notSorG.remove(v);
		synchronized (lock) {
			// System.out.println("S += " + v);
			S.add(v);
		}
		System.out.println(" --> " + v);
		for (Long v2 : dist2notSorG) {
			instances.get(v2).thankYou();
		}
		return;
	}

	private void joinG(Long v2) {
		synchronized (G) {
			G.add(v2);
		}
		return;
	}

	public void thankYou() {
		// System.out.print(v + " resumed. ");
		this.setCanJoin(true);
	}

	public void takeABreakPlease() {
		// System.out.print(v + " is taking a break. ");
		this.setCanJoin(false);
	}

	public Set<Long> getW() {
		return W;
	}

	@Override
	public void run() {
		while (true) {
			synchronized (lock) {
				dist2notSorG.removeAll(S);
				dist2notSorG.removeAll(G);
			}
			if (hasWhiteNeighbours(v)) {
				Long w = computeSpan(v); // span

				for (Long v2 : dist2notSorG) {
					instances.get(v2).recieveSpan(v, w);
				}

				synchronized (waitingForSpans) {
					while (!spans.keySet().containsAll(dist2notSorG)) {
						synchronized (lock) {
							dist2notSorG.removeAll(S);
							dist2notSorG.removeAll(G);
						}
						try {
							waitingForSpans.wait(0, 50000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				// if (spans.keySet().containsAll(dist2notSorG)) {
				LinkedHashMap<Long, Long> oldspans = new LinkedHashMap<>();
				oldspans.putAll(spans);
				w = computeSpan(v);
				boolean isBiggest = true;
				for (Long v2 : dist2notSorG) {
					if ((spans.get(v2) > w)
							|| ((spans.get(v2) == w) && (v2 < v))) {
						isBiggest = false;
					}
				}
				if (isBiggest && hasWhiteNeighbours(v)) {// && canJoin) {
					joinS(v, g);
					System.out.println("koniec+ " + v);
					return;
				}
				spans.clear();
				// }
			} else {
				joinG(v);
				System.out.println("koniec- " + v);
				return;
			}
		}
	}

	public boolean isCanJoin() {
		return canJoin;
	}

	public void setCanJoin(boolean canJoin) {
		this.canJoin = canJoin;
	}
}
