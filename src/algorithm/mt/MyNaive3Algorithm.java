package algorithm.mt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.cursors.IntCursor;

import main.Utils;
import algorithm.AbstractMDSAlgorithm;
import algorithm.AbstractMDSResult;
import algorithm.MDSResultBackedByIntOpenHashSet;
import datastructure.graph.Graph;

public class MyNaive3Algorithm implements AbstractMDSAlgorithm {
	private long runTime = -1L;
	private int currentCores;
	@SuppressWarnings("unused")
	private int currentPoolCores;
	private int maxCores;
	private LinkedHashSet<Integer> result;
	private int currentBest;
	private Object isWriting = new Object();
	private Object isCreatingNewThread = new Object();
	private ArrayList<Thread> pool;

	public int getCurrentCores() {
		int result;
		synchronized (isCreatingNewThread) {
			result = currentCores;
		}
		return result;
	}

	public int getMaxCores() {
		return maxCores;
	}

	public int getCurrentBest() {
		int result;
		synchronized (isWriting) {
			result = currentBest;
		}
		return result;
	}

	public void tryToSetResult(LinkedHashSet<Integer> newBest) {
		synchronized (isWriting) {
			if (newBest.size() < this.currentBest) {
				this.result = new LinkedHashSet<>(newBest);
				this.currentBest = newBest.size();
				/*
				 * System.out.println("b  " +
				 * Utils.LargeCollectionToString(newBest));
				 */
			}
		}
	}

	public Thread tryToCreateNewThread(ArrayList<Integer> unchosenVertices,
			LinkedHashSet<Integer> chosenVertices, Graph g) {
		Thread result = null;
		synchronized (isCreatingNewThread) {
			if (currentCores < maxCores) {
				currentCores++;
				Thread t = new Thread(new MyNaive3Runner(this,
						unchosenVertices, chosenVertices, g));
				result = t;
			} else {
				result = null;
			}
		}
		return result;
	}

	public void decCC() {
		synchronized (isCreatingNewThread) {
			currentCores--;
		}
	}

	public void fillPool(ArrayList<Thread> pool, ArrayList<Integer> unch,
			LinkedHashSet<Integer> ch, Graph g, ArrayList<Integer> unchP) {
		if (unch.size() == 0) {
			Thread t = new Thread(new MyNaive3Runner(this, unchP, ch, g));
			pool.add(t);
			currentPoolCores++;
			return;
		}
		
		Integer v = unch.get(unch.size() - 1);
		unch.remove(unch.size() - 1);

		LinkedHashSet<Integer> nch2 = new LinkedHashSet<>(ch);
		nch2.add(v);

		fillPool(pool, unch, ch, g, unchP);
		fillPool(pool, unch, nch2, g, unchP);

		unch.add(v);
		return;
	}

	public MyNaive3Algorithm() {
		currentCores = 0;
		currentPoolCores = 0;
		maxCores = Runtime.getRuntime().availableProcessors();
		result = new LinkedHashSet<>();
		currentBest = Integer.MAX_VALUE;
		pool = new ArrayList<>();
	}

	@Override
	public AbstractMDSResult mdsAlg(Graph g) {
		long start = System.currentTimeMillis();
		ArrayList<Integer> vertices = new ArrayList<Integer>();
		for (IntCursor intcur : g.getVertices()) {
			vertices.add(intcur.value);
		}
		System.out.println(Utils.largeCollectionToString(vertices));
		Collections.sort(vertices, new algorithm.GreaterByN1BComparator(g));
		System.out.println(Utils.largeCollectionToString(vertices));

		long height = Math.round(Math.log(maxCores) / Math.log(2));
		LinkedList<Integer> ver2 = new LinkedList<>();
		for (int i = 0; i < height; i++) {
			ver2.addFirst(vertices.get(vertices.size() - 1));
			vertices.remove(vertices.size() - 1);
		}
		ArrayList<Integer> ver3 = new ArrayList<>(ver2);
		System.out.println(Utils.largeCollectionToString(ver3));

		fillPool(pool, ver3, new LinkedHashSet<Integer>(), g, vertices);

		for (Thread t : pool) {
			t.start();
		}

		for (Thread t : pool) {
			try {
				t.join();
				synchronized (isCreatingNewThread) {
					currentPoolCores--;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		runTime = System.currentTimeMillis() - start;
		MDSResultBackedByIntOpenHashSet result = new MDSResultBackedByIntOpenHashSet();
		IntOpenHashSet resultData = new IntOpenHashSet(this.result.size());
		for (Integer i : this.result) {
			resultData.add(i);
		}
		result.setResult(resultData);
		return result;
	}

	@Override
	public long getLastPrepTime() {
		return 0;
	}

	@Override
	public long getLastRunTime() {
		return runTime;
	}
}
