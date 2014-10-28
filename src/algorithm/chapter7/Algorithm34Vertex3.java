package algorithm.chapter7;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import model.Graph;

public class Algorithm34Vertex3 extends Thread {
	private Graph g;
	private LinkedHashSet<Integer> W;
	private Set<Integer> S;
	private Set<Integer> G;
	private Integer v;
	private Object lock;
	private Object waitingForSpans = new Object();
	private Map<Integer, Integer> spans;
	private LinkedHashMap<Integer, Algorithm34Vertex3> instances;
	private boolean canJoin = true;
	private LinkedHashSet<Integer> dist2notSorG;

	public Algorithm34Vertex3(Graph g, Integer v, Set<Integer> S, Set<Integer> G,
			LinkedHashMap<Integer, Algorithm34Vertex3> instances, Object lock) {
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
	}

	public void recieveJoin(Integer from) {
		synchronized (dist2notSorG) {
			dist2notSorG.remove(from);
		}
	}

	private void joinS(Integer v, Graph g) {
		System.out.print(v + " <-- ");
		System.out.print(dist2notSorG);
		for (Integer v2 : dist2notSorG) {
			instances.get(v2).takeABreakPlease();
			// System.out.println(instances.get(v2).getW());
		}
		for (Integer v2 : dist2notSorG) {
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
		for (Integer v2 : dist2notSorG) {
			instances.get(v2).thankYou();
		}
		return;
	}

	private void joinG(Integer v2) {
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

	public Set<Integer> getW() {
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
				Integer w = computeSpan(v); // span

				for (Integer v2 : dist2notSorG) {
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
				LinkedHashMap<Integer, Integer> oldspans = new LinkedHashMap<>();
				oldspans.putAll(spans);
				w = computeSpan(v);
				boolean isBiggest = true;
				for (Integer v2 : dist2notSorG) {
					Integer getV2 = spans.get(v2);
					if ((getV2 > w) || ((getV2.equals(w)) && (v2 < v))) {
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
