package mindomset.algorithm.greedy;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import mindomset.algorithm.AbstractMDSAlgorithm;
import mindomset.algorithm.AbstractMDSResult;
import mindomset.algorithm.MDSResultBackedByIntOpenHashSet;
import mindomset.algorithm.Utils;
import mindomset.datastructure.graph.Graph;

import com.carrotsearch.hppc.IntIntOpenHashMap;
import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.cursors.IntCursor;

public class GreedySweepAlgorithm implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;
	private IntOpenHashSet S = new IntOpenHashSet();
	private IntOpenHashSet W = new IntOpenHashSet();
	private IntOpenHashSet G = new IntOpenHashSet();
	private IntObjectOpenHashMap<IntOpenHashSet> neig = new IntObjectOpenHashMap<IntOpenHashSet>();
	private IntObjectOpenHashMap<IntOpenHashSet> neig3 = new IntObjectOpenHashMap<IntOpenHashSet>();
	private IntObjectOpenHashMap<IntOpenHashSet> neigNonG = new IntObjectOpenHashMap<IntOpenHashSet>();
	private IntObjectOpenHashMap<IntOpenHashSet> neigN2 = new IntObjectOpenHashMap<IntOpenHashSet>();
	private IntIntOpenHashMap howManyWhiteCanKeySee = new IntIntOpenHashMap();
	private IntObjectOpenHashMap<IntOpenHashSet> keyGrantsFlowerToValues = new IntObjectOpenHashMap<IntOpenHashSet>();
	private IntObjectOpenHashMap<IntOpenHashSet> keyHasGrantedFlowerByValues = new IntObjectOpenHashMap<IntOpenHashSet>();
	private IntIntOpenHashMap sweepCardinality = new IntIntOpenHashMap();
	private int iterations = 0;
	private int skipped = 0;
	private int initialSize = 0;

	private static class ResultHolder {
		public int result;
		public int iterations;
		public int neighCount;
		public int skipped;
	}

	private void recomputeSweepCardinalityFor(IntOpenHashSet vertices) {
		for (IntCursor vercur : vertices) {
			sweepCardinality.put(vercur.value, 0);
		}
		for (IntCursor vercur : vertices) {
			IntOpenHashSet neigNG = neigNonG.get(vercur.value);
			for (IntCursor ncur : neigNG) {
				if (Utils.containsAll(neigNG, neigNonG.get(ncur.value))) {
					sweepCardinality.addTo(vercur.value, 1);
				}
			}
		}
	}

	private ResultHolder maxByN1(IntOpenHashSet white,
			IntObjectOpenHashMap<IntOpenHashSet> neig, int oldMax) {
		ResultHolder rh = new ResultHolder();
		int max = 0;
		int maxCount = -1;
		rh.skipped = 0;
		int iterations = 0;
		int maxCar = -1;
		for (IntCursor potcur : white) {
			iterations = iterations + 1;
			int sweepCar = sweepCardinality.get(potcur.value);
			int vertexCardinality = neigNonG.get(potcur.value).size();
			if (sweepCar > maxCar
					|| (sweepCar == maxCar && vertexCardinality < maxCount)) {
//			if (vertexCardinality > maxCount
//					|| (vertexCardinality == maxCount && sweepCar > maxCar)) {
				max = potcur.value;
				maxCount = vertexCardinality;
				maxCar = sweepCar;
			}
		}
		rh.result = max;
		rh.neighCount = maxCount;
		rh.iterations = iterations;
		return rh;
	}

	private void cleanNonFlowers() {
		int lastMax = -1;
		while (!G.isEmpty()) {
			iterations = iterations + 1;
			ResultHolder rh = maxByN1(W, neigNonG, lastMax);
			iterations = iterations + rh.iterations;
			lastMax = rh.neighCount;
			int pick = rh.result;
			skipped = skipped + rh.skipped;
			W.remove(pick);
			IntOpenHashSet greying = new IntOpenHashSet(neigNonG.get(pick));
			G.removeAll(greying);
			for (IntCursor v : neigN2.get(pick)) {
				neigNonG.get(v.value).removeAll(greying);
			}
			IntOpenHashSet verticesToRecompute;// = new IntOpenHashSet(
					//(int) (W.size() / 0.60));
//			for (IntCursor n1cur : neig.get(pick)) {
//				// if (verticesToRecompute.contains(n1cur.value)) {
//				// continue;
//				// }
//				verticesToRecompute.add(n1cur.value);
//				for (IntCursor n2cur : neig.get(n1cur.value)) {
//					// if (verticesToRecompute.contains(n2cur.value)) {
//					// continue;
//					// }
//					verticesToRecompute.add(n2cur.value);
//					for (IntCursor n3cur : neig.get(n2cur.value)) {
//						verticesToRecompute.add(n3cur.value);
//						// for (IntCursor n4cur : neig.get(n3cur.value)) {
//						// verticesToRecompute.add(n4cur.value);
//						// }
//					}
//				}
//			}
			verticesToRecompute = neig3.get(pick);
			recomputeSweepCardinalityFor(verticesToRecompute);
			sweepCardinality.put(pick, 0);
			S.add(pick);
		}
	}

	@Override
	public AbstractMDSResult mdsAlg(Graph g) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long start = bean.getCurrentThreadCpuTime();
		W = new IntOpenHashSet(g.getVertices());
		G = new IntOpenHashSet(g.getVertices());
		initialSize = (int) Math.ceil(g.getNumberOfVertices() * (1 / 0.65)) + 1;

		neig = new IntObjectOpenHashMap<IntOpenHashSet>(initialSize);
		neigNonG = new IntObjectOpenHashMap<>(initialSize);
		neigN2 = new IntObjectOpenHashMap<>(initialSize);
		howManyWhiteCanKeySee = new IntIntOpenHashMap(initialSize);
		keyGrantsFlowerToValues = new IntObjectOpenHashMap<>(initialSize);
		keyHasGrantedFlowerByValues = new IntObjectOpenHashMap<>(initialSize);
		for (IntCursor vcur : W) {
			IntOpenHashSet n1 = g.getN1(vcur.value);
			neig.put(vcur.value, new IntOpenHashSet(n1));
			neigNonG.put(vcur.value, new IntOpenHashSet(n1));
			neigN2.put(vcur.value, new IntOpenHashSet(g.getN2(vcur.value)));
			howManyWhiteCanKeySee.put(vcur.value, n1.size());
			keyGrantsFlowerToValues.put(vcur.value, new IntOpenHashSet());
			keyHasGrantedFlowerByValues.put(vcur.value, new IntOpenHashSet());
		}
		for (IntCursor wcur : W) {
			IntOpenHashSet neig3set = new IntOpenHashSet();
			for (IntCursor n1cur : neig.get(wcur.value)) {
				 if (neig3set.contains(n1cur.value)) {
				 continue;
				 }
				neig3set.add(n1cur.value);
				for (IntCursor n2cur : neig.get(n1cur.value)) {
					 if (neig3set.contains(n2cur.value)) {
					 continue;
					 }
					neig3set.add(n2cur.value);
					for (IntCursor n3cur : neig.get(n2cur.value)) {
						neig3set.add(n3cur.value);
						// for (IntCursor n4cur : neig.get(n3cur.value)) {
						// verticesToRecompute.add(n4cur.value);
						// }
					}
				}
			}
			neig3.put(wcur.value, neig3set);
		}
		sweepCardinality = new IntIntOpenHashMap(initialSize);
		recomputeSweepCardinalityFor(W);
		S = new IntOpenHashSet(initialSize);
		iterations = 0;
		prepTime = bean.getCurrentThreadCpuTime() - start;

		skipped = 0;
		cleanNonFlowers();

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
