package algorithm.chapter7;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import model.Graph;

public class Algorithm34Vertex extends Thread {
	private Graph g;
	private LinkedHashSet<Integer> W;
	private Set<Integer> S;
	private Set<Integer> G;
	private Integer v;
	private Object lock;
	private Object waitingForSpans = new Object();
	private Map<Integer, Integer> spans;
	private LinkedHashMap<Integer, Algorithm34Vertex> instances;
	private final Object canJoinLock = new Object();
	private Integer canJoin = 0; // mutex - how many unfinished joins are in
								// neighbourhood
	private LinkedHashSet<Integer> dist2notSorG;
	private final Object wLock = new Object();
	private Integer w;

	public Algorithm34Vertex(Graph g, Integer v, Set<Integer> S, Set<Integer> G,
			LinkedHashMap<Integer, Algorithm34Vertex> instances, Object lock) {
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

	private boolean hasWhiteNeighbours(Integer v) {
		Set<Integer> A;
		synchronized (W) {
			A = new LinkedHashSet<>(W);
		}
		A.remove(v);
		return !A.isEmpty();
	}

	private Integer computeSpan(Integer v) {
		Integer result;
		synchronized (W) {
			result = Integer.valueOf(W.size());
		}
		return result;
	}

	public void recieveSpan(Integer from, Integer span) {
		synchronized (spans) {
			spans.put(from, span);
			// System.out.println("span recieved from " + from + " at " + v +
			// " value " + span);
		}
		synchronized (waitingForSpans) {
			waitingForSpans.notifyAll();
		}
	}

	public void recieveRemoveFromW(Integer from) {
		synchronized (W) {
			W.remove(from);
			// System.out.println("W remove recieved from " + from + " at " +
			// v);
		}
		synchronized (wLock) {
			w = computeSpan(this.v);
		}
	}

	public void recieveJoinS(Integer from) {
		synchronized (dist2notSorG) {
			dist2notSorG.remove(from);
		}
	}

	public void recieveJoinG(Integer from) {
		synchronized (dist2notSorG) {
			dist2notSorG.remove(from);
		}
	}

	private void joinS(Integer v, Graph g) {
		synchronized (dist2notSorG) {
			System.out.print(v + " <-- ");
			System.out.print(dist2notSorG);
			for (Integer v2 : dist2notSorG) {
				instances.get(v2).takeABreakPlease();
				// System.out.println(instances.get(v2).getW());
			}
			for (Integer v2 : dist2notSorG) {
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
			for (Integer v2 : dist2notSorG) {
				instances.get(v2).thankYou();
			}
			dist2notSorG.remove(v);
		}
		return;
	}

	private void joinG(Integer v) {
		synchronized (G) {
			G.add(v);
		}
		for (Integer v2 : dist2notSorG) {
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

	public Set<Integer> getW() {
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
					for (Integer v2 : dist2notSorG) {
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
				LinkedHashMap<Integer, Integer> oldspans = new LinkedHashMap<>();
				oldspans.putAll(spans);
				w = computeSpan(v);

				boolean isBiggest = true;
				synchronized (dist2notSorG) {
					for (Integer v2 : dist2notSorG) {
						Integer getV2 = spans.get(v2);
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
