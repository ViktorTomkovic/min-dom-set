package mindomset.algorithm.greedy;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import mindomset.algorithm.AbstractMDSAlgorithm;
import mindomset.algorithm.AbstractMDSResult;
import mindomset.algorithm.MDSResultBackedByIntOpenHashSet;
import mindomset.datastructure.graph.Graph;

import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.cursors.IntCursor;

public class GreedySproutAlgorithm implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;

	private static class ResultHolder {
		public int result;
		public int iterations;
		public int neighCount;
		public int skipped;
	}

	private ResultHolder maxByN1(IntOpenHashSet white,
			IntObjectOpenHashMap<IntOpenHashSet> neig, int oldMax) {
		ResultHolder rh = new ResultHolder();
		int max = 0;
		int maxCount = 0;
		rh.skipped = 0;
		int iterations = 0;
		for (IntCursor current : white) {
			iterations = iterations + 1;
			int currentCount = neig.get(current.value).size();
			if (currentCount > maxCount) {
				max = current.value;
				maxCount = currentCount;
			}
			if (oldMax == currentCount) {
				rh.skipped = 1;
				break;
			}
		}
		rh.result = max;
		rh.neighCount = maxCount;
		rh.iterations = iterations;
		return rh;
	}

	@Override
	public AbstractMDSResult mdsAlg(Graph g) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long start = bean.getCurrentThreadCpuTime();
		IntOpenHashSet W = new IntOpenHashSet(g.getVertices());
		IntOpenHashSet G = new IntOpenHashSet(g.getVertices());
		int initialSize = (int) Math.ceil(g.getNumberOfVertices() * (1 / 0.65)) + 1;

		IntObjectOpenHashMap<IntOpenHashSet> neigW = new IntObjectOpenHashMap<>(
				initialSize);
		for (IntCursor vcur : W) {
			neigW.put(vcur.value, new IntOpenHashSet(g.getN1(vcur.value)));
		}

		prepTime = bean.getCurrentThreadCpuTime() - start;
		IntOpenHashSet S = new IntOpenHashSet(initialSize);
		int iterations = 0;
		boolean added = true;
		while (added) {
			IntOpenHashSet chosenOnes = new IntOpenHashSet();
			IntOpenHashSet flowers = new IntOpenHashSet();
//			chosenOnes.clear();
//			flowers.clear();
			for (IntCursor vcur : G) {
				iterations = iterations + 1;
				IntOpenHashSet neighs = neigW.get(vcur.value);
				if (neighs.size() == 2) {
					int[] vv = neighs.toArray();
					if (vcur.value == vv[0]) {
						chosenOnes.add(vv[0]);
						flowers.add(vv[1]);
						neigW.get(vv[1]).remove(vv[0]);
					} else {
						chosenOnes.add(vv[1]);
						flowers.add(vv[0]);
						neigW.get(vv[0]).remove(vv[1]);
					}
				} else if (neighs.size() == 1) {
					int v = neighs.toArray()[0];
					chosenOnes.add(v);
					flowers.add(v);
				}
			}

			System.out.println("Unique flowers: " + flowers.size());
			for (IntCursor chcur : chosenOnes) {
				iterations = iterations + 1;
				G.remove(chcur.value);
				W.remove(chcur.value);
			}
			for (IntCursor flowercur : flowers) {
				G.remove(flowercur.value);
				// W.remove(flowercur.value);
				S.add(flowercur.value);
				for (IntCursor ncur : g.getN1(flowercur.value)) {
					// G.remove(ncur.value);

					neigW.get(ncur.value).remove(flowercur.value);
				}
			}
			added = !flowers.isEmpty();
		}
		int lastMax = -1;
		int skipped = 0;
		while (!G.isEmpty()) {
			iterations = iterations + 1;
			ResultHolder rh = maxByN1(W, neigW, lastMax);
			iterations = iterations + rh.iterations;
			lastMax = rh.neighCount;
			int pick = rh.result;
			skipped = skipped + rh.skipped;
			W.remove(pick);
			IntOpenHashSet greying = new IntOpenHashSet(neigW.get(pick));
			G.removeAll(greying);
			for (IntCursor v : g.getN2(pick)) {
				neigW.get(v.value).removeAll(greying);
			}
			S.add(pick);
		}
		runTime = bean.getCurrentThreadCpuTime() - start;
		System.out.println("Number of iterations: " + iterations);
		System.out.println("Skipped: " + skipped);
		MDSResultBackedByIntOpenHashSet result = new MDSResultBackedByIntOpenHashSet();
		result.setResult(S);
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
