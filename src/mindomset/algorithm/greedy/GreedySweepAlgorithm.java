package mindomset.algorithm.greedy;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;

import mindomset.algorithm.AbstractMDSAlgorithm;
import mindomset.algorithm.AbstractMDSResult;
import mindomset.algorithm.LessByN1HComparator;
import mindomset.algorithm.MDSResultBackedByIntOpenHashSet;
import mindomset.algorithm.Utils;
import mindomset.datastructure.graph.Graph;

import com.carrotsearch.hppc.BitSet;
import com.carrotsearch.hppc.IntIntOpenHashMap;
import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.IntObjectCursor;

public class GreedySweepAlgorithm implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;
	private IntOpenHashSet S = new IntOpenHashSet();
	private IntOpenHashSet W = new IntOpenHashSet();
	private IntOpenHashSet G = new IntOpenHashSet();
	private IntObjectOpenHashMap<IntOpenHashSet> neig = new IntObjectOpenHashMap<IntOpenHashSet>();
	private IntObjectOpenHashMap<IntOpenHashSet> neigNonG = new IntObjectOpenHashMap<IntOpenHashSet>();
	private IntObjectOpenHashMap<IntOpenHashSet> neigN2 = new IntObjectOpenHashMap<IntOpenHashSet>();
	private IntOpenHashSet definiteFlowers = new IntOpenHashSet();
	private IntOpenHashSet potentialFlowers = new IntOpenHashSet();
	private IntIntOpenHashMap howManyWhiteCanKeySee = new IntIntOpenHashMap();
	private IntObjectOpenHashMap<IntOpenHashSet> keyGrantsFlowerToValues = new IntObjectOpenHashMap<IntOpenHashSet>();
	private IntObjectOpenHashMap<IntOpenHashSet> keyHasGrantedFlowerByValues = new IntObjectOpenHashMap<IntOpenHashSet>();
	private int iterations = 0;
	private int skipped = 0;
	private int initialSize = 0;

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
		int maxCar = -1;
		IntIntOpenHashMap sweepCardinality = new IntIntOpenHashMap(initialSize);
		for (IntCursor vercur : W) {
			sweepCardinality.put(vercur.value, 0);
		}
		for (IntCursor vercur : W) {
			IntOpenHashSet neigNG = neigNonG.get(vercur.value);
			for (IntCursor ncur : neigNonG.get(vercur.value)) {
				if (Utils.containsAll(neigNG, neigNonG.get(ncur.value))) {
					sweepCardinality.addTo(vercur.value, 1);
				}
			}
		}
		for (IntCursor potcur : white) {
			iterations = iterations + 1;
			int sweepCar = sweepCardinality.get(potcur.value);
			int vertexCardinality = neigNonG.get(
					potcur.value).size();
			if (sweepCar > maxCar
					|| (sweepCar == maxCar &&  vertexCardinality > maxCount)) {
//			if (vertexCardinality > maxCount
//					|| (vertexCardinality == maxCount && sweepCar > maxCar)) {
				max = potcur.value;
				maxCount = vertexCardinality;
				maxCar = sweepCar;
			}
			// if (oldMax == flowerCardinality) {
			// rh.skipped = 1;
			// break;
			// }
		}
		rh.result = max;
		rh.neighCount = maxCount;
		rh.iterations = iterations;
		return rh;
	}

	private ResultHolder candidateForFlower(int oldMax) {
		ResultHolder rh = new ResultHolder();
		int max = -1;
		int maxCount = -1;
		// int maxCar = Integer.MAX_VALUE;
		int maxCar = -1;
		rh.skipped = 0;
		int iterations = 0;
		IntIntOpenHashMap sweepCardinality = new IntIntOpenHashMap(initialSize);
		for (IntCursor vercur : W) {
			sweepCardinality.put(vercur.value, 0);
		}
		for (IntCursor vercur : W) {
			IntOpenHashSet neigNG = neigNonG.get(vercur.value);
			for (IntCursor ncur : neigNonG.get(vercur.value)) {
				if (Utils.containsAll(neigNG, neigNonG.get(ncur.value))) {
					sweepCardinality.addTo(vercur.value, 1);
				}
			}
		}
		for (IntCursor potcur : potentialFlowers) {
			iterations = iterations + 1;
			int sweepCar = sweepCardinality.get(potcur.value);
			int vertexCardinality = neigNonG.get(
					potcur.value).size();
			if (vertexCardinality > maxCount
					|| (vertexCardinality == maxCount &&  sweepCar > maxCar)) {
				max = potcur.value;
				maxCount = vertexCardinality;
				maxCar = sweepCar;
			}
			// if (oldMax == flowerCardinality) {
			// rh.skipped = 1;
			// break;
			// }
		}
		System.out.println(sweepCardinality);
		rh.result = max;
		rh.neighCount = maxCount;
		rh.iterations = iterations;
		return rh;
	}

	private void markFlowers() {
		Integer[] sortedByDegree = new Integer[W.size()];
		BitSet markedAsFlower = new BitSet(W.size());
		int j = 0;
		for (int i = 0; i < W.allocated.length; i++) {
			if (W.allocated[i]) {
				sortedByDegree[j] = W.keys[i];
				j = j + 1;
			}
		}
		Arrays.sort(sortedByDegree, new LessByN1HComparator(neig));

		definiteFlowers = new IntOpenHashSet();
		int indexInSorted = 0;
		// vertices that create component on their own are flowers
		while ((indexInSorted < sortedByDegree.length)
				&& (neig.get(sortedByDegree[indexInSorted]).size() == 1)) {
			definiteFlowers.add(sortedByDegree[indexInSorted]);
			markedAsFlower.set(sortedByDegree[indexInSorted]);
			indexInSorted = indexInSorted + 1;
		}
		// vertices with degree 2 can grants definite flower status
		IntOpenHashSet skaredaPremenna;
		while ((indexInSorted < sortedByDegree.length)
				&& ((skaredaPremenna = neig.get(sortedByDegree[indexInSorted]))
						.size() == 2)) {
			int[] vertices = skaredaPremenna.toArray();
			if (sortedByDegree[indexInSorted] == vertices[0]) {
				definiteFlowers.add(vertices[1]);
				markedAsFlower.set(vertices[1]);
			} else {
				definiteFlowers.add(vertices[0]);
				markedAsFlower.set(vertices[0]);
			}
			indexInSorted = indexInSorted + 1;
		}

		while (indexInSorted < sortedByDegree.length) {
			// leave out already marked flowers
			int grants = sortedByDegree[indexInSorted];
			if (!markedAsFlower.get(grants)) {
				IntOpenHashSet neighs = neig.get(grants);
				for (IntCursor neighscur : neighs) {
					int granted = neighscur.value;
					if (grants != granted
							&& Utils.containsAll(neig.get(granted), neighs)) {
						// a potential flower founded
						// System.out.println(grants + "->" + granted);
						potentialFlowers.add(granted);
						keyGrantsFlowerToValues.get(grants).add(granted);
						keyHasGrantedFlowerByValues.get(granted).add(grants);
						markedAsFlower.set(granted);
					}
				}
			}
			indexInSorted = indexInSorted + 1;
		}
		System.out.println("Definite flowers: " + definiteFlowers.size() + " "
				+ definiteFlowers);
		System.out.println("Potential flowers: " + potentialFlowers.size()
				+ " " + potentialFlowers);
	}

	private void clearEmptyFlowers() {
		IntOpenHashSet emptyFlowers = new IntOpenHashSet();
		for (IntCursor potcur : potentialFlowers) {
			if (neigNonG.get(potcur.value).size() == 0) {
				emptyFlowers.add(potcur.value);
			}
		}
		potentialFlowers.removeAll(emptyFlowers);
	}

	private void cleanDefiniteFlowers() {
		for (IntCursor flowercur : definiteFlowers) {
			iterations = iterations + 1;
			IntOpenHashSet greying = new IntOpenHashSet(
					neigNonG.get(flowercur.value));
			for (IntCursor vcur : neigN2.get(flowercur.value)) {
				neigNonG.get(vcur.value).removeAll(greying);
			}
			S.add(flowercur.value);
			G.removeAll(greying);
			W.remove(flowercur.value);
		}
		clearEmptyFlowers();
	}

	private void cleanPotentialFlowers() {
		int lastMax = -1;
		while (!potentialFlowers.isEmpty()) {
			iterations = iterations + 1;
			ResultHolder rh = candidateForFlower(lastMax);
			iterations = iterations + rh.iterations;
			lastMax = rh.neighCount;
			int pick = rh.result;
			skipped = skipped + rh.skipped;
			W.remove(pick);
			System.out.println(pick + " "
					+ keyHasGrantedFlowerByValues.get(pick).size() + " "
					+ neigNonG.get(pick).size());
			IntOpenHashSet greying = new IntOpenHashSet(neigNonG.get(pick));
			for (IntCursor v : neigN2.get(pick)) {
				neigNonG.get(v.value).removeAll(greying);
			}
			S.add(pick);
			G.removeAll(greying);
			potentialFlowers.removeAll(greying);
			IntOpenHashSet cleanFlowers = keyHasGrantedFlowerByValues.get(pick);
			for (IntCursor flowercur : cleanFlowers) {
				keyGrantsFlowerToValues.get(flowercur.value).remove(pick);
			}
			keyHasGrantedFlowerByValues.remove(pick);
			clearEmptyFlowers();
		}
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
