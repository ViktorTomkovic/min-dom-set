package algorithm.chapter7;

import java.util.Map;
import java.util.Set;

import model.Graph;

public class Algorithm34Vertex2 implements Runnable {
	private Graph g;
	private Set<Long> W;
	private Set<Long> S;
	private Long v;
	private Object lock;
	private Map<Long, Long> spans;

	public Algorithm34Vertex2(Graph g, Long v, Set<Long> W, Set<Long> S,
			Map<Long, Long> spans, Object lock) {
		this.g = g;
		this.W = W;
		this.S = S;
		this.v = v;
		this.spans = spans;
		this.lock = lock;
	}

	synchronized boolean hasWhiteNeighbours(Long v, Graph g, Set<Long> W) {
		Set<Long> A = g.getN1(v);
		A.retainAll(W);
		A.remove(v);
		return A.size() > 0;
	}

	synchronized Long computeSpan(Long v, Graph g, Set<Long> W) {
		Set<Long> A = g.getN1(v);
		A.retainAll(W);
		return (long) A.size();
	}

	void updateSpan(Long v, Long span) {
		spans.put(v, span);
	}

	synchronized void joinS(Long v, Graph g, Set<Long> S, Set<Long> W) {
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
		Long w = computeSpan(v, g, W); // span
		updateSpan(v, w);
		while (true) {
			if (hasWhiteNeighbours(v, g, W)) {
				w = computeSpan(v, g, W); // span
				updateSpan(v, w);

				boolean isBiggest = true;
				synchronized (lock) {
					Set<Long> dist2 = g.getN2(v);
					dist2.retainAll(W);
					for (Long v2 : dist2) {
						Long getV2 = spans.get(v2);
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
