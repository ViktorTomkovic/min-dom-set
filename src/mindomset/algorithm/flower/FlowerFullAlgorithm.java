package mindomset.algorithm.flower;

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

public class FlowerFullAlgorithm implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;
	private IntOpenHashSet S = new IntOpenHashSet(); // chosen
	private IntOpenHashSet W = new IntOpenHashSet(); // non-S
	private IntOpenHashSet G = new IntOpenHashSet(); 
	private IntObjectOpenHashMap<IntOpenHashSet> neig = new IntObjectOpenHashMap<IntOpenHashSet>();
	private IntObjectOpenHashMap<IntOpenHashSet> neigNonG = new IntObjectOpenHashMap<IntOpenHashSet>();
	private IntObjectOpenHashMap<IntOpenHashSet> neigN2 = new IntObjectOpenHashMap<IntOpenHashSet>();
	private IntOpenHashSet definiteFlowers = new IntOpenHashSet();
	private IntOpenHashSet potentialFlowers = new IntOpenHashSet();
	private IntOpenHashSet others = new IntOpenHashSet();
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

	private ResultHolder candidateForGreedy(int oldMax) {
		ResultHolder rh = new ResultHolder();
		int max = -1;
		int maxCount = -1;
		int maxSweep = -1;
		rh.skipped = 0;
		int iterations = 0;
		for (IntCursor current : W) {
			iterations = iterations + 1;
			int currentCount = neigNonG.get(current.value).size();
			IntOpenHashSet nn = neigNonG.get(current.value);
			int sweepCardinality = 0;
			for (IntCursor nncur : nn) {
				IntOpenHashSet nnn = neigNonG.get(nncur.value);
				if (nnn.size() == 1 && nnn.contains(current.value)) {
					sweepCardinality = sweepCardinality + 1;
				} else if (nnn.size() == 2 && nnn.contains(nncur.value) && nnn.contains(current.value)) {
					sweepCardinality = sweepCardinality + 1;
				}
			}
			if ((currentCount > maxCount)
					|| (currentCount == maxCount && sweepCardinality > maxSweep)) {
				max = current.value;
				maxCount = currentCount;
				maxSweep = sweepCardinality;
			}
		}
		rh.result = max;
		rh.neighCount = maxCount;
		rh.iterations = iterations;
		return rh;
	}

	private ResultHolder candidateForPotentialFlower(int oldMax) {
		ResultHolder rh = new ResultHolder();
		int max = -1;
		int maxCount = -1;
		int maxCar = Integer.MAX_VALUE;
		// int maxCar = -1;
		int maxSweep = -1;
		rh.skipped = 0;
		int iterations = 0;
		for (IntCursor potcur : potentialFlowers) {
			iterations = iterations + 1;
			// System.out.println(potcur.value);
			int flowerCardinality = keyHasGrantedFlowerByValues.get(
					potcur.value).size();
			IntOpenHashSet nn = neigNonG.get(potcur.value);
			int vertexCardinality = nn.size();
			int sweepCardinality = 0;
			for (IntCursor nncur : nn) {
				IntOpenHashSet nnn = neigNonG.get(nncur.value);
				if (nnn.size() == 1 && nnn.contains(nncur.value)) {
					sweepCardinality = sweepCardinality + 1;
				} else if (nnn.size() == 2 && nnn.contains(nncur.value) && nnn.contains(potcur.value)) {
					sweepCardinality = sweepCardinality + 1;
				}
			}
			if ((flowerCardinality > maxCount)
					|| ((flowerCardinality == maxCount) && (sweepCardinality > maxSweep))
					|| ((flowerCardinality == maxCount)
							&& (sweepCardinality == maxSweep) && (vertexCardinality > maxCar))) {
				max = potcur.value;
				maxCount = flowerCardinality;
				maxCar = vertexCardinality;
				maxSweep = sweepCardinality;
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
		System.out.print(indexInSorted + "\t");
		while ((indexInSorted < sortedByDegree.length)
				&& ((skaredaPremenna = neig.get(sortedByDegree[indexInSorted]))
						.size() == 2)) {
			int[] vertices = skaredaPremenna.toArray();
			if (sortedByDegree[indexInSorted] == vertices[0]) {
				definiteFlowers.add(vertices[1]);
				markedAsFlower.set(vertices[1]);
				others.add(vertices[0]);
			} else {
				definiteFlowers.add(vertices[0]);
				markedAsFlower.set(vertices[0]);
				others.add(vertices[1]);
			}
			indexInSorted = indexInSorted + 1;
		}
		System.out.print(indexInSorted + "\t");

		while (indexInSorted < sortedByDegree.length) {
			// leave out already marked flowers
			int grants = sortedByDegree[indexInSorted];
			if (true /*!markedAsFlower.get(grants)*/) {
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
		System.out.print(indexInSorted + "\n");
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
		int countChosenDefFlowers = 0;
		for (IntCursor flowercur : definiteFlowers) {
			iterations = iterations + 1;
			int flower = flowercur.value;
			IntOpenHashSet greying = new IntOpenHashSet(
					neig.get(flower));
			for (IntCursor vcur : neigN2.get(flower)) {
				neigNonG.get(vcur.value).removeAll(greying);
				//neigNonG.get(vcur.value).remove(flower);
				//neigNonG.get(vcur.value).removeAll(others);
			}
			S.add(flower);
			countChosenDefFlowers = countChosenDefFlowers + 1;
			G.removeAll(greying);
			//G.remove(flower);
			W.remove(flower);
		}
		//G.removeAll(others);
		clearEmptyFlowers();
		System.out.println("Chosen Definite Flowers: " + countChosenDefFlowers);
	}

	private void cleanPotentialFlowers() {
		int lastMax = -1;
		int countChosenPotFlowers = 0;
		while (!potentialFlowers.isEmpty()) {
			iterations = iterations + 1;
			ResultHolder rh = candidateForPotentialFlower(lastMax);
			
			iterations = iterations + rh.iterations;
			lastMax = rh.neighCount;
			int pick = rh.result;
			skipped = skipped + rh.skipped;
			// System.out.println(pick + " "
			// + keyHasGrantedFlowerByValues.get(pick).size() + " "
			// + neigNonG.get(pick).size());
			W.remove(pick);
			IntOpenHashSet greying = new IntOpenHashSet(neigNonG.get(pick));
			for (IntCursor v : neigN2.get(pick)) {
				neigNonG.get(v.value).removeAll(greying);
			}
//			for (IntCursor v : neigN2.get(pick)) {
//				if (potentialFlowers.contains(v.value) && neigNonG.get(v.value).size() == 0) {
//					potentialFlowers.remove(v.value);
//				}
//			}
			S.add(pick);
			countChosenPotFlowers = countChosenPotFlowers + 1;
			G.removeAll(greying);
			potentialFlowers.removeAll(greying);
			IntOpenHashSet cleanFlowers = keyHasGrantedFlowerByValues.get(pick);
			for (IntCursor flowercur : cleanFlowers) {
				keyGrantsFlowerToValues.get(flowercur.value).remove(pick);
			}
			keyHasGrantedFlowerByValues.remove(pick);
			clearEmptyFlowers();
		}
		System.out.println("Chosen Potetntial Flowers: " + countChosenPotFlowers);
	}

	private void cleanNonFlowers() {
		int lastMax = -1;
		while (!G.isEmpty()) {
			iterations = iterations + 1;
			ResultHolder rh = candidateForGreedy(lastMax);
			iterations = iterations + rh.iterations;
			lastMax = rh.neighCount;
			int pick = rh.result;
			skipped = skipped + rh.skipped;
			W.remove(pick);
			System.out.println(pick);
			if (pick == -1) {
				System.out.println(G);
				System.out.println(rh.neighCount);
			}
			IntOpenHashSet greying = new IntOpenHashSet(neigNonG.get(pick));
			for (IntCursor v : neigN2.get(pick)) {
				neigNonG.get(v.value).removeAll(greying);
			}
			S.add(pick);
			G.removeAll(greying);
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

		markFlowers();
		cleanDefiniteFlowers();
		cleanPotentialFlowers();
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
