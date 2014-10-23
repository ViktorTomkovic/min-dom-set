package algorithm.chapter7;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import model.Graph;

public class Algorithm34Vertex extends Thread {
	private Graph g;
	private LinkedHashSet<Long> W;
	private Set<Long> S;
	private Set<Long> G;
	private Long v;
	private Object lock;
	private Object waitingForSpans = new Object();
	private Map<Long, Long> spans;
	private LinkedHashMap<Long, Algorithm34Vertex> instances;
	private final Object canJoinLock = new Object();
	private Long canJoin = 0L; // mutex - how many unfinished joins are in
								// neighbourhood
	private LinkedHashSet<Long> dist2notSorG;
	private final Object wLock = new Object();
	private Long w;

	public Algorithm34Vertex(Graph g, Long v, Set<Long> S, Set<Long> G,
			LinkedHashMap<Long, Algorithm34Vertex> instances, Object lock) {
		this.g = g;
		this.v = v;
		this.S = S;
		this.G = G;
		this.instances = instances;
		this.W = new LinkedHashSet<>(g.getN1(v));
		this.spans = new LinkedHashMap<>();
		this.dist2notSorG = new LinkedHashSet<>(g.getN2(v));
		this.lock = lock;
		this.w = computeSpan(v);
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
		Long result;
		synchronized (W) {
			result = Long.valueOf(W.size());
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
		synchronized (wLock) {
			w = computeSpan(this.v);
		}
	}

	public void recieveJoinS(Long from) {
		synchronized (dist2notSorG) {
			dist2notSorG.remove(from);
		}
	}

	public void recieveJoinG(Long from) {
		synchronized (dist2notSorG) {
			dist2notSorG.remove(from);
		}
	}

	private void joinS(Long v, Graph g) {
		synchronized (dist2notSorG) {
			System.out.print(v + " <-- ");
			System.out.print(dist2notSorG);
			for (Long v2 : dist2notSorG) {
				instances.get(v2).takeABreakPlease();
				// System.out.println(instances.get(v2).getW());
			}
			for (Long v2 : dist2notSorG) {
				Algorithm34Vertex alg = instances.get(v2);
				alg.recieveRemoveFromW(v);
				// alg.recieveSpan(v, 0L);
				if (!v2.equals(v)) {
					alg.recieveJoinS(v);
				}
			}
			synchronized (lock) {
				// System.out.println("S += " + v);
				S.add(v);
			}
			System.out.println(" --> " + v);
			for (Long v2 : dist2notSorG) {
				instances.get(v2).thankYou();
			}
			dist2notSorG.remove(v);
		}
		return;
	}

	private void joinG(Long v) {
		synchronized (G) {
			G.add(v);
		}
		for (Long v2 : dist2notSorG) {
			Algorithm34Vertex alg = instances.get(v2);
			alg.recieveRemoveFromW(v);
			// alg.recieveSpan(v, 0L);
			if (!v2.equals(v)) {
				alg.recieveJoinG(v);
			}
		}
		return;
	}

	public void thankYou() {
		// System.out.print(v + " resumed. ");
		synchronized (this.canJoinLock) {
			this.canJoin = this.canJoin + 1;
		}
	}

	public void takeABreakPlease() {
		// System.out.print(v + " is taking a break. ");
		synchronized (this.canJoinLock) {
			this.canJoin = this.canJoin - 1;
		}
	}

	public Set<Long> getW() {
		return W;
	}

	public boolean recievedFromAll() {
		boolean b = false;
		synchronized (dist2notSorG) {
			b = spans.keySet().containsAll(dist2notSorG);
		}
		return b;
	}

	@Override
	public void run() {
		while (true) {
			if (hasWhiteNeighbours(v)) {
				w = computeSpan(v); // span

				synchronized (dist2notSorG) {
					for (Long v2 : dist2notSorG) {
						instances.get(v2).recieveSpan(v, w);
					}
				}
				synchronized (waitingForSpans) {
					while (!recievedFromAll()) {
						try {
							waitingForSpans.wait(0, 1000);
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
				synchronized (dist2notSorG) {
					for (Long v2 : dist2notSorG) {
						Long getV2 = spans.get(v2);
						if ((getV2 > w)
								|| ((getV2.equals(w)) && (v2 < v))) {
							isBiggest = false;
						}
					}
				}
				if (finalTest(isBiggest)) {
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

	private boolean finalTest(boolean isBiggest) {
		boolean b = isBiggest && hasWhiteNeighbours(v);
		synchronized (canJoinLock) {
			b = b && (canJoin == 0);
		}
		return b;
	}
}
