package algorithm.chapter7;

import java.util.Map;
import java.util.Set;

import model.Graph;

public class Algorithm34Vertex2 implements Runnable {
	private Graph g;
	private Set<Integer> W;
	private Set<Integer> S;
	private Integer v;
	private Object lock;
	private Map<Integer, Integer> spans;

	public Algorithm34Vertex2(Graph g, Integer v, Set<Integer> W, Set<Integer> S,
			Map<Integer, Integer> spans, Object lock) {
		this.g = g;
		this.W = W;
		this.S = S;
		this.v = v;
		this.spans = spans;
		this.lock = lock;
	}

	synchronized boolean hasWhiteNeighbours(Integer v, Graph g, Set<Integer> W) {
		Set<Integer> A = g.getN1(v);
		A.retainAll(W);
		A.remove(v);
		return A.size() > 0;
	}

	synchronized Integer computeSpan(Integer v, Graph g, Set<Integer> W) {
		Set<Integer> A = g.getN1(v);
		A.retainAll(W);
		return A.size();
	}

	void updateSpan(Integer v, Integer span) {
		spans.put(v, span);
	}

	synchronized void joinS(Integer v, Graph g, Set<Integer> S, Set<Integer> W) {
		System.out.print(v + " " + W + " ");
		W.removeAll(g.getN1(v));
		System.out.println(W);
		S.add(v);
		return;
	}

	@Override
	public void run() {
		/*
		 * try { long t = Math.round(MyRandom.Double() * 1000);
		 * System.out.println(v + " " + t); Thread.sleep(t); } catch
		 * (InterruptedException e) { e.printStackTrace(); } if (W.contains(v))
		 * { W.removeAll(g.getNeighboursOfVertexIncluded(v)); S.add(v); }
		 */
		Integer w = computeSpan(v, g, W); // span
		updateSpan(v, w);
		while (true) {
			if (hasWhiteNeighbours(v, g, W)) {
				w = computeSpan(v, g, W); // span
				updateSpan(v, w);

				boolean isBiggest = true;
				synchronized (lock) {
					Set<Integer> dist2 = g.getN2(v);
					dist2.retainAll(W);
					for (Integer v2 : dist2) {
						Integer getV2 = spans.get(v2);
						if ((getV2 > w)
								|| (getV2.equals(w) && v2 < v)) {
							isBiggest = false;
						}
					}
					if (isBiggest && hasWhiteNeighbours(v, g, W)) {
						joinS(v, g, S, W);
						System.out.println("koniec+ " + v);
						return;
					}
				}
			} else {
				w = computeSpan(v, g, W);
				updateSpan(v, w);
				System.out.println("koniec- " + v);
				return;
			}
		}
	}

}
