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
	private IntOpenHashSet S = new IntOpenHashSet();
	private IntOpenHashSet W = new IntOpenHashSet();
	private IntOpenHashSet G = new IntOpenHashSet();
	private IntObjectOpenHashMap<IntOpenHashSet> neigW = new IntObjectOpenHashMap<IntOpenHashSet>();
	private IntObjectOpenHashMap<IntOpenHashSet> neigW2 = new IntObjectOpenHashMap<IntOpenHashSet>();
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
		Arrays.sort(sortedByDegree, new LessByN1HComparator(neigW));

		definiteFlowers = new IntOpenHashSet();
		int indexInSorted = 0;
		// vertices that create component on their own are flowers
		while ((indexInSorted < sortedByDegree.length)
				&& (neigW.get(sortedByDegree[indexInSorted]).size() == 1)) {
			definiteFlowers.add(sortedByDegree[indexInSorted]);
			markedAsFlower.set(sortedByDegree[indexInSorted]);
			indexInSorted = indexInSorted + 1;
		}
		// vertices with degree 2 can grants definite flower status
		IntOpenHashSet skaredaPremenna;
		while ((indexInSorted < sortedByDegree.length)
				&& ((skaredaPremenna = neigW.get(sortedByDegree[indexInSorted]))
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
				IntOpenHashSet neighs = neigW.get(grants);
				for (IntCursor neighscur : neighs) {
					int granted = neighscur.value;
					if (Utils.containsAll(neigW.get(granted), neighs)) {
						// a potential flower founded
						potentialFlowers.add(neighscur.value);
						keyGrantsFlowerToValues.get(grants).add(granted);
						keyHasGrantedFlowerByValues.get(granted).add(grants);
						markedAsFlower.set(granted);
					}
				}
			}
			indexInSorted = indexInSorted + 1;
		}
		System.out.println("Definite flowers: " + definiteFlowers.size());
		System.out.println("Potential flowers: " + potentialFlowers.size());
	}

	private void markPotentialFlowers() {

	}

	private void cleanDefiniteFlowers() {
		for (IntCursor flowercur : definiteFlowers) {
			iterations = iterations + 1;
			W.remove(flowercur.value);
			IntOpenHashSet greying = new IntOpenHashSet(
					neigW.get(flowercur.value));
			G.removeAll(greying);
			for (IntCursor vcur : neigW2.get(flowercur.value)) {
				neigW.get(vcur.value).removeAll(greying);
			}
			S.add(flowercur.value);
		}
	}

	private void cleanPotentialFlowers() {
		//
	}

	private void cleanNonFlowers() {
		int lastMax = -1;
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
			for (IntCursor v : neigW2.get(pick)) {
				neigW.get(v.value).removeAll(greying);
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

		neigW = new IntObjectOpenHashMap<>(initialSize);
		neigW2 = new IntObjectOpenHashMap<>(initialSize);
		howManyWhiteCanKeySee = new IntIntOpenHashMap(initialSize);
		keyGrantsFlowerToValues = new IntObjectOpenHashMap<>(initialSize);
		keyHasGrantedFlowerByValues = new IntObjectOpenHashMap<>(initialSize);
		for (IntCursor vcur : W) {
			IntOpenHashSet n1 = g.getN1(vcur.value);
			neigW.put(vcur.value, new IntOpenHashSet(n1));
			neigW2.put(vcur.value, new IntOpenHashSet(g.getN2(vcur.value)));
			howManyWhiteCanKeySee.put(vcur.value, n1.size());
			keyGrantsFlowerToValues.put(vcur.value, new IntOpenHashSet());
			keyHasGrantedFlowerByValues.put(vcur.value, new IntOpenHashSet());
		}
		S = new IntOpenHashSet(initialSize);
		iterations = 0;		
		prepTime = bean.getCurrentThreadCpuTime() - start;

		markFlowers();
		markPotentialFlowers();
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
